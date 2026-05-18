package com.hooksniff.kotlin.models
import kotlinx.serialization.Serializable
@Serializable
data class AppPortalAccessIn(
    val expiry: Long? = null,
    val readOnly: Boolean? = null,
    val featureFlags: List<String>? = null,
)
