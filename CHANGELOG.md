# Changelog

## 1.2.0 (2026-05-18)

### Breaking Changes
- **API paths changed**: All endpoints now use `/v1/...` instead of `/api/v1/...` (HookSniff native format)
- **Package consistency**: All files now use `com.hooksniff.kotlin` package

### New Features
- **Environment** resource — Manage environments (dev/staging/prod) and their variables
- **BackgroundTask** resource — List, get, and cancel async background tasks
- **OperationalWebhook** resource — Manage operational webhook endpoints and delivery logs
- **MessagePoller** resource — Long-polling consumer API (poll, seek, commit)
- **Inbound** resource — Inbound webhook proxy configurations
- **Connector** resource — Third-party connector management (Shopify, Stripe, Discord, etc.)
- **Integration** resource — Full integration CRUD with test, events, and stats
- **Stream** resource — Real-time streaming channels, subscriptions, and message publishing
- **Webhook.verify(Map)** — Verify signatures with `Map<String, String>` headers

### New Models
- `IntegrationIn`, `IntegrationOut`, `IntegrationUpdate`, `IntegrationEventOut`, `IntegrationStatsOut`, `IntegrationTestResponse`
- `StreamChannelIn`, `StreamChannelOut`, `StreamChannelDetailOut`, `StreamChannelUpdate`, `StreamMessageOut`, `StreamSubscriptionOut`
- `PublishEventIn`, `PublishEventResponse`
- `BackgroundTaskStatus`, `BackgroundTaskType`
- `ApplicationIn`, `AppPortalAccessIn`, `AppPortalAccessOut`, `BulkReplayIn`, `RecoverIn`, `RecoverOut`, `ReplayIn`, `ReplayOut`
- `StreamPortalAccessIn`, `StreamTokenExpireIn`, `DashboardAccessOut`

### Bug Fixes
- Fixed package name inconsistency (`com.hooksniff` → `com.hooksniff.kotlin`)
- Fixed API paths from Svix pattern to HookSniff pattern
- Fixed `WebhookTest` to use correct exception class
- Fixed `.com` domain references to `hooksniff.vercel.app`

## 1.1.0 (2026-05-17)

- Initial release
- Core resources: Endpoint, Message, MessageAttempt, EventType, Authentication, Statistics, Health
- Webhook signature verification (HMAC-SHA256)
- Automatic retry with exponential backoff
- Rate limit handling (429)

## 1.0.0 (2026-05-10)

- Initial SDK based on Svix SDK
