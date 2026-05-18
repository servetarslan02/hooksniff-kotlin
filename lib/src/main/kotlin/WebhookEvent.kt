package com.hooksniff.kotlin

// ─── Event Data Classes ─────────────────────────────────────────────

/** Data payload for endpoint.created events. */
data class EndpointCreatedData(
    val appId: String = "",
    val endpointId: String = "",
    val appUid: String? = null
)

/** Data payload for endpoint.updated events. */
data class EndpointUpdatedData(
    val appId: String = "",
    val endpointId: String = "",
    val appUid: String? = null
)

/** Data payload for endpoint.deleted events. */
data class EndpointDeletedData(
    val appId: String = "",
    val endpointId: String = "",
    val appUid: String? = null
)

/** Data payload for endpoint.enabled events. */
data class EndpointEnabledData(
    val appId: String = "",
    val endpointId: String = "",
    val appUid: String? = null
)

/** Data payload for endpoint.disabled events. */
data class EndpointDisabledData(
    val appId: String = "",
    val endpointId: String = "",
    val appUid: String? = null,
    val failSince: String? = null,
    /** "none" | "first-failure" | "repeated-failure" */
    val trigger: String? = null
)

/** Info about the last delivery attempt. */
data class LastAttemptInfo(
    val id: String = "",
    val timestamp: String = "",
    val responseStatusCode: Int = 0
)

/** Info about a delivery attempt. */
data class AttemptInfo(
    val id: String = "",
    val timestamp: String = "",
    val responseStatusCode: Int = 0
)

/** Data payload for message.attempt.exhausted events. */
data class MessageAttemptExhaustedData(
    val appId: String = "",
    val msgId: String = "",
    val lastAttempt: LastAttemptInfo = LastAttemptInfo(),
    val appUid: String? = null
)

/** Data payload for message.attempt.failing events. */
data class MessageAttemptFailingData(
    val appId: String = "",
    val msgId: String = "",
    val attempt: AttemptInfo = AttemptInfo(),
    val appUid: String? = null
)

/** Data payload for message.atattempt.recovered events. */
data class MessageAttemptRecoveredData(
    val appId: String = "",
    val msgId: String = "",
    val attempt: AttemptInfo = AttemptInfo(),
    val appUid: String? = null
)

// ─── Typed Event Wrapper ────────────────────────────────────────────

/**
 * Represents a parsed webhook event from HookSniff.
 *
 * @property event Event type name (e.g., "endpoint.created")
 * @property data Event payload data (typed or raw map)
 * @property timestamp ISO 8601 timestamp string
 */
data class WebhookEvent(
    val event: String,
    val data: Map<String, Any?>,
    val timestamp: String
) {
    /** Alias for [event] — the event type name. */
    val eventType: String get() = event

    /** Get a value from the data map by key. */
    operator fun get(key: String): Any? = data[key]

    /** Check if key exists in data map. */
    operator fun contains(key: String): Boolean = data.containsKey(key)

    /** Known event types. */
    companion object {
        val EVENT_TYPE_MAP = mapOf(
            "endpoint.created" to "EndpointCreatedData",
            "endpoint.updated" to "EndpointUpdatedData",
            "endpoint.deleted" to "EndpointDeletedData",
            "endpoint.enabled" to "EndpointEnabledData",
            "endpoint.disabled" to "EndpointDisabledData",
            "message.attempt.exhausted" to "MessageAttemptExhaustedData",
            "message.attempt.failing" to "MessageAttemptFailingData",
            "message.atattempt.failing" to "MessageAttemptFailingData",
            "message.attempt.recovered" to "MessageAttemptRecoveredData",
            "message.atattempt.recovered" to "MessageAttemptRecoveredData"
        )
    }
}
