package com.hooksniff

import kotlinx.serialization.json.*

// ── Application ──────────────────────────────────────────────
class ApplicationResource internal constructor(private val http: HttpTransport) {
    fun create(params: ApplicationCreate): Application = jsonTo(http.request("POST", "/v1/applications", params))
    fun list(perPage: Int = 50): Paginator = Paginator(http, "/v1/applications", perPage)
    fun get(id: String): Application = jsonTo(http.request("GET", "/v1/applications/$id"))
    fun update(id: String, params: ApplicationUpdate): Application = jsonTo(http.request("PUT", "/v1/applications/$id", params))
    fun delete(id: String) { http.request("DELETE", "/v1/applications/$id") }
}

// ── Endpoint ─────────────────────────────────────────────────
class EndpointResource internal constructor(private val http: HttpTransport) {
    fun create(params: EndpointCreate): Endpoint = jsonTo(http.request("POST", "/v1/endpoints", params))
    fun list(perPage: Int = 50): Paginator = Paginator(http, "/v1/endpoints", perPage)
    fun get(id: String): Endpoint = jsonTo(http.request("GET", "/v1/endpoints/$id"))
    fun update(id: String, params: EndpointUpdate): Endpoint = jsonTo(http.request("PUT", "/v1/endpoints/$id", params))
    fun delete(id: String) { http.request("DELETE", "/v1/endpoints/$id") }
    fun rotateSecret(id: String): SecretRotateResponse = jsonTo(http.request("POST", "/v1/endpoints/$id/rotate-secret"))
}

// ── Webhook ──────────────────────────────────────────────────
class WebhookResource internal constructor(private val http: HttpTransport) {
    fun send(params: WebhookSend, idempotencyKey: String? = null): WebhookDelivery =
        jsonTo(http.request("POST", "/v1/webhooks", params, idempotencyKey))
    fun sendBatch(webhooks: List<WebhookSend>, idempotencyKey: String? = null): List<WebhookDelivery> {
        val resp = http.request("POST", "/v1/webhooks/batch", mapOf("webhooks" to webhooks), idempotencyKey)
        return resp.jsonObject["deliveries"]?.jsonArray?.map { jsonTo<WebhookDelivery>(it) } ?: emptyList()
    }
    fun list(perPage: Int = 50): Paginator = Paginator(http, "/v1/webhooks", perPage)
    fun get(id: String): WebhookDelivery = jsonTo(http.request("GET", "/v1/webhooks/$id"))
    fun replay(id: String): WebhookDelivery = jsonTo(http.request("POST", "/v1/webhooks/$id/replay"))
    fun batchReplay(ids: List<String>): Int {
        val resp = http.request("POST", "/v1/webhooks/batch-replay", mapOf("webhook_ids" to ids))
        return resp.jsonObject["replayed"]?.jsonPrimitive?.int ?: 0
    }
}

// ── ApiKey ───────────────────────────────────────────────────
class ApiKeyResource internal constructor(private val http: HttpTransport) {
    fun list(): List<ApiKey> = jsonTo(http.request("GET", "/v1/api-keys"))
    fun create(name: String): ApiKeyCreated = jsonTo(http.request("POST", "/v1/api-keys", mapOf("name" to name)))
    fun delete(id: String) { http.request("DELETE", "/v1/api-keys/$id") }
    fun rotate(id: String): ApiKeyCreated = jsonTo(http.request("POST", "/v1/api-keys/$id/rotate"))
}

// ── Analytics ────────────────────────────────────────────────
class AnalyticsResource internal constructor(private val http: HttpTransport) {
    fun deliveries(range: String = "24h"): JsonElement = http.request("GET", "/v1/analytics/deliveries?range=$range")
    fun successRate(range: String = "24h"): JsonElement = http.request("GET", "/v1/analytics/success-rate?range=$range")
    fun latency(range: String = "24h"): JsonElement = http.request("GET", "/v1/analytics/latency?range=$range")
}

