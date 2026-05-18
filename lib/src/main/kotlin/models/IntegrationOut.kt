package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class IntegrationOut(
    val id: String? = null,
    val customerId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val connectorConfigId: String? = null,
    val connectorName: String? = null,
    val connectorDisplayName: String? = null,
    val endpointId: String? = null,
    val endpointUrl: String? = null,
    val enabled: Boolean = false,
    val eventFilter: List<String>? = null,
    val transformId: String? = null,
    val retryPolicy: Map<String, kotlinx.serialization.json.JsonElement>? = null,
    val metadata: Map<String, kotlinx.serialization.json.JsonElement>? = null,
    val lastTriggeredAt: String? = null,
    val lastSuccessAt: String? = null,
    val lastFailureAt: String? = null,
    val failureCount: Int = 0,
    val totalDeliveries: Long = 0,
    val totalFailures: Long = 0,
    val healthStatus: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
