package com.hooksniff.kotlin

/**
 * Manage operational webhook endpoints and view delivery logs.
 */
class OperationalWebhook(private val client: HookSniffHttpClient) {

    /** List all operational webhook endpoints. */
    suspend fun list(): List<Map<String, Any?>> {
        val url = client.newUrlBuilder().encodedPath("/v1/operational-webhooks")
        return client.executeRequest<Any, List<Map<String, Any?>>>("GET", url.build())
    }

    /** Create a new operational webhook endpoint. */
    suspend fun create(body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/operational-webhooks")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("POST", url.build(), reqBody = body)
    }

    /** Get an operational webhook endpoint by ID. */
    suspend fun get(id: String): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/operational-webhooks/$id")
        return client.executeRequest<Any, Map<String, Any?>>("GET", url.build())
    }

    /** Update an operational webhook endpoint. */
    suspend fun update(id: String, body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/operational-webhooks/$id")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("PUT", url.build(), reqBody = body)
    }

    /** Delete an operational webhook endpoint. */
    suspend fun delete(id: String) {
        val url = client.newUrlBuilder().encodedPath("/v1/operational-webhooks/$id")
        client.executeRequest<Any, Boolean>("DELETE", url.build())
    }

    /** List delivery logs for an operational webhook endpoint. */
    suspend fun listDeliveries(id: String): List<Map<String, Any?>> {
        val url = client.newUrlBuilder().encodedPath("/v1/operational-webhooks/$id/deliveries")
        return client.executeRequest<Any, List<Map<String, Any?>>>("GET", url.build())
    }
}
