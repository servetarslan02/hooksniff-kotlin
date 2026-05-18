package com.hooksniff.kotlin

data class HookSniffOptions(
    var baseUrl: String? = null,
    val retrySchedule: List<Long> = listOf(1000, 2000, 4000),
    var timeoutMs: Long = 30000,
    var debug: Boolean = false,
    var headers: MutableMap<String, String> = mutableMapOf()
) {
    init {
        if (retrySchedule.count() > 5) {
            throw IllegalArgumentException("number of retries must not exceed 5")
        }
    }

    fun header(name: String, value: String): HookSniffOptions {
        headers[name] = value
        return this
    }
}
