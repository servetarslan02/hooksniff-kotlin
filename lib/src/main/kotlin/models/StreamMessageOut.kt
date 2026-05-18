package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class StreamMessageOut(
    val id: String? = null,
    val channelId: String? = null,
    val eventType: String? = null,
    val payload: Map<String, kotlinx.serialization.json.JsonElement>? = null,
    val deliveredCount: Int = 0,
    val createdAt: String? = null,
)
