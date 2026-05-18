package com.hooksniff.kotlin

/**
 * Extension functions to parse [WebhookEvent] data into typed data classes.
 *
 * Usage:
 * ```kotlin
 * val event = webhook.verifyAndParse(payload, headers)
 * when (event.event) {
 *     "endpoint.created" -> {
 *         val data = event.toEndpointCreatedData()
 *         println(data.endpointId)
 *     }
 *     "message.attempt.exhausted" -> {
 *         val data = event.toMessageAttemptExhaustedData()
 *         println(data.lastAttempt.responseStatusCode)
 *     }
 * }
 * ```
 */

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.nested(key: String): Map<String, Any?> {
    return (this[key] as? Map<String, Any?>) ?: emptyMap()
}

private fun Map<*, *>.str(key: String): String {
    return (this[key] ?: this[key.toString()] ?: "").toString()
}

private fun Map<*, *>.strNullable(key: String): String? {
    val v = this[key] ?: this[key.toString()]
    return v?.toString()
}

private fun Map<*, *>.int(key: String): Int {
    return (this[key] ?: this[key.toString()] ?: 0).toString().toIntOrNull() ?: 0
}

/** Parse event data as EndpointCreatedData. */
fun WebhookEvent.toEndpointCreatedData(): EndpointCreatedData {
    val raw = data
    return EndpointCreatedData(
        appId = raw.str("appId"),
        endpointId = raw.str("endpointId"),
        appUid = raw.strNullable("appUid")
    )
}

/** Parse event data as EndpointUpdatedData. */
fun WebhookEvent.toEndpointUpdatedData(): EndpointUpdatedData {
    return EndpointUpdatedData(
        appId = data.str("appId"),
        endpointId = data.str("endpointId"),
        appUid = data.strNullable("appUid")
    )
}

/** Parse event data as EndpointDeletedData. */
fun WebhookEvent.toEndpointDeletedData(): EndpointDeletedData {
    return EndpointDeletedData(
        appId = data.str("appId"),
        endpointId = data.str("endpointId"),
        appUid = data.strNullable("appUid")
    )
}

/** Parse event data as EndpointEnabledData. */
fun WebhookEvent.toEndpointEnabledData(): EndpointEnabledData {
    return EndpointEnabledData(
        appId = data.str("appId"),
        endpointId = data.str("endpointId"),
        appUid = data.strNullable("appUid")
    )
}

/** Parse event data as EndpointDisabledData. */
fun WebhookEvent.toEndpointDisabledData(): EndpointDisabledData {
    return EndpointDisabledData(
        appId = data.str("appId"),
        endpointId = data.str("endpointId"),
        appUid = data.strNullable("appUid"),
        failSince = data.strNullable("failSince"),
        trigger = data.strNullable("trigger")
    )
}

/** Parse event data as MessageAttemptExhaustedData. */
fun WebhookEvent.toMessageAttemptExhaustedData(): MessageAttemptExhaustedData {
    val lastRaw = data.nested("lastAttempt")
    return MessageAttemptExhaustedData(
        appId = data.str("appId"),
        msgId = data.str("msgId"),
        lastAttempt = LastAttemptInfo(
            id = lastRaw.str("id"),
            timestamp = lastRaw.str("timestamp"),
            responseStatusCode = lastRaw.int("responseStatusCode")
        ),
        appUid = data.strNullable("appUid")
    )
}

/** Parse event data as MessageAttemptFailingData. */
fun WebhookEvent.toMessageAttemptFailingData(): MessageAttemptFailingData {
    val attRaw = data.nested("attempt")
    return MessageAttemptFailingData(
        appId = data.str("appId"),
        msgId = data.str("msgId"),
        attempt = AttemptInfo(
            id = attRaw.str("id"),
            timestamp = attRaw.str("timestamp"),
            responseStatusCode = attRaw.int("responseStatusCode")
        ),
        appUid = data.strNullable("appUid")
    )
}

/** Parse event data as MessageAttemptRecoveredData. */
fun WebhookEvent.toMessageAttemptRecoveredData(): MessageAttemptRecoveredData {
    val attRaw = data.nested("attempt")
    return MessageAttemptRecoveredData(
        appId = data.str("appId"),
        msgId = data.str("msgId"),
        attempt = AttemptInfo(
            id = attRaw.str("id"),
            timestamp = attRaw.str("timestamp"),
            responseStatusCode = attRaw.int("responseStatusCode")
        ),
        appUid = data.strNullable("appUid")
    )
}
