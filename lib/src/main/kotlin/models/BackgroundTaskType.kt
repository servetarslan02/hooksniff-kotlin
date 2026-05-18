package com.hooksniff.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BackgroundTaskType {
    @SerialName("AGGREGATE_EVENT_TYPES") AGGREGATE_EVENT_TYPES,
    @SerialName("REPLAY") REPLAY,
    @SerialName("EXPORT") EXPORT,
}
