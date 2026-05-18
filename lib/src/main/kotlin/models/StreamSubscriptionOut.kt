package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class StreamSubscriptionOut(
    val id: String? = null,
    val channelId: String? = null,
    val customerId: String? = null,
    val connectionType: String? = null,
    val clientId: String? = null,
    val eventFilter: List<String>? = null,
    val connectedAt: String? = null,
    val lastHeartbeatAt: String? = null,
    val messagesSent: Long = 0,
    val metadata: Map<String, kotlinx.serialization.json.JsonElement>? = null,
)
