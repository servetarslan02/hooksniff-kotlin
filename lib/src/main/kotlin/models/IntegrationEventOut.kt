package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class IntegrationEventOut(
    val id: String? = null,
    val integrationId: String? = null,
    val eventType: String? = null,
    val sourceEventId: String? = null,
    val payload: Map<String, kotlinx.serialization.json.JsonElement>? = null,
    val status: String? = null,
    val deliveryId: String? = null,
    val errorMessage: String? = null,
    val attempts: Int = 0,
    val durationMs: Int? = null,
    val createdAt: String? = null,
    val processedAt: String? = null,
)
