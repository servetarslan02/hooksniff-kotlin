package com.hooksniff

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class WebhookVerificationError(message: String) : Exception("Webhook verification failed: $message")

class Webhook(private val secret: String) {
    init {
        require(secret.isNotEmpty()) { "Webhook secret is required" }
    }

    fun verify(payload: String, headers: Map<String, String>): JsonElement {
        val normalized = headers.mapKeys { it.key.lowercase() }

        val msgId = normalized["webhook-id"]
            ?: throw WebhookVerificationError("missing required webhook headers (webhook-id, webhook-signature, webhook-timestamp)")
        val msgSignature = normalized["webhook-signature"]
            ?: throw WebhookVerificationError("missing required webhook headers (webhook-id, webhook-signature, webhook-timestamp)")
        val msgTimestamp = normalized["webhook-timestamp"]
            ?: throw WebhookVerificationError("missing required webhook headers (webhook-id, webhook-signature, webhook-timestamp)")

        val timestamp = msgTimestamp.toLongOrNull()
            ?: throw WebhookVerificationError("invalid webhook timestamp")

        val now = System.currentTimeMillis() / 1000
        if (kotlin.math.abs(now - timestamp) > 300) {
            throw WebhookVerificationError("webhook timestamp is too old")
        }

        val toSign = "$msgId.$msgTimestamp.$payload"
        val expectedSignature = sign(toSign)

        val signatures = msgSignature.split(" ")
        val isValid = signatures.any { sig ->
            val parts = sig.split(",", limit = 2)
            parts.size == 2 && parts[0] == "v1" && constantTimeEquals(parts[1], expectedSignature)
        }

        if (!isValid) {
            throw WebhookVerificationError("invalid webhook signature")
        }

        return Json.parseToJsonElement(payload)
    }

    private fun sign(content: String): String {
        val secretBytes = try {
            Base64.getDecoder().decode(secret.removePrefix("whsec_"))
        } catch (_: Exception) {
            secret.removePrefix("whsec_").toByteArray()
        }
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secretBytes, "HmacSHA256"))
        return Base64.getEncoder().encodeToString(mac.doFinal(content.toByteArray()))
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }
}
