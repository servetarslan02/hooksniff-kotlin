package com.hooksniff.kotlin.models
import kotlinx.serialization.Serializable
@Serializable
data class ApplicationIn(
    val name: String? = null,
    val uid: String? = null,
    val metadata: Map<String, String>? = null,
)
