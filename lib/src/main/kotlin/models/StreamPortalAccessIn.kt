package com.hooksniff.kotlin.models
import kotlinx.serialization.Serializable
@Serializable
data class StreamPortalAccessIn(
    val expiry: Long? = null,
    val readOnly: Boolean? = null,
)
