package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class PublishEventResponse(
    val success: Boolean = false,
    val messageId: String? = null,
    val deliveredTo: Int = 0,
)
