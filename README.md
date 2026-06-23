# HookSniff Kotlin SDK

Official Kotlin SDK for [HookSniff](https://hooksniff.vercel.app) — the webhook infrastructure for developers.

## Installation

### Gradle (Kotlin DSL)
```kotlin
implementation("com.hooksniff:hooksniff-kotlin:0.5.0")
```

### Gradle (Groovy)
```groovy
implementation 'com.hooksniff:hooksniff-kotlin:0.5.0'
```

### Maven
```xml
<dependency>
    <groupId>com.hooksniff</groupId>
    <artifactId>hooksniff-kotlin</artifactId>
    <version>0.5.0</version>
</dependency>
```

## Quick Start

```kotlin
import com.hooksniff.*

fun main() {
    val hs = HookSniff("hr_live_...")

    // Create an application
    val app = hs.application.create(ApplicationCreate(name = "My App"))

    // Create an endpoint
    val ep = hs.endpoint.create(EndpointCreate(
        url = "https://app.com/webhook",
        application_id = app.id
    ))

    // Send a webhook
    val delivery = hs.webhook.send(WebhookSend(
        endpoint_id = ep.id,
        event = "order.created",
        data = buildJsonObject {
            put("order_id", "12345")
            put("amount", 99.99)
        }
    ))

    println(delivery.id)
}
```

## Features

- **Zero external dependencies** — uses java.net.http only (+ kotlinx.serialization)
- **Full Kotlin DSL** — idiomatic Kotlin with data classes
- **Auto-retry** — exponential backoff on 429/5xx errors
- **Auto-pagination** — iterate through all resources
- **Webhook verification** — Standard Webhooks compliant
- **27 resources** — Application, Endpoint, Webhook, Billing, Cortex, Teams, and more

## Webhook Verification

```kotlin
val wh = Webhook("whsec_...")

val event = wh.verify(payload, headers)
println(event)
```

## Error Handling

```kotlin
try {
    hs.endpoint.get("invalid_id")
} catch (e: AuthenticationError) {
    println("Invalid API key")
} catch (e: NotFoundError) {
    println("Endpoint not found")
} catch (e: RateLimitError) {
    println("Rate limited, retry after ${e.retryAfter}s")
} catch (e: ValidationError) {
    println("Validation error: ${e.detail}")
}
```

## License

MIT — see [LICENSE](LICENSE) for details.
