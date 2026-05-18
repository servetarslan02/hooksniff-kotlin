package com.hooksniff.kotlin

import com.hooksniff.kotlin.models.PollingEndpointConsumerSeekIn
import com.hooksniff.kotlin.models.PollingEndpointConsumerSeekOut
import com.hooksniff.kotlin.models.PollingEndpointMessageOut

/**
 * Poll messages from the message poller (long-polling consumer API).
 */
class MessagePoller(private val client: HookSniffHttpClient) {

    /** Poll for new messages. */
    suspend fun poll(): List<PollingEndpointMessageOut> {
        val url = client.newUrlBuilder().encodedPath("/v1/message-poller/poll")
        return client.executeRequest<Any, List<PollingEndpointMessageOut>>("GET", url.build())
    }

    /** Seek the poller cursor to a specific position. */
    suspend fun seek(body: PollingEndpointConsumerSeekIn): PollingEndpointConsumerSeekOut {
        val url = client.newUrlBuilder().encodedPath("/v1/message-poller/seek")
        return client.executeRequest<PollingEndpointConsumerSeekIn, PollingEndpointConsumerSeekOut>("POST", url.build(), reqBody = body)
    }

    /** Commit the poller cursor (acknowledge consumed messages). */
    suspend fun commit() {
        val url = client.newUrlBuilder().encodedPath("/v1/message-poller/commit")
        client.executeRequest<Any, Boolean>("POST", url.build())
    }
}
