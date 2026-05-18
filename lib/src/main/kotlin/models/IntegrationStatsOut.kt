package com.hooksniff.kotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class IntegrationStatsOut(
    val totalEvents: Long = 0,
    val delivered: Long = 0,
    val failed: Long = 0,
    val pending: Long = 0,
    val filtered: Long = 0,
    val avgDurationMs: Double? = null,
    val successRate: Double = 0.0,
    val last24hEvents: Long = 0,
    val last24hFailures: Long = 0,
)
