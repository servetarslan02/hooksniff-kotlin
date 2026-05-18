package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class IntegrationIn(
    val name: String? = null,
    val description: String? = null,
    val connectorConfigId: String? = null,
    val endpointId: String? = null,
    val eventFilter: List<String>? = null,
    val transformId: String? = null,
    val retryPolicy: Map<String, kotlinx.serialization.json.JsonElement>? = null,
    val metadata: Map<String, kotlinx.serialization.json.JsonElement>? = null,
    val enabled: Boolean? = null,
)
