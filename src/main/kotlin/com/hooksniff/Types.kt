package com.hooksniff

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable data class Application(val id: String, val customer_id: String, val name: String, val description: String? = null, val is_active: Boolean = true, val created_at: String = "", val updated_at: String = "", val endpoint_count: Int = 0)
@Serializable data class ApplicationCreate(val name: String, val description: String? = null)
@Serializable data class ApplicationUpdate(val name: String? = null, val description: String? = null)

@Serializable data class Endpoint(val id: String, val url: String, val description: String? = null, val is_active: Boolean = true, val application_id: String = "", val routing_strategy: String = "round-robin", val fallback_url: String? = null, val format: String = "standard", val created_at: String = "")
@Serializable data class EndpointCreate(val url: String, val application_id: String, val description: String? = null, val routing_strategy: String? = null, val fallback_url: String? = null)
@Serializable data class EndpointUpdate(val url: String? = null, val description: String? = null, val is_active: Boolean? = null, val routing_strategy: String? = null, val fallback_url: String? = null)

@Serializable data class WebhookDelivery(val id: String, val endpoint_id: String, val event: String, val status: String, val attempt_count: Int = 0, val response_status: Int? = null, val replay_count: Int = 0, val created_at: String = "", val is_test: Boolean = false)
@Serializable data class WebhookSend(val endpoint_id: String, val event: String, val data: JsonElement, val is_test: Boolean? = null)

@Serializable data class ApiKey(val id: String, val name: String, val api_key_prefix: String = "", val created_at: String = "", val last_used_at: String? = null, val is_active: Boolean = true)
@Serializable data class ApiKeyCreated(val id: String, val key: String, val prefix: String = "", val message: String = "")

@Serializable data class User(val id: String, val email: String, val name: String, val plan: String = "developer", val webhook_limit: Int = 0, val webhook_count: Int = 0, val is_admin: Boolean = false, val created_at: String = "")

@Serializable data class SearchResult(val deliveries: List<WebhookDelivery> = emptyList(), val total: Int = 0, val page: Int = 0, val per_page: Int = 0, val query: String = "")

@Serializable data class HealthResponse(val status: String, val api: HealthComponent? = null, val database: HealthComponent? = null, val redis: HealthComponent? = null)
@Serializable data class HealthComponent(val status: String, val latency_ms: Long? = null, val error: String? = null)

@Serializable data class OutboundIPs(val ips: List<String> = emptyList(), val updated_at: String = "")

@Serializable data class SecretRotateResponse(val id: String, val message: String, val old_secret_valid_until: String = "", val signing_secret: String = "")

@Serializable data class Subscription(val plan: String, val status: String, val webhook_limit: Int = 0, val endpoint_limit: Int? = null, val retention_days: Int? = null)

@Serializable data class ListResponse(val data: List<JsonElement> = emptyList(), val total: Int? = null, val page: Int? = null, val per_page: Int? = null)