// ── Search ───────────────────────────────────────────────────
class SearchResource internal constructor(private val http: HttpTransport) {
    fun deliveries(query: String, page: Int = 1, perPage: Int = 20): SearchResult =
        jsonTo(http.request("GET", "/v1/search?q=$query&page=$page&per_page=$perPage"))
}

// ── Health ───────────────────────────────────────────────────
class HealthResource internal constructor(private val http: HttpTransport) {
    fun check(): HealthResponse = jsonTo(http.request("GET", "/health"))
    fun outboundIPs(): OutboundIPs = jsonTo(http.request("GET", "/v1/outbound-ips"))
}

// ── Team ─────────────────────────────────────────────────────
class TeamResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/teams")
    fun create(name: String, description: String? = null): JsonElement =
        http.request("POST", "/v1/teams", buildMap { put("name", name); description?.let { put("description", it) } })
    fun get(id: String): JsonElement = http.request("GET", "/v1/teams/$id")
    fun delete(id: String) { http.request("DELETE", "/v1/teams/$id") }
    fun listMembers(teamId: String): JsonElement = http.request("GET", "/v1/teams/$teamId/members")
    fun inviteMember(teamId: String, email: String, role: String = "viewer"): JsonElement =
        http.request("POST", "/v1/teams/$teamId/members", mapOf("email" to email, "role" to role))
    fun removeMember(teamId: String, memberId: String) { http.request("DELETE", "/v1/teams/$teamId/members/$memberId") }
}

// ── Billing ──────────────────────────────────────────────────
class BillingResource internal constructor(private val http: HttpTransport) {
    fun subscription(): Subscription = jsonTo(http.request("GET", "/v1/billing/subscription"))
    fun upgrade(plan: String): JsonElement = http.request("POST", "/v1/billing/upgrade", mapOf("plan" to plan))
    fun portal(): JsonElement = http.request("POST", "/v1/billing/portal")
    fun cancel(): JsonElement = http.request("POST", "/v1/billing/cancel")
    fun usage(): JsonElement = http.request("GET", "/v1/billing/usage")
    fun invoices(): JsonElement = http.request("GET", "/v1/billing/invoices")
}

// ── Notification ─────────────────────────────────────────────
class NotificationResource internal constructor(private val http: HttpTransport) {
    fun list(perPage: Int = 20): JsonElement = http.request("GET", "/v1/notifications?per_page=$perPage")
    fun getUnreadCount(): Int {
        val resp = http.request("GET", "/v1/notifications/unread-count")
        return resp.jsonObject["unread_count"]?.jsonPrimitive?.int ?: 0
    }
    fun markRead(id: String) { http.request("POST", "/v1/notifications/$id/read") }
    fun markAllRead() { http.request("POST", "/v1/notifications/read-all") }
}

// ── Cortex ───────────────────────────────────────────────────
class CortexResource internal constructor(private val http: HttpTransport) {
    fun insights(): JsonElement = http.request("GET", "/v1/cortex/insights")
    fun anomalies(endpointId: String? = null): JsonElement =
        http.request("GET", "/v1/cortex/anomalies" + (endpointId?.let { "?endpoint_id=$it" } ?: ""))
    fun predict(endpointId: String): JsonElement = http.request("GET", "/v1/cortex/predict/$endpointId")
    fun autoHeal(endpointId: String): JsonElement = http.request("POST", "/v1/cortex/auto-heal/$endpointId")
}

// ── Alert ────────────────────────────────────────────────────
class AlertResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/alerts")
    fun create(name: String, condition: String, threshold: Int, channels: List<String>): JsonElement =
        http.request("POST", "/v1/alerts", mapOf("name" to name, "condition" to condition, "threshold" to threshold, "channels" to channels))
    fun get(id: String): JsonElement = http.request("GET", "/v1/alerts/$id")
    fun update(id: String, params: Map<String, Any?>): JsonElement = http.request("PUT", "/v1/alerts/$id", params)
    fun delete(id: String) { http.request("DELETE", "/v1/alerts/$id") }
    fun listEvents(alertId: String): JsonElement = http.request("GET", "/v1/alerts/$alertId/events")
}

