package com.hooksniff.kotlin

import com.hooksniff.kotlin.exceptions.ApiException
import kotlin.random.Random
import kotlin.random.nextULong
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

data class ResponseMetadata(val statusCode: Int, val requestId: String? = null, val rateLimitRemaining: Int? = null, val rateLimitReset: Int? = null, val headers: Map<String, List<String>> = emptyMap())

open class HookSniffHttpClient
internal constructor(
    private val baseUrl: HttpUrl,
    private val defaultHeaders: Map<String, String>,
    private val retrySchedule: List<Long>,
    private val jsonDeserializer: Json = Json { ignoreUnknownKeys = true },
    private val debug: Boolean = false
) {
    private val client: OkHttpClient = OkHttpClient()

    fun newUrlBuilder(): HttpUrl.Builder {
        return HttpUrl.Builder().scheme(baseUrl.scheme).host(baseUrl.host).port(baseUrl.port)
    }

    internal suspend inline fun <reified Req, reified Res> executeRequest(
        method: String,
        url: HttpUrl,
        headers: Headers? = null,
        reqBody: Req? = null,
    ): Res {
        val reqBuilder = Request.Builder().url(url)
        if (reqBody != null) {
            reqBuilder.method(method, Json.encodeToString(reqBody).toRequestBody())
        } else {
            reqBuilder.method(method, null)
        }

        for ((k, v) in defaultHeaders) {
            reqBuilder.addHeader(k, v)
        }
        if (headers != null) {
            for ((k, v) in headers) {
                reqBuilder.addHeader(k, v)
            }
        }

        if (headers?.get("idempotency-key") == null && method == "POST") {
            val uuid = UUID.randomUUID().toString()
            reqBuilder.addHeader("idempotency-key", "auto_" + uuid)
        }

        reqBuilder.addHeader("hooksniff-req-id", Random.nextULong().toString())

        val request = reqBuilder.build()
        val res = executeRequestWithRetry(request)

        // if body is null panic
        if (res.body == null) {
            throw ApiException("Body is null", res.code)
        }
        val bodyString = res.body!!.string()
        if (res.code == 204) {
            return jsonDeserializer.decodeFromString<Res>("true")
        }
        if (res.code in 200..299) {
            return jsonDeserializer.decodeFromString<Res>(bodyString)
        }
        throw ApiException("Non 200 status code ${res.code}", res.code, bodyString)
    }

    suspend fun executeRequestWithRetry(request: Request): Response {
        var currentRequest = request
        var retryCount = 0

        if (debug) {
            println("[HookSniff] → ${request.method} ${request.url}")
        }
        val startTime = System.currentTimeMillis()

        while (retryCount <= retrySchedule.size) {
            val res = try {
                client.newCall(currentRequest).execute()
            } catch (e: java.net.SocketTimeoutException) {
                // Timeout — retry with backoff
                if (retryCount >= retrySchedule.size) throw e
                val delayMs = retrySchedule[retryCount]
                delay(delayMs)
                currentRequest = request.newBuilder()
                    .header("hooksniff-retry-count", (retryCount + 1).toString())
                    .build()
                retryCount++
                continue
            } catch (e: java.net.SocketException) {
                if (retryCount >= retrySchedule.size) throw e
                val delayMs = retrySchedule[retryCount]
                delay(delayMs)
                currentRequest = request.newBuilder()
                    .header("hooksniff-retry-count", (retryCount + 1).toString())
                    .build()
                retryCount++
                continue
            }

            // 429 Rate Limit — respect Retry-After header
            if (res.code == 429 && retryCount < retrySchedule.size) {
                val retryAfter = res.header("Retry-After")
                val delayMs = retryAfter?.toLongOrNull()?.times(1000) ?: retrySchedule[retryCount]
                res.close()
                delay(delayMs)
                currentRequest = request.newBuilder()
                    .header("hooksniff-retry-count", (retryCount + 1).toString())
                    .build()
                retryCount++
                continue
            }

            // 5xx Server Error — exponential backoff
            if (res.code >= 500 && retryCount < retrySchedule.size) {
                res.close()
                delay(retrySchedule[retryCount])
                currentRequest = request.newBuilder()
                    .header("hooksniff-retry-count", (retryCount + 1).toString())
                    .build()
                retryCount++
                continue
            }

            return res
        }

        val finalResponse = client.newCall(currentRequest).execute()
        if (debug) {
            val elapsed = System.currentTimeMillis() - startTime
            println("[HookSniff] ← ${finalResponse.code} (${elapsed}ms)")
        }
        return finalResponse
    }
}
