# ADR-012: Privacy & Data Handling

**Status:** Accepted
**Date:** 2026-06-18

## Context

[PROJECT_NAME] stores and transmits personal data (exact types depend on your feature set). This data is subject to GDPR (EU users) and similar regulations.

We needed baseline decisions on:
- Where user data is stored locally and how it is protected
- What data is sent to third-party services
- How to handle data deletion requests

## Decision

**Local storage:**
- `SharedPreferences` and unencrypted `DataStore` must **not** store sensitive user data (auth tokens, personal identifiable data)
- Room is acceptable for caching public user data (usernames, profile photos, transaction history) — it is not encrypted by default, which is acceptable for cached public data but not for auth credentials
- Auth tokens (when implemented) must use `EncryptedSharedPreferences` or `DataStore` with `EncryptedFile` backing

**Data in transit:**
- All API communication uses HTTPS — enforced at the network layer (Ktor configuration)
- No plaintext HTTP permitted, including in debug builds (no `android:usesCleartextTraffic="true"`)

**Third-party services:**
- No PII sent to analytics or crash reporting services as event properties or identifiers (see ADR-011)
- Any third-party SDK integrated must have a privacy-compliant data processing agreement

**Data deletion:**
- When a user deletes their account (future feature), the following must be cleared locally: Room database, DataStore, any cached files. The server-side deletion is the responsibility of the backend team.
- No data retention beyond what is needed for the feature — do not cache data that isn't displayed

**Photo access (iOS):**
- Use `PHPickerViewController` for photo selection — requires no permission and gives access only to selected photos (see `docs/harness/patterns/ios-patterns.md`)
- Do not request full photo library access unless a specific feature explicitly requires browsing the full library

## Consequences

**Easier:**
- GDPR baseline maintained without a dedicated privacy team
- PHPicker eliminates the most common iOS privacy complaint (full photo library access prompt)

**Harder:**
- Auth token storage (when implemented) requires `EncryptedSharedPreferences` — slightly more complex than plain `SharedPreferences`
- Must audit each new third-party SDK for data collection before integration

**Common mistake to avoid:** Storing the user's auth token in plain `SharedPreferences` because it's convenient. This makes the token extractable from a rooted device or from an ADB backup. Always use encrypted storage for credentials. The security rule in `CLAUDE.md` covers this: "Never store sensitive data in plain `SharedPreferences` or unencrypted `DataStore`."
