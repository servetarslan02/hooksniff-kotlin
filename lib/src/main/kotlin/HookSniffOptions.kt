package com.hooksniff.kotlin

data class HookSniffOptions(
    var baseUrl: String? = null,
    val retrySchedule: List<Long> = listOf(1000, 2000, 4000),
) {
    init {
        if (retrySchedule.count() > 5) {
            throw IllegalArgumentException("number of retries must not exceed 5")
        }
    }
}
