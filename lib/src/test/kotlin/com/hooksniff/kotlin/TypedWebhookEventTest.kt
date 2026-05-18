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

    @Test
    fun testEndpointUpdatedData() {
        val event = WebhookEvent(event = "endpoint.updated", data = mapOf("appId" to "a1", "endpointId" to "e1"), timestamp = "")
        assertEquals("a1", event.toEndpointUpdatedData().appId)
    }

    @Test
    fun testEndpointDeletedData() {
        val event = WebhookEvent(event = "endpoint.deleted", data = mapOf("appId" to "a1", "endpointId" to "e1"), timestamp = "")
        assertEquals("e1", event.toEndpointDeletedData().endpointId)
    }

    @Test
    fun testEndpointEnabledData() {
        val event = WebhookEvent(event = "endpoint.enabled", data = mapOf("appId" to "a1", "endpointId" to "e1"), timestamp = "")
        assertEquals("a1", event.toEndpointEnabledData().appId)
    }

    @Test
    fun testEmptyData() {
        val event = WebhookEvent(event = "endpoint.created", data = emptyMap(), timestamp = "")
        assertEquals("", event.toEndpointCreatedData().appId)
    }

    @Test
    fun testGetMissingKey() {
        val event = WebhookEvent(event = "test", data = mapOf("x" to 1), timestamp = "")
        assertNull(event["missing"])
    }

    @Test
    fun testAllEndpointEventTypes() {
        for (type in listOf("endpoint.created", "endpoint.updated", "endpoint.deleted", "endpoint.enabled", "endpoint.disabled")) {
            assertEquals(type, WebhookEvent(event = type, data = emptyMap(), timestamp = "").event)
        }
    }

    @Test
    fun testUnicodeData() {
        val event = WebhookEvent(event = "endpoint.created", data = mapOf("appId" to "ünïcödé", "endpointId" to "日本語"), timestamp = "")
        val data = event.toEndpointCreatedData()
        assertEquals("ünïcödé", data.appId)
        assertEquals("日本語", data.endpointId)
    }

    @Test
    fun testLargeData() {
        val event = WebhookEvent(event = "endpoint.created", data = mapOf("appId" to "a".repeat(10000), "endpointId" to "e".repeat(10000)), timestamp = "")
        val data = event.toEndpointCreatedData()
        assertEquals(10000, data.appId.length)
    }

    @Test
    fun testSpecialCharacters() {
        val event = WebhookEvent(event = "endpoint.created", data = mapOf("appId" to "a@b.c", "endpointId" to "e#1"), timestamp = "")
        val data = event.toEndpointCreatedData()
        assertEquals("a@b.c", data.appId)
    }

    @Test
    fun testTriggerNone() {
        val event = WebhookEvent(event = "endpoint.disabled", data = mapOf("appId" to "a", "endpointId" to "e", "trigger" to "none"), timestamp = "")
        val data = event.toEndpointDisabledData()
        assertEquals("none", data.trigger)
    }

    @Test
    fun testTriggerFirstFailure() {
        val event = WebhookEvent(event = "endpoint.disabled", data = mapOf("appId" to "a", "endpointId" to "e", "trigger" to "first-failure"), timestamp = "")
        val data = event.toEndpointDisabledData()
        assertEquals("first-failure", data.trigger)
    }

    @Test
    fun testFailSince() {
        val event = WebhookEvent(event = "endpoint.disabled", data = mapOf("appId" to "a", "endpointId" to "e", "failSince" to "2026-01"), timestamp = "")
        val data = event.toEndpointDisabledData()
        assertEquals("2026-01", data.failSince)
    }

    @Test
    fun testAllEndpointTypes() {
        for (type in listOf("endpoint.created", "endpoint.updated", "endpoint.deleted", "endpoint.enabled", "endpoint.disabled")) {
            assertEquals(type, WebhookEvent(event = type, data = emptyMap(), timestamp = "").event)
        }
    }

    @Test
    fun testGetExistingKey() {
        val event = WebhookEvent(event = "test", data = mapOf("x" to 1), timestamp = "")
        assertEquals(1, event["x"])
    }

    @Test
    fun testContainsExistingKey() {
        val event = WebhookEvent(event = "test", data = mapOf("x" to 1), timestamp = "")
        assertTrue("x" in event)
    }

    @Test
    fun testContainsMissingKey() {
        val event = WebhookEvent(event = "test", data = mapOf("x" to 1), timestamp = "")
        assertFalse("missing" in event)
    }

    @Test
    fun testEventTypeProperty() {
        val event = WebhookEvent(event = "endpoint.created", data = emptyMap(), timestamp = "")
        assertEquals("endpoint.created", event.eventType)
    }
}
