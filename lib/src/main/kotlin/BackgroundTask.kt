package com.hooksniff.kotlin

/**
 * Manage background tasks (async operations like stats aggregation).
 */
class BackgroundTask(private val client: HookSniffHttpClient) {

    /** List all background tasks. */
    suspend fun list(): List<Map<String, Any?>> {
        val url = client.newUrlBuilder().encodedPath("/v1/background-tasks")
        return client.executeRequest<Any, List<Map<String, Any?>>>("GET", url.build())
    }

    /** Get a background task by ID. */
    suspend fun get(taskId: String): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/background-tasks/$taskId")
        return client.executeRequest<Any, Map<String, Any?>>("GET", url.build())
    }

    /** Cancel a background task. */
    suspend fun cancel(taskId: String): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/background-tasks/$taskId")
        return client.executeRequest<Any, Map<String, Any?>>("PUT", url.build())
    }
}
