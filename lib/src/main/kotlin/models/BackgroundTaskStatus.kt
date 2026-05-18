package com.hooksniff.kotlin.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BackgroundTaskStatus {
    @SerialName("PENDING") PENDING,
    @SerialName("RUNNING") RUNNING,
    @SerialName("COMPLETED") COMPLETED,
    @SerialName("FAILED") FAILED,
    @SerialName("CANCELLED") CANCELLED,
}
