package com.hooksniff.kotlin

/**
 * Manage inbound webhook configurations and handle inbound webhooks.
 */
class Inbound(private val client: HookSniffHttpClient) {

    /** List all inbound webhook configurations. */
    suspend fun listConfigs(): List<Map<String, Any?>> {
        val url = client.newUrlBuilder().encodedPath("/v1/inbound/configs")
        return client.executeRequest<Any, List<Map<String, Any?>>>("GET", url.build())
    }

    /** Create a new inbound webhook configuration. */
    suspend fun createConfig(body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/inbound/configs")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("POST", url.build(), reqBody = body)
    }

    /** Handle an inbound webhook. */
    suspend fun handleInbound(body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/inbound/configs")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("POST", url.build(), reqBody = body)
    }
}