// ── Template ─────────────────────────────────────────────────
class TemplateResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/templates")
    fun create(name: String, content: String, description: String? = null): JsonElement =
        http.request("POST", "/v1/templates", buildMap { put("name", name); put("content", content); description?.let { put("description", it) } })
    fun get(id: String): JsonElement = http.request("GET", "/v1/templates/$id")
    fun update(id: String, params: Map<String, Any?>): JsonElement = http.request("PUT", "/v1/templates/$id", params)
    fun delete(id: String) { http.request("DELETE", "/v1/templates/$id") }
}

// ── Schema ───────────────────────────────────────────────────
class SchemaResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/schemas")
    fun create(name: String, schema: Map<String, Any?>): JsonElement =
        http.request("POST", "/v1/schemas", mapOf("name" to name, "schema" to schema))
    fun get(id: String): JsonElement = http.request("GET", "/v1/schemas/$id")
    fun delete(id: String) { http.request("DELETE", "/v1/schemas/$id") }
    fun validate(id: String, data: Any): JsonElement = http.request("POST", "/v1/schemas/$id/validate", mapOf("data" to data))
}

// ── Connector ────────────────────────────────────────────────
class ConnectorResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/connectors")
    fun get(id: String): JsonElement = http.request("GET", "/v1/connectors/$id")
    fun listConfigs(): JsonElement = http.request("GET", "/v1/connectors/configs")
    fun createConfig(connectorId: String, name: String, config: Map<String, Any?>): JsonElement =
        http.request("POST", "/v1/connectors/configs", mapOf("connector_id" to connectorId, "name" to name, "config" to config))
    fun getConfig(id: String): JsonElement = http.request("GET", "/v1/connectors/configs/$id")
    fun updateConfig(id: String, params: Map<String, Any?>): JsonElement = http.request("PUT", "/v1/connectors/configs/$id", params)
    fun deleteConfig(id: String) { http.request("DELETE", "/v1/connectors/configs/$id") }
}

// ── Stream ───────────────────────────────────────────────────
class StreamResource internal constructor(private val http: HttpTransport) {
    fun listChannels(): JsonElement = http.request("GET", "/v1/stream/channels")
    fun createChannel(name: String, description: String? = null): JsonElement =
        http.request("POST", "/v1/stream/channels", buildMap { put("name", name); description?.let { put("description", it) } })
    fun getChannel(id: String): JsonElement = http.request("GET", "/v1/stream/channels/$id")
    fun deleteChannel(id: String) { http.request("DELETE", "/v1/stream/channels/$id") }
    fun listMessages(channelId: String): JsonElement = http.request("GET", "/v1/stream/channels/$channelId/messages")
    fun publish(channelId: String, event: String, data: Map<String, Any?>): JsonElement =
        http.request("POST", "/v1/stream/publish", mapOf("channel_id" to channelId, "event" to event, "data" to data))
    fun listSubscriptions(): JsonElement = http.request("GET", "/v1/stream/subscriptions")
    fun getSubscription(id: String): JsonElement = http.request("GET", "/v1/stream/subscriptions/$id")
    fun disconnectSubscription(id: String) { http.request("DELETE", "/v1/stream/subscriptions/$id") }
}

// ── BackgroundTask ───────────────────────────────────────────
class BackgroundTaskResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/background-tasks")
    fun get(id: String): JsonElement = http.request("GET", "/v1/background-tasks/$id")
    fun cancel(id: String) { http.request("POST", "/v1/background-tasks/$id/cancel") }
}

// ── Integration ──────────────────────────────────────────────
class IntegrationResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/integrations")
    fun get(id: String): JsonElement = http.request("GET", "/v1/integrations/$id")
    fun delete(id: String) { http.request("DELETE", "/v1/integrations/$id") }
    fun rotateKey(id: String): JsonElement = http.request("POST", "/v1/integrations/$id/rotate-key")
}

