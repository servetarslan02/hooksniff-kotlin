# HookSniff Kotlin SDK

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.servetarslan02/hooksniff-sdk-kotlin"><img src="https://img.shields.io/maven-central/v/io.github.servetarslan02/hooksniff-sdk-kotlin" alt="Maven Central"></a>
  <a href="https://github.com/servetarslan02/hooksniff-kotlin"><img src="https://img.shields.io/github/license/servetarslan02/hooksniff-kotlin" alt="License"></a>
  <a href="https://hooksniff.vercel.app"><img src="https://img.shields.io/badge/webhook-platform-blue" alt="HookSniff"></a>
</p>

Official Kotlin SDK for [HookSniff](https://hooksniff.vercel.app) — reliable webhook delivery for developers.

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.servetarslan02:hooksniff-sdk-kotlin:1.2.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'io.github.servetarslan02:hooksniff-sdk-kotlin:1.2.0'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.servetarslan02</groupId>
    <artifactId>hooksniff-sdk-kotlin</artifactId>
    <version>1.2.0</version>
</dependency>
```

## Quick Start

```kotlin
import com.hooksniff.kotlin.HookSniff
import com.hooksniff.kotlin.HookSniffOptions
import com.hooksniff.kotlin.models.EndpointIn
import com.hooksniff.kotlin.models.MessageIn
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // Initialize client
    val client = HookSniff(
        token = "hs_xxx_your_api_key",
        options = HookSniffOptions(baseUrl = "https://hooksniff-api-1046140057667.europe-west1.run.app")
    )

    // List endpoints
    val endpoints = client.endpoint.list("app_xxx")
    println("Endpoints: ${endpoints.data?.size}")

    // Create an endpoint
    val newEndpoint = client.endpoint.create(
        "app_xxx",
        EndpointIn(url = "https://your-app.com/webhook")
    )
    println("Created: ${newEndpoint.id}")

    // Send a webhook message
    val message = client.message.create(
        "app_xxx",
        MessageIn(
            eventType = "order.created",
            payload = mapOf("order_id" to "12345", "total" to 99.99)
        )
    )
    println("Message sent: ${message.id}")
}
```

## Authentication

Use your API key (starts with `hs_`) or a JWT token from the login endpoint.

```kotlin
// With API key
val client = HookSniff("hs_xxx")

// With custom base URL
val client = HookSniff(
    token = "hs_xxx",
    options = HookSniffOptions(baseUrl = "https://hooksniff-api-1046140057667.europe-west1.run.app")
)

// With custom retry schedule (max 5 retries)
val client = HookSniff(
    token = "hs_xxx",
    options = HookSniffOptions(
        retrySchedule = listOf(50, 100, 200, 500, 1000)
    )
)
```

## Webhook Verification

Verify incoming webhook signatures using HMAC-SHA256 (Standard Webhooks compliant).

```kotlin
import com.hooksniff.kotlin.Webhook
import com.hooksniff.kotlin.exceptions.WebhookVerificationException

val webhook = Webhook("whsec_your_signing_secret")

