package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class StreamChannelOut(
    val id: String? = null,
    val customerId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val channelType: String? = null,
    val eventFilter: List<String>? = null,
    val enabled: Boolean = false,
    val maxSubscribers: Int = 0,
    val currentSubscribers: Int = 0,
    val totalMessages: Long = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
