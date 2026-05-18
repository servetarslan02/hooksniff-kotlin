package com.hooksniff.kotlin

import com.hooksniff.kotlin.exceptions.WebhookSigningException
import com.hooksniff.kotlin.exceptions.WebhookVerificationException
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Webhook {
    private val key: ByteArray

    /**
     * Verify a webhook signature using java.net.http.HttpHeaders.
     */
    @Throws(WebhookVerificationException::class)
    fun verify(payload: String?, headers: java.net.http.HttpHeaders) {
        var msgId = headers.firstValue(HOOKSNIFF_MSG_ID_KEY)
        var msgSignature = headers.firstValue(HOOKSNIFF_MSG_SIGNATURE_KEY)
        var msgTimestamp = headers.firstValue(HOOKSNIFF_MSG_TIMESTAMP_KEY)
        if (msgId.isEmpty || msgSignature.isEmpty || msgTimestamp.isEmpty) {
            msgId = headers.firstValue(UNBRANDED_MSG_ID_KEY)
            msgSignature = headers.firstValue(UNBRANDED_MSG_SIGNATURE_KEY)
            msgTimestamp = headers.firstValue(UNBRANDED_MSG_TIMESTAMP_KEY)
            if (msgId.isEmpty || msgSignature.isEmpty || msgTimestamp.isEmpty) {
                throw WebhookVerificationException("Missing required headers")
            }
        }
        verifyInternal(msgId.get(), msgSignature.get(), msgTimestamp.get(), payload)
    }

    /**
     * Verify a webhook signature using a Map<String, String>.
     */
    @Throws(WebhookVerificationException::class)
    fun verify(payload: String?, headers: Map<String, String>) {
        var msgId = headers[HOOKSNIFF_MSG_ID_KEY]
        var msgSignature = headers[HOOKSNIFF_MSG_SIGNATURE_KEY]
        var msgTimestamp = headers[HOOKSNIFF_MSG_TIMESTAMP_KEY]
        if (msgId == null || msgSignature == null || msgTimestamp == null) {
            msgId = headers[UNBRANDED_MSG_ID_KEY]
            msgSignature = headers[UNBRANDED_MSG_SIGNATURE_KEY]
            msgTimestamp = headers[UNBRANDED_MSG_TIMESTAMP_KEY]
            if (msgId == null || msgSignature == null || msgTimestamp == null) {
                throw WebhookVerificationException("Missing required headers")
            }
        }
        verifyInternal(msgId, msgSignature, msgTimestamp, payload)
    }

    /**
     * Verify and parse a webhook payload using java.net.http.HttpHeaders.
     *
     * Returns a [WebhookEvent] with typed fields: `event`, `data`, `timestamp`.
     */
    @Throws(WebhookVerificationException::class)
    fun verifyAndParse(payload: String?, headers: java.net.http.HttpHeaders): WebhookEvent {
        verify(payload, headers)
        return parsePayload(payload)
    }

    /**
     * Verify and parse a webhook payload using a Map<String, String>.
     *
     * Returns a [WebhookEvent] with typed fields: `event`, `data`, `timestamp`.
     */
    @Throws(WebhookVerificationException::class)
    fun verifyAndParse(payload: String?, headers: Map<String, String>): WebhookEvent {
        verify(payload, headers)
        return parsePayload(payload)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parsePayload(payload: String?): WebhookEvent {
        if (payload.isNullOrBlank()) {
            return WebhookEvent(event = "", data = emptyMap(), timestamp = "")
        }
        return try {
            // Use basic JSON parsing without external dependency
            val parsed = parseJsonString(payload)
            val event = parsed["event"]?.toString() ?: parsed["eventType"]?.toString() ?: ""
            val data = when (val d = parsed["data"]) {
                is Map<*, *> -> d as Map<String, Any?>
                else -> emptyMap()
            }
            val timestamp = parsed["timestamp"]?.toString() ?: ""
            WebhookEvent(event = event, data = data, timestamp = timestamp)
        } catch (e: Exception) {
            WebhookEvent(event = "", data = emptyMap(), timestamp = "")
        }
    }

    private fun parseJsonString(json: String): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        val trimmed = json.trim()
        if (!trimmed.startsWith("{")) return result

        // Extract simple string fields
        extractString(trimmed, "event")?.let { result["event"] = it }
        extractString(trimmed, "eventType")?.let { result["eventType"] = it }
        extractString(trimmed, "timestamp")?.let { result["timestamp"] = it }

        // Extract data as a map
        val dataIndex = trimmed.indexOf("\"data\"")
        if (dataIndex >= 0) {
            val colonIndex = trimmed.indexOf(":", dataIndex + 6)
            if (colonIndex >= 0) {
                val braceStart = trimmed.indexOf("{", colonIndex)
                if (braceStart >= 0) {
                    var depth = 0
                    var end = braceStart
                    for (i in braceStart until trimmed.length) {
                        if (trimmed[i] == '{') depth++
                        if (trimmed[i] == '}') depth--
                        if (depth == 0) { end = i + 1; break }
                    }
                    result["data"] = parseNestedJson(trimmed.substring(braceStart, end))
                }
            }
        }

        return result
    }

    private fun extractString(json: String, key: String): String? {
        val search = "\"$key\""
        val index = json.indexOf(search)
        if (index < 0) return null

        val colonIndex = json.indexOf(":", index + search.length)
        if (colonIndex < 0) return null

        val start = json.indexOf("\"", colonIndex + 1)
        if (start < 0) return null

        val sb = StringBuilder()
        var i = start + 1
        while (i < json.length) {
            if (json[i] == '\\') { i += 2; continue }
            if (json[i] == '"') break
            sb.append(json[i])
            i++
        }
        return sb.toString()
    }

    private fun parseNestedJson(json: String): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        val trimmed = json.trim()
        if (!trimmed.startsWith("{")) return result

        var i = 1
        while (i < trimmed.length - 1) {
            while (i < trimmed.length && trimmed[i].isWhitespace()) i++
            if (i >= trimmed.length - 1 || trimmed[i] == '}') break

            if (trimmed[i] != '"') { i++; continue }
            i++
            val keyStart = i
            while (i < trimmed.length && trimmed[i] != '"') {
                if (trimmed[i] == '\\') i++
                i++
            }
            val key = trimmed.substring(keyStart, i)
            i++

            while (i < trimmed.length && (trimmed[i] == ':' || trimmed[i].isWhitespace())) i++

            if (i >= trimmed.length) break
            when (trimmed[i]) {
                '"' -> {
                    i++
                    val valStart = i
                    while (i < trimmed.length) {
                        if (trimmed[i] == '\\') { i += 2; continue }
                        if (trimmed[i] == '"') break
                        i++
                    }
                    result[key] = trimmed.substring(valStart, i)
                    i++
                }
                '{' -> {
                    var depth = 0; val start = i
                    for (j in i until trimmed.length) {
                        if (trimmed[j] == '{') depth++
                        if (trimmed[j] == '}') depth--
                        if (depth == 0) { i = j + 1; break }
                    }
                    result[key] = parseNestedJson(trimmed.substring(start, i))
                }
                '[' -> {
                    var depth = 0
                    for (j in i until trimmed.length) {
                        if (trimmed[j] == '[') depth++
                        if (trimmed[j] == ']') depth--
                        if (depth == 0) { i = j + 1; break }
                    }
                }
                't', 'f' -> {
                    result[key] = trimmed.startsWith("true", i)
                    i += if (trimmed.startsWith("true", i)) 4 else 5
                }
                'n' -> { result[key] = null; i += 4 }
                else -> {
                    val start = i
                    while (i < trimmed.length && ",} ]".indexOf(trimmed[i]) < 0) i++
                    val numStr = trimmed.substring(start, i).trim()
                    result[key] = numStr.toLongOrNull() ?: numStr.toDoubleOrNull() ?: numStr
                }
            }

            while (i < trimmed.length && (trimmed[i] == ',' || trimmed[i].isWhitespace())) i++
        }

        return result
    }

    private fun verifyInternal(msgId: String, msgSignature: String, msgTimestamp: String, payload: String?) {
        val timestamp = verifyTimestamp(msgTimestamp)
        val expectedSignature: String =
            try {
                sign(msgId, timestamp, payload).split(",".toRegex()).toTypedArray()[1]
            } catch (e: WebhookSigningException) {
                throw WebhookVerificationException("Failed to generate expected signature")
            }
        val msgSignatures = msgSignature.split(" ".toRegex()).toTypedArray()
        for (versionedSignature in msgSignatures) {
            val sigParts = versionedSignature.split(",".toRegex()).toTypedArray()
            if (sigParts.size < 2) {
                continue
            }
            val version = sigParts[0]
            if (version != "v1") {
                continue
            }
            val signature = sigParts[1]
            if (MessageDigest.isEqual(signature.toByteArray(), expectedSignature.toByteArray())) {
                return
            }
        }
        throw WebhookVerificationException("No matching signature found")
    }

    @Throws(WebhookSigningException::class)
    fun sign(msgId: String?, timestamp: Long, payload: String?): String {
        return try {
            val toSign = String.format("%s.%s.%s", msgId, timestamp, payload)
            val sha512Hmac: Mac = Mac.getInstance(HMAC_SHA256)
            val keySpec = SecretKeySpec(key, HMAC_SHA256)
            sha512Hmac.init(keySpec)
            val macData: ByteArray = sha512Hmac.doFinal(toSign.toByteArray(StandardCharsets.UTF_8))
            val signature = Base64.getEncoder().encodeToString(macData)
            String.format("v1,%s", signature)
        } catch (e: InvalidKeyException) {
            throw WebhookSigningException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw WebhookSigningException(e)
        }
    }

    companion object {
        const val SECRET_PREFIX = "whsec_"
        const val HOOKSNIFF_MSG_ID_KEY = "hooksniff-id"
        const val HOOKSNIFF_MSG_SIGNATURE_KEY = "hooksniff-signature"
        const val HOOKSNIFF_MSG_TIMESTAMP_KEY = "hooksniff-timestamp"
        const val UNBRANDED_MSG_ID_KEY = "webhook-id"
        const val UNBRANDED_MSG_SIGNATURE_KEY = "webhook-signature"
        const val UNBRANDED_MSG_TIMESTAMP_KEY = "webhook-timestamp"
        private const val HMAC_SHA256 = "HmacSHA256"
        private const val TOLERANCE_IN_SECONDS = 5 * 60 // 5 minutes
        private const val SECOND_IN_MS = 1000L

        @Throws(WebhookVerificationException::class)
        private fun verifyTimestamp(timestampHeader: String): Long {
            val now: Long = System.currentTimeMillis() / SECOND_IN_MS
            val timestamp: Long =
                try {
                    timestampHeader.toLong()
                } catch (e: NumberFormatException) {
                    throw WebhookVerificationException("Invalid Signature Headers")
                }

            if (timestamp < now - TOLERANCE_IN_SECONDS) {
                throw WebhookVerificationException("Message timestamp too old")
            }
            if (timestamp > now + TOLERANCE_IN_SECONDS) {
                throw WebhookVerificationException("Message timestamp too new")
            }
            return timestamp
        }
    }

    constructor(secret: String) {
        var sec = secret
        if (sec.startsWith(SECRET_PREFIX)) {
            sec = sec.substring(SECRET_PREFIX.length)
        }
        key = Base64.getDecoder().decode(sec)
    }

    constructor(secret: ByteArray) {
        key = secret
    }
}
