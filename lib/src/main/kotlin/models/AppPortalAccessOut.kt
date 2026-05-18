package com.hooksniff.kotlin.models
import kotlinx.serialization.Serializable
@Serializable
data class AppPortalAccessOut(
    val token: String? = null,
    val url: String? = null,
)
