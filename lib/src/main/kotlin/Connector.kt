package com.hooksniff.kotlin

/**
 * Manage connectors (Shopify, Stripe, etc.) and their configurations.
 */
class Connector(private val client: HookSniffHttpClient) {

    /** List all available connectors. */
    suspend fun list(): List<Map<String, Any?>> {
        val url = client.newUrlBuilder().encodedPath("/v1/connectors")
        return client.executeRequest<Any, List<Map<String, Any?>>>("GET", url.build())
    }

    /** Get a connector by ID. */
    suspend fun get(id: String): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/connectors/$id")
        return client.executeRequest<Any, Map<String, Any?>>("GET", url.build())
    }

    /** List all connector configurations. */
    suspend fun listConfigs(): List<Map<String, Any?>> {
        val url = client.newUrlBuilder().encodedPath("/v1/connectors/configs")
        return client.executeRequest<Any, List<Map<String, Any?>>>("GET", url.build())
    }

    /** Create a new connector configuration. */
    suspend fun createConfig(body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/connectors/configs")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("POST", url.build(), reqBody = body)
    }

    /** Update a connector configuration. */
    suspend fun updateConfig(id: String, body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/connectors/configs/$id")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("PUT", url.build(), reqBody = body)
    }

    /** Delete a connector configuration. */
    suspend fun deleteConfig(id: String) {
        val url = client.newUrlBuilder().encodedPath("/v1/connectors/configs/$id")
        client.executeRequest<Any, Boolean>("DELETE", url.build())
    }
}
