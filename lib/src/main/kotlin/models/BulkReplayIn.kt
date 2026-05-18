package com.hooksniff.kotlin.models
import kotlinx.serialization.Serializable
@Serializable
data class BulkReplayIn(
    val since: String? = null,
    val until: String? = null,
    val eventTypes: List<String>? = null,
    val channel: String? = null,
)
