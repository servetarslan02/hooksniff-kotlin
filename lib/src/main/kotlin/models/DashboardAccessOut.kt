package com.hooksniff.kotlin.models
import kotlinx.serialization.Serializable
@Serializable
data class DashboardAccessOut(
    val token: String? = null,
    val url: String? = null,
)
