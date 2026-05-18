package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class PublishEventIn(
    val channelId: String? = null,
    val eventType: String? = null,
    val payload: Map<String, kotlinx.serialization.json.JsonElement>? = null,
)
