package com.hooksniff.kotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TypedWebhookEventTest {

    @Test
    fun testEndpointCreatedData() {
        val event = WebhookEvent(
            event = "endpoint.created",
            data = mapOf("appId" to "a1", "endpointId" to "e1", "appUid" to "u1"),
            timestamp = "2026-05-19"
        )

        val data = event.toEndpointCreatedData()
        assertEquals("a1", data.appId)
        assertEquals("e1", data.endpointId)
        assertEquals("u1", data.appUid)
    }

    @Test
    fun testEndpointDisabledData() {
        val event = WebhookEvent(
            event = "endpoint.disabled",
            data = mapOf("appId" to "a1", "endpointId" to "e1", "failSince" to "2026-01", "trigger" to "repeated-failure"),
            timestamp = ""
        )

        val data = event.toEndpointDisabledData()
        assertEquals("2026-01", data.failSince)
        assertEquals("repeated-failure", data.trigger)
    }

    @Test
    fun testMessageAttemptExhaustedData() {
        val event = WebhookEvent(
            event = "message.attempt.exhausted",
            data = mapOf(
                "appId" to "a1",
                "msgId" to "m1",
                "lastAttempt" to mapOf("id" to "att", "timestamp" to "t", "responseStatusCode" to 500)
            ),
            timestamp = ""
        )

        val data = event.toMessageAttemptExhaustedData()
        assertEquals("m1", data.msgId)
        assertEquals(500, data.lastAttempt.responseStatusCode)
    }

    @Test
    fun testMessageAttemptFailingData() {
        val event = WebhookEvent(
            event = "message.atattempt.failing",
            data = mapOf(
                "appId" to "a1",
                "msgId" to "m1",
                "attempt" to mapOf("id" to "att", "timestamp" to "t", "responseStatusCode" to 429)
            ),
            timestamp = ""
        )

        val data = event.toMessageAttemptFailingData()
        assertEquals(429, data.attempt.responseStatusCode)
    }

    @Test
    fun testMessageAttemptRecoveredData() {
        val event = WebhookEvent(
            event = "message.atattempt.recovered",
            data = mapOf(
                "appId" to "a1",
                "msgId" to "m1",
                "attempt" to mapOf("id" to "att", "timestamp" to "t", "responseStatusCode" to 200)
            ),
            timestamp = ""
        )

        val data = event.toMessageAttemptRecoveredData()
        assertEquals(200, data.attempt.responseStatusCode)
    }

    @Test
    fun testBackwardCompat() {
        val event = WebhookEvent(
            event = "endpoint.created",
            data = mapOf("appId" to "a1"),
            timestamp = "t"
        )

        assertEquals("a1", event["appId"])
        assertTrue("appId" in event)
        assertEquals("endpoint.created", event.eventType)
    }

    @Test
    fun testEventTypeMap() {
        assertEquals(10, WebhookEvent.EVENT_TYPE_MAP.size)
        assertTrue(WebhookEvent.EVENT_TYPE_MAP.containsKey("endpoint.created"))
        assertTrue(WebhookEvent.EVENT_TYPE_MAP.containsKey("message.attempt.exhausted"))
    }
}