// ── ServiceToken ─────────────────────────────────────────────
class ServiceTokenResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/service-tokens")
    fun create(name: String): JsonElement = http.request("POST", "/v1/service-tokens", mapOf("name" to name))
    fun delete(id: String) { http.request("DELETE", "/v1/service-tokens/$id") }
}

// ── OperationalWebhook ───────────────────────────────────────
class OperationalWebhookResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/operational-webhooks")
    fun create(url: String, events: List<String>): JsonElement =
        http.request("POST", "/v1/operational-webhooks", mapOf("url" to url, "events" to events))
    fun get(id: String): JsonElement = http.request("GET", "/v1/operational-webhooks/$id")
    fun delete(id: String) { http.request("DELETE", "/v1/operational-webhooks/$id") }
}

// ── RateLimit ────────────────────────────────────────────────
class RateLimitResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/rate-limits")
}

// ── Audit ────────────────────────────────────────────────────
class AuditResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/audit-log")
}

// ── Sso ──────────────────────────────────────────────────────
class SsoResource internal constructor(private val http: HttpTransport) {
    fun getConfig(): JsonElement = http.request("GET", "/v1/sso/config")
}

// ── CustomDomain ─────────────────────────────────────────────
class CustomDomainResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/custom-domains")
}

// ── Environment ──────────────────────────────────────────────
class EnvironmentResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/environments")
}

// ── Broadcast ────────────────────────────────────────────────
class BroadcastResource internal constructor(private val http: HttpTransport) {
    fun list(): JsonElement = http.request("GET", "/v1/broadcasts")
    fun create(title: String, message: String, scheduledAt: String? = null): JsonElement =
        http.request("POST", "/v1/broadcasts", buildMap { put("title", title); put("message", message); scheduledAt?.let { put("scheduled_at", it) } })
    fun get(id: String): JsonElement = http.request("GET", "/v1/broadcasts/$id")
    fun delete(id: String) { http.request("DELETE", "/v1/broadcasts/$id") }
    fun send(id: String) { http.request("POST", "/v1/broadcasts/$id/send") }
}

// ── Transform ────────────────────────────────────────────────
class TransformResource internal constructor(private val http: HttpTransport) {
    fun list(endpointId: String): JsonElement = http.request("GET", "/v1/endpoints/$endpointId/transforms")
    fun create(endpointId: String, name: String, code: String): JsonElement =
        http.request("POST", "/v1/endpoints/$endpointId/transforms", mapOf("name" to name, "code" to code))
    fun get(endpointId: String, id: String): JsonElement = http.request("GET", "/v1/endpoints/$endpointId/transforms/$id")
    fun update(endpointId: String, id: String, params: Map<String, Any?>): JsonElement =
        http.request("PUT", "/v1/endpoints/$endpointId/transforms/$id", params)
    fun delete(endpointId: String, id: String) { http.request("DELETE", "/v1/endpoints/$endpointId/transforms/$id") }
}

// ── Paginator ────────────────────────────────────────────────
class Paginator internal constructor(private val http: HttpTransport, private val path: String, private val perPage: Int = 50) {
    private var page = 1
    private var items = mutableListOf<JsonElement>()
    private var exhausted = false

    fun next(): JsonElement? {
        if (items.isEmpty() && !exhausted) fetchPage()
        if (items.isEmpty()) return null
        return items.removeFirst()
    }

    fun all(): List<JsonElement> {
        val result = mutableListOf<JsonElement>()
        while (true) { result.add(next() ?: break) }
        return result
    }

    private fun fetchPage() {
        val separator = if (path.contains("?")) "&" else "?"
        val resp = http.request("GET", "$path${separator}page=$page&per_page=$perPage")
        val arr = resp.jsonArray
        items = arr.toMutableList()
        if (arr.size < perPage) exhausted = true
        page++
    }
}

// ── Helper ───────────────────────────────────────────────────
inline fun <reified T> jsonTo(element: JsonElement): T =
    kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromJsonElement(element)
