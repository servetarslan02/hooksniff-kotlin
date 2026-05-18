package com.hooksniff.kotlin

/**
 * Manage environments (dev, staging, production) and their variables.
 */
class Environment(private val client: HookSniffHttpClient) {

    /** List all environments. */
    suspend fun list(): List<Map<String, Any?>> {
        val url = client.newUrlBuilder().encodedPath("/v1/environments")
        return client.executeRequest<Any, List<Map<String, Any?>>>("GET", url.build())
    }

    /** Create a new environment. */
    suspend fun create(body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/environments")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("POST", url.build(), reqBody = body)
    }

    /** Get an environment by ID. */
    suspend fun get(id: String): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/environments/$id")
        return client.executeRequest<Any, Map<String, Any?>>("GET", url.build())
    }

    /** Update an environment. */
    suspend fun update(id: String, body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/environments/$id")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("PATCH", url.build(), reqBody = body)
    }

    /** Delete an environment. */
    suspend fun delete(id: String) {
        val url = client.newUrlBuilder().encodedPath("/v1/environments/$id")
        client.executeRequest<Any, Boolean>("DELETE", url.build())
    }

    /** List variables for an environment. */
    suspend fun listVariables(envId: String): List<Map<String, Any?>> {
        val url = client.newUrlBuilder().encodedPath("/v1/environments/$envId/variables")
        return client.executeRequest<Any, List<Map<String, Any?>>>("GET", url.build())
    }

    /** Get a specific variable. */
    suspend fun getVariable(envId: String, varId: String): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/environments/$envId/variables/$varId")
        return client.executeRequest<Any, Map<String, Any?>>("GET", url.build())
    }

    /** Create a variable in an environment. */
    suspend fun createVariable(envId: String, body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/environments/$envId/variables")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("POST", url.build(), reqBody = body)
    }

    /** Update a variable. */
    suspend fun updateVariable(envId: String, varId: String, body: Map<String, Any?>): Map<String, Any?> {
        val url = client.newUrlBuilder().encodedPath("/v1/environments/$envId/variables/$varId")
        return client.executeRequest<Map<String, Any?>, Map<String, Any?>>("PUT", url.build(), reqBody = body)
    }

    /** Delete a variable. */
    suspend fun deleteVariable(envId: String, varId: String) {
        val url = client.newUrlBuilder().encodedPath("/v1/environments/$envId/variables/$varId")
        client.executeRequest<Any, Boolean>("DELETE", url.build())
    }
}
