package com.hooksniff

import kotlinx.serialization.json.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.math.pow
import kotlin.random.Random

data class ClientConfig(
    val baseUrl: String = "https://hooksniff-api-499907444852.europe-west1.run.app",
    val timeout: Duration = Duration.ofSeconds(30),
    val retries: Int = 3,
    val headers: Map<String, String> = emptyMap()
)

internal class HttpTransport(private val apiKey: String, config: ClientConfig = ClientConfig()) {
    private val baseUrl = config.baseUrl.trimEnd('/')
    private val client = HttpClient.newBuilder().connectTimeout(config.timeout).build()
    private val timeout = config.timeout
    private val retries = config.retries
    private val extraHeaders = config.headers

    fun request(method: String, path: String, body: Any? = null, idempotencyKey: String? = null): JsonElement {
        val url = "$baseUrl$path"
        var lastError: Exception? = null

        for (attempt in 0..retries) {
            try {
                val builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(timeout)
                    .header("Authorization", "Bearer $apiKey")
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "hooksniff-sdk/kotlin/$SDK_VERSION")

                extraHeaders.forEach { (k, v) -> builder.header(k, v) }
                idempotencyKey?.let { builder.header("Idempotency-Key", it) }

                when (method) {
                    "GET" -> builder.GET()
                    "DELETE" -> builder.DELETE()
                    else -> {
                        val jsonBody = if (body != null) Json.encodeToString(JsonElement.serializer(), toJsonElement(body)) else "{}"
                        builder.method(method, HttpRequest.BodyPublishers.ofString(jsonBody))
                    }
                }

                val response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString())

                if (response.statusCode() in 200..299) {
                    if (response.body().isBlank()) return JsonNull
                    return Json.parseToJsonElement(response.body())
                }

                val errorBody = try {
                    Json.parseToJsonElement(response.body()).jsonObject.toMap().mapValues { toAny(it.value) }
                } catch (_: Exception) { emptyMap() }

                if (response.statusCode() in 400..499 && response.statusCode() != 429 && response.statusCode() != 408) {
                    throw mapError(response.statusCode(), errorBody)
                }

                if (response.statusCode() == 429) {
                    val retryAfter = response.headers().firstValue("Retry-After").orElse("60").toIntOrNull() ?: 60
                    if (attempt < retries) {
                        Thread.sleep(retryAfter * 1000L)
                        continue
                    }
                    throw RateLimitError(
                        (errorBody["error"] as? Map<*, *>)?.get("detail") as? String ?: "Rate limited",
                        retryAfter
                    )
                }

                lastError = mapError(response.statusCode(), errorBody)
                if (attempt < retries) {
                    Thread.sleep((2.0.pow(attempt) * 1000 + Random.nextDouble() * 1000).toLong())
                    continue
                }
                throw lastError
            } catch (e: HookSniffError) {
                throw e
            } catch (e: Exception) {
                lastError = e
                if (attempt < retries) {
                    Thread.sleep((2.0.pow(attempt) * 1000 + Random.nextDouble() * 1000).toLong())
                    continue
                }
            }
        }
        throw lastError ?: Exception("Request failed after retries")
    }

    private fun toJsonElement(obj: Any?): JsonElement = when (obj) {
        null -> JsonNull
        is JsonElement -> obj
        is Map<*, *> -> JsonObject(obj.entries.associate { (k, v) -> k.toString() to toJsonElement(v) })
        is List< *> -> JsonArray(obj.map { toJsonElement(it) })
        is String -> JsonPrimitive(obj)
        is Number -> JsonPrimitive(obj)
        is Boolean -> JsonPrimitive(obj)
        else -> JsonPrimitive(obj.toString())
    }

    private fun toAny(element: JsonElement): Any? = when (element) {
        is JsonNull -> null
        is JsonPrimitive -> if (element.isString) element.content else element.booleanOrNull ?: element.content
        is JsonObject -> element.mapValues { toAny(it.value) }
        is JsonArray -> element.map { toAny(it) }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://hooksniff-api-499907444852.europe-west1.run.app"
        const val SDK_VERSION = "0.5.0"
    }
}
