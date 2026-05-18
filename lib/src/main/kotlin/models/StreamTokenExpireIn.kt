package com.hooksniff.kotlin.models
import kotlinx.serialization.Serializable
@Serializable
data class StreamTokenExpireIn(
    val expiry: Long? = null,
)
