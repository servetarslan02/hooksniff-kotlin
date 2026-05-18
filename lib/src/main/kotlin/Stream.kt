package com.hooksniff.kotlin

import com.hooksniff.kotlin.models.PublishEventIn
import com.hooksniff.kotlin.models.PublishEventResponse
import com.hooksniff.kotlin.models.StreamChannelDetailOut
import com.hooksniff.kotlin.models.StreamChannelIn
import com.hooksniff.kotlin.models.StreamChannelOut
import com.hooksniff.kotlin.models.StreamChannelUpdate
import com.hooksniff.kotlin.models.StreamMessageOut
import com.hooksniff.kotlin.models.StreamSubscriptionOut

class Stream(private val client: HookSniffHttpClient) {
    /** List all stream channels. */
    suspend fun listChannels(): List<StreamChannelOut> {
        val url = client.newUrlBuilder().encodedPath("/v1/stream/channels")
        return client.executeRequest<Any, List<StreamChannelOut>>("GET", url.build())
    }

    /** Get a stream channel by ID. */
    suspend fun getChannel(id: String): StreamChannelDetailOut {
        val url = client.newUrlBuilder().encodedPath("/v1/stream/channels/$id")
        return client.executeRequest<Any, StreamChannelDetailOut>("GET", url.build())
    }

    /** Create a new stream channel. */
    suspend fun createChannel(channelIn: StreamChannelIn): StreamChannelOut {
        val url = client.newUrlBuilder().encodedPath("/v1/stream/channels")
        return client.executeRequest<StreamChannelIn, StreamChannelOut>("POST", url.build(), reqBody = channelIn)
    }

    /** Update a stream channel. */
    suspend fun updateChannel(id: String, channelUpdate: StreamChannelUpdate): StreamChannelOut {
        val url = client.newUrlBuilder().encodedPath("/v1/stream/channels/$id")
        return client.executeRequest<StreamChannelUpdate, StreamChannelOut>("PUT", url.build(), reqBody = channelUpdate)
    }

    /** Delete a stream channel. */
    suspend fun deleteChannel(id: String) {
        val url = client.newUrlBuilder().encodedPath("/v1/stream/channels/$id")
        client.executeRequest<Any, Boolean>("DELETE", url.build())
    }

    /** List messages for a stream channel. */
    suspend fun listMessages(channelId: String): List<StreamMessageOut> {
        val url = client.newUrlBuilder().encodedPath("/v1/stream/channels/$channelId/messages")
        return client.executeRequest<Any, List<StreamMessageOut>>("GET", url.build())
    }

    /** List all stream subscriptions. */
    suspend fun listSubscriptions(): List<StreamSubscriptionOut> {
        val url = client.newUrlBuilder().encodedPath("/v1/stream/subscriptions")
        return client.executeRequest<Any, List<StreamSubscriptionOut>>("GET", url.build())
    }

    /** Disconnect a stream subscription. */
    suspend fun disconnectSubscription(id: String) {
        val url = client.newUrlBuilder().encodedPath("/v1/stream/subscriptions/$id")
        client.executeRequest<Any, Boolean>("DELETE", url.build())
    }

    /** Publish an event to a stream channel. */
    suspend fun publishEvent(publishEventIn: PublishEventIn): StreamMessageOut {
        val url = client.newUrlBuilder().encodedPath("/v1/stream/events")
        return client.executeRequest<PublishEventIn, StreamMessageOut>("POST", url.build(), reqBody = publishEventIn)
    }
}

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/** Subscribe to real-time events via SSE on a channel. */
fun subscribe(channelId: String): Flow<String> = flow {
    val url = URL("${client.baseUrl}/v1/stream/channels/$channelId/subscribe")
    val conn = url.openConnection() as HttpURLConnection
    conn.setRequestProperty("Authorization", "Bearer ${client.apiKey}")
    conn.setRequestProperty("Accept", "text/event-stream")
    conn.connectTimeout = 30_000
    conn.readTimeout = 0 // SSE connections are long-lived

    try {
        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        var eventType = ""
        val data = StringBuilder()

        reader.forEachLine { line ->
            when {
                line.startsWith("event:") -> eventType = line.removePrefix("event:").trim()
                line.startsWith("data:") -> data.append(line.removePrefix("data:").trim())
                line.isEmpty() && data.isNotEmpty() -> {
                    emit(data.toString())
                    eventType = ""
                    data.clear()
                }
            }
        }
    } finally {
        conn.disconnect()
    }
}
