package com.hooksniff

import kotlin.test.*

class HookSniffTest {
    @Test
    fun testClientInit() {
        val hs = HookSniff("hr_test_fake")
        assertNotNull(hs.application)
        assertNotNull(hs.endpoint)
        assertNotNull(hs.webhook)
        assertNotNull(hs.apiKey)
        assertNotNull(hs.analytics)
        assertNotNull(hs.search)
        assertNotNull(hs.health)
        assertNotNull(hs.team)
        assertNotNull(hs.billing)
        assertNotNull(hs.notification)
        assertNotNull(hs.cortex)
        assertNotNull(hs.template)
        assertNotNull(hs.schema)
        assertNotNull(hs.alert)
        assertNotNull(hs.connector)
        assertNotNull(hs.stream)
        assertNotNull(hs.backgroundTask)
        assertNotNull(hs.integration)
        assertNotNull(hs.serviceToken)
        assertNotNull(hs.operationalWebhook)
        assertNotNull(hs.rateLimit)
        assertNotNull(hs.audit)
        assertNotNull(hs.sso)
        assertNotNull(hs.customDomain)
        assertNotNull(hs.environment)
        assertNotNull(hs.broadcast)
        assertNotNull(hs.transform)
        println("✅ All 27 resources initialized")
    }

    @Test
    fun testWebhookVerification() {
        assertFailsWith<IllegalArgumentException> { Webhook("") }

        val wh = Webhook("whsec_test")
        assertFailsWith<WebhookVerificationError> { wh.verify("{}", emptyMap()) }

        val oldHeaders = mapOf(
            "webhook-id" to "test",
            "webhook-signature" to "v1,test",
            "webhook-timestamp" to "1000000000"
        )
        assertFailsWith<WebhookVerificationError> { wh.verify("{}", oldHeaders) }
        println("✅ Webhook verification tests passed")
    }

    @Test
    fun testErrorHierarchy() {
        val auth = AuthenticationError()
        assertEquals(401, auth.statusCode)
        assertEquals("UNAUTHORIZED", auth.code)

        val notFound = NotFoundError()
        assertEquals(404, notFound.statusCode)

        val rateLimit = RateLimitError(retryAfter = 60)
        assertEquals(429, rateLimit.statusCode)
        assertEquals(60, rateLimit.retryAfter)

        val validation = ValidationError()
        assertEquals(400, validation.statusCode)

        val server = ServerError()
        assertEquals(500, server.statusCode)
        println("✅ Error hierarchy tests passed")
    }

    @Test
    fun testMapError() {
        val errBody = mapOf("error" to mapOf("code" to "TEST", "detail" to "test error"))

        assertTrue(mapError(401, errBody) is AuthenticationError)
        assertTrue(mapError(404, errBody) is NotFoundError)
        assertTrue(mapError(429, errBody) is RateLimitError)
        assertTrue(mapError(400, errBody) is ValidationError)
        assertTrue(mapError(422, errBody) is ValidationError)
        assertTrue(mapError(500, errBody) is ServerError)
        assertTrue(mapError(502, errBody) is ServerError)
        println("✅ mapError tests passed")
    }

    @Test
    fun testCustomConfig() {
        val hs = HookSniff("hr_test_fake", ClientConfig(
            baseUrl = "https://custom.api.com",
            retries = 5
        ))
        assertNotNull(hs)
        println("✅ Custom config test passed")
    }
}
