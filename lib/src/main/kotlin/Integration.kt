package com.hooksniff.kotlin

import com.hooksniff.kotlin.models.IntegrationEventOut
import com.hooksniff.kotlin.models.IntegrationIn
import com.hooksniff.kotlin.models.IntegrationOut
import com.hooksniff.kotlin.models.IntegrationStatsOut
import com.hooksniff.kotlin.models.IntegrationTestResponse
import com.hooksniff.kotlin.models.IntegrationUpdate

class Integration(private val client: HookSniffHttpClient) {
    /** List all integrations. */
    suspend fun list(): List<IntegrationOut> {
        val url = client.newUrlBuilder().encodedPath("/v1/integrations")
        return client.executeRequest<Any, List<IntegrationOut>>("GET", url.build())
    }

    /** Get an integration by ID. */
    suspend fun get(id: String): IntegrationOut {
        val url = client.newUrlBuilder().encodedPath("/v1/integrations/$id")
        return client.executeRequest<Any, IntegrationOut>("GET", url.build())
    }

    /** Create a new integration. */
    suspend fun create(integrationIn: IntegrationIn): IntegrationOut {
        val url = client.newUrlBuilder().encodedPath("/v1/integrations")
        return client.executeRequest<IntegrationIn, IntegrationOut>("POST", url.build(), reqBody = integrationIn)
    }

    /** Update an integration. */
    suspend fun update(id: String, integrationUpdate: IntegrationUpdate): IntegrationOut {
        val url = client.newUrlBuilder().encodedPath("/v1/integrations/$id")
        return client.executeRequest<IntegrationUpdate, IntegrationOut>("PUT", url.build(), reqBody = integrationUpdate)
    }

    /** Delete an integration. */
    suspend fun delete(id: String) {
        val url = client.newUrlBuilder().encodedPath("/v1/integrations/$id")
        client.executeRequest<Any, Boolean>("DELETE", url.build())
    }

    /** Test an integration. */
    suspend fun test(id: String): IntegrationTestResponse {
        val url = client.newUrlBuilder().encodedPath("/v1/integrations/$id/test")
        return client.executeRequest<Any, IntegrationTestResponse>("POST", url.build())
    }

    /** List events for an integration. */
    suspend fun listEvents(id: String): List<IntegrationEventOut> {
        val url = client.newUrlBuilder().encodedPath("/v1/integrations/$id/events")
        return client.executeRequest<Any, List<IntegrationEventOut>>("GET", url.build())
    }

    /** Get stats for an integration. */
    suspend fun getStats(id: String): IntegrationStatsOut {
        val url = client.newUrlBuilder().encodedPath("/v1/integrations/$id/stats")
        return client.executeRequest<Any, IntegrationStatsOut>("GET", url.build())
    }
}
