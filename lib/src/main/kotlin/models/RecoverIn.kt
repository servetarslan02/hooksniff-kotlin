package com.hooksniff.kotlin.models
import kotlinx.serialization.Serializable
@Serializable
data class RecoverIn(
    val since: String? = null,
    val until: String? = null,
)
