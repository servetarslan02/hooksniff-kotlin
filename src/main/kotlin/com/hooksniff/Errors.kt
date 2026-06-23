package com.hooksniff

open class HookSniffError(
    val statusCode: Int,
    val code: String,
    val detail: String
) : Exception("HookSniff API error $statusCode ($code): $detail")

class AuthenticationError(detail: String = "Invalid API key") :
    HookSniffError(401, "UNAUTHORIZED", detail)

class NotFoundError(detail: String = "Resource not found") :
    HookSniffError(404, "NOT_FOUND", detail)

class RateLimitError(detail: String = "Rate limited", val retryAfter: Int = 60) :
    HookSniffError(429, "RATE_LIMITED", detail)

class ValidationError(detail: String = "Validation failed") :
    HookSniffError(400, "BAD_REQUEST", detail)

class ServerError(detail: String = "Internal server error") :
    HookSniffError(500, "INTERNAL_ERROR", detail)

fun mapError(statusCode: Int, body: Map<String, Any?>): HookSniffError {
    @Suppress("UNCHECKED_CAST")
    val error = body["error"] as? Map<String, Any?> ?: emptyMap()
    val code = error["code"] as? String ?: "UNKNOWN"
    val detail = (error["detail"] as? String ?: error["message"] as? String ?: "Unknown error")

    return when (statusCode) {
        401 -> AuthenticationError(detail)
        404 -> NotFoundError(detail)
        429 -> RateLimitError(detail)
        400, 422 -> ValidationError(detail)
        in 500..599 -> ServerError(detail)
        else -> HookSniffError(statusCode, code, detail)
    }
}
