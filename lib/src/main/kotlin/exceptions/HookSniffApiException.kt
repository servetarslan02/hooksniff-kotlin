package com.hooksniff.kotlin.exceptions

/**
 * Base exception for all HookSniff API errors.
 */
open class HookSniffApiException(
    message: String,
    val statusCode: Int,
    val responseBody: String? = null,
    val headers: Map<String, String> = emptyMap()
) : RuntimeException(message)

/** 400 Bad Request — The request was malformed or missing required fields */
class BadRequestException(detail: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(detail ?: "Bad request", 400, detail, headers)

/** 401 Unauthorized — Invalid or missing authentication */
class UnauthorizedException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Unauthorized", 401, message, headers)

/** 403 Forbidden — Insufficient permissions */
class ForbiddenException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Forbidden", 403, message, headers)

/** 404 Not Found — Resource does not exist */
class NotFoundException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Not found", 404, message, headers)

/** 409 Conflict — Resource already exists or conflict */
class ConflictException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Conflict", 409, message, headers)

/** 422 Unprocessable Entity — Validation error */
class UnprocessableEntityException(
    val validationErrors: List<ValidationErrorItem> = emptyList(),
    message: String?,
    headers: Map<String, String> = emptyMap()
) : HookSniffApiException(message ?: "Unprocessable entity", 422, message, headers)

/** 429 Too Many Requests — Rate limit exceeded */
class RateLimitException(
    val retryAfter: Int? = null,
    headers: Map<String, String> = emptyMap()
) : HookSniffApiException(
    "Rate limit exceeded${retryAfter?.let { " (retry after ${it}s)" } ?: ""}",
    429, null, headers
)

/** 500 Internal Server Error */
class InternalServerException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Internal server error", 500, message, headers)

/** 502 Bad Gateway */
class BadGatewayException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Bad gateway", 502, message, headers)

/** 503 Service Unavailable */
class ServiceUnavailableException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Service unavailable", 503, message, headers)

/** 504 Gateway Timeout */
class GatewayTimeoutException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Gateway timeout", 504, message, headers)

/** Validation error item from 422 responses */
data class ValidationErrorItem(
    val loc: List<String>,
    val msg: String,
    val type: String
)

/** Factory to create the appropriate exception from a status code */
object HookSniffApiExceptionFactory {
    fun create(statusCode: Int, body: String?, headers: Map<String, String> = emptyMap()): HookSniffApiException {
        return when (statusCode) {
            400 -> BadRequestException(body, headers)
            401 -> UnauthorizedException(body, headers)
            403 -> ForbiddenException(body, headers)
            404 -> NotFoundException(body, headers)
            409 -> ConflictException(body, headers)
            422 -> UnprocessableEntityException(emptyList(), body, headers)
            429 -> {
                val retryAfter = headers["retry-after"]?.toIntOrNull()
                RateLimitException(retryAfter, headers)
            }
            500 -> InternalServerException(body, headers)
            502 -> BadGatewayException(body, headers)
            503 -> ServiceUnavailableException(body, headers)
            504 -> GatewayTimeoutException(body, headers)
            else -> HookSniffApiException("HTTP $statusCode", statusCode, body, headers)
        }
    }
}

/** 408 Request Timeout — The server timed out waiting for the request */
class RequestTimeoutException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Request timeout", 408, message, headers)

/** 410 Gone — The resource has been permanently removed */
class GoneException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Gone", 410, message, headers)

/** 413 Payload Too Large — The request body exceeds the limit */
class PayloadTooLargeException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Payload too large", 413, message, headers)

/** 501 Not Implemented — The server does not support this functionality */
class NotImplementedException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Not implemented", 501, message, headers)

/** 507 Insufficient Storage — The server cannot store the representation */
class InsufficientStorageException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Insufficient storage", 507, message, headers)

/** 508 Loop Detected — The server detected an infinite loop */
class LoopDetectedException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Loop detected", 508, message, headers)

/** Timeout — request exceeded the configured timeout */
class TimeoutException(message: String? = null) :
    HookSniffApiException(message ?: "Request timeout", 0, null, emptyMap())

/** Network error — connection failed, DNS error, etc. */
class NetworkException(message: String? = null) :
    HookSniffApiException(message ?: "Network error", 0, null, emptyMap())

/** Authentication error — token invalid, expired, or missing */
class AuthenticationException(message: String?, headers: Map<String, String> = emptyMap()) :
    HookSniffApiException(message ?: "Authentication failed", 401, message, headers)
