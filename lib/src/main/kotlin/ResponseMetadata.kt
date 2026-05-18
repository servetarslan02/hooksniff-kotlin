package com.hooksniff.kotlin

/**
 * Response metadata from the last API request.
 *
 * @property statusCode HTTP status code
 * @property requestId x-request-id header
 * @property rateLimitRemaining x-ratelimit-remaining header
 * @property rateLimitReset x-ratelimit-reset header (Unix timestamp)
 * @property headers All response headers
 */
data class ResponseMetadata(
    val statusCode: Int,
    val requestId: String? = null,
    val rateLimitRemaining: Int? = null,
    val rateLimitReset: Int? = null,
    val headers: Map<String, List<String>> = emptyMap()
) {
    /** Get first value for a header name. */
    fun getHeader(name: String): String? =
        headers[name]?.firstOrNull()
            ?: headers.entries.find { it.key.equals(name, ignoreCase = true) }?.value?.firstOrNull()
}
