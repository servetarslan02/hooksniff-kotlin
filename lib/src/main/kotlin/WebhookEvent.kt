package com.hooksniff.kotlin

/**
 * Represents a parsed webhook event from HookSniff.
 *
 * @property event Event type name (e.g., "endpoint.created")
 * @property data Event payload data
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
}