try {
    // With Map<String, String> headers
    webhook.verify(payload, mapOf(
        "webhook-id" to headers["webhook-id"]!!,
        "webhook-timestamp" to headers["webhook-timestamp"]!!,
        "webhook-signature" to headers["webhook-signature"]!!
    ))
    println("✅ Signature valid!")
} catch (e: WebhookVerificationException) {
    println("❌ Invalid signature: ${e.message}")
}
```

### HookSniff Branded Headers

The SDK also supports HookSniff-branded headers:

```kotlin
webhook.verify(payload, mapOf(
    "hooksniff-id" to "...",
    "hooksniff-timestamp" to "...",
    "hooksniff-signature" to "..."
))
```

### Signing Webhooks

```kotlin
val signature = webhook.sign(msgId = "msg_xxx", timestamp = System.currentTimeMillis() / 1000, payload = jsonString)
// Returns: "v1,<base64-signature>"
```

## Resources

### Core

| Resource | Methods | Description |
|----------|---------|-------------|
| `endpoint` | list, create, get, update, delete, patch, getSecret, rotateSecret, getHeaders, updateHeaders, patchHeaders, getStats, bulkReplay, recover, replayMissing, sendExample | Manage webhook endpoints |
| `message` | list, create, get, expungeContent, expungeAllContents, precheck | Send and manage webhook messages |
| `messageAttempt` | listByEndpoint, listByMsg, listAttemptedMessages, get, expungeContent, listAttemptedDestinations, resend | Track delivery attempts |
| `eventType` | list, create, get, update, delete, patch, importOpenapi | Manage event types |
| `authentication` | logout, streamLogout, getStreamPollerToken, rotateStreamPollerToken | Auth operations |
| `statistics` | aggregateAppStats, aggregateEventTypes | Usage statistics |
| `health` | get | API health check |

### Faz 8-15 — New Features

| Resource | Methods | Description |
|----------|---------|-------------|
| `environment` | list, create, get, update, delete, listVariables, getVariable, createVariable, updateVariable, deleteVariable | Manage environments (dev/staging/prod) |
| `backgroundTask` | list, get, cancel | Manage async background tasks |
| `operationalWebhook` | list, create, get, update, delete, listDeliveries | Operational webhook endpoints |
| `messagePoller` | poll, seek, commit | Long-polling consumer API |
| `inbound` | listConfigs, createConfig, handleInbound | Inbound webhook proxy |
| `connector` | list, get, listConfigs, createConfig, updateConfig, deleteConfig | Third-party connectors |
| `integration` | list, get, create, update, delete, test, listEvents, getStats | Integration management |
| `stream` | listChannels, getChannel, createChannel, updateChannel, deleteChannel, listMessages, subscribe, publishEvent | Real-time streaming |

## Error Handling

The SDK throws `ApiException` for HTTP errors with status code and response body:

```kotlin
import com.hooksniff.kotlin.exceptions.ApiException

try {
    val endpoint = client.endpoint.get("app_xxx", "ep_xxx")
} catch (e: ApiException) {
    println("Status: ${e.statusCode}")  // e.g., 404
    println("Body: ${e.body}")          // JSON error response
    println("Message: ${e.message}")    // Error description
}
```

### Common Status Codes

| Code | Meaning |
|------|---------|
| 400 | Bad Request — check your input |
| 401 | Unauthorized — invalid or missing token |
| 403 | Forbidden — insufficient permissions |
| 404 | Not Found — resource doesn't exist |
| 409 | Conflict — duplicate resource |
| 422 | Validation Error — invalid parameters |
| 429 | Rate Limited — auto-retry with backoff |
| 500 | Server Error — retry later |

## Rate Limiting

The SDK automatically handles 429 rate limit responses:
- Reads the `Retry-After` header
- Waits the specified duration
- Retries the request automatically

For 5xx server errors, the SDK retries with exponential backoff using the configured `retrySchedule`.

## Coroutines

All API methods are `suspend` functions. Use them within a coroutine scope:

```kotlin
// In a coroutine scope
runBlocking {
    val endpoints = client.endpoint.list("app_xxx")
}

// Or in a ViewModel / CoroutineScope
viewModelScope.launch {
    val messages = client.message.list("app_xxx")
}
```

## Requirements

- Kotlin 1.9+
- JDK 11+
- kotlinx-serialization 1.8+
- kotlinx-coroutines 1.5+
- OkHttp 4.12+

## Links

- 📖 [Documentation](https://hooksniff.vercel.app/docs)
- 🔑 [API Reference](https://hooksniff-api-1046140057667.europe-west1.run.app/v1/docs)
- 💰 [Pricing](https://hooksniff.vercel.app/pricing)
- 🐛 [Report a Bug](https://github.com/servetarslan02/hooksniff-kotlin/issues)
- 💬 [GitHub Discussions](https://github.com/servetarslan02/HookSniff/discussions)

## License

MIT — see [LICENSE](LICENSE) for details.
