package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class StreamChannelUpdate(
    val name: String? = null,
    val description: String? = null,
    val eventFilter: List<String>? = null,
    val maxSubscribers: Int? = null,
    val enabled: Boolean? = null,
)
