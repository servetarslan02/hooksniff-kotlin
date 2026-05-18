package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class IntegrationTestResponse(
    val success: Boolean = false,
    val eventId: String? = null,
    val message: String? = null,
)
