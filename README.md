# HookSniff Kotlin SDK

<p align="center">
  <a href="https://github.com/servetarslan02/HookSniff"><img src="https://img.shields.io/github/license/servetarslan02/HookSniff" alt="License"></a>
</p>

Kotlin SDK for the [HookSniff](https://hooksniff.vercel.app) webhook delivery platform.

## Installation

```bash
implementation("com.hooksniff:hooksniff:1.0.0")
```

## Quick Start

```kotlin
val client = HookSniff("hs_xxx")
val endpoints = client.endpoint.list()
println(endpoints)
```

## Webhook Verification

```kotlin
val wh = Webhook("whsec_xxx")
val payload = wh.verify(body, headers)
```

## Resources

| Resource | Methods |
|----------|---------|
| Endpoint | list, create, get, update, delete |
| Message | create, list, get |
| MessageAttempt | list, listByMsg, get, resend |
| Authentication | dashboardAccess |
| EventType | list |
| Statistics | aggregate |

## Links

- [Documentation](https://hooksniff.vercel.app/docs)
- [API Reference](https://hooksniff-api-1046140057667.europe-west1.run.app)
- [GitHub](https://github.com/servetarslan02/HookSniff)
