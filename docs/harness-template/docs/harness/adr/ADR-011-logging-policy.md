# ADR-011: Logging Policy

**Status:** Accepted
**Date:** 2026-06-18

## Context

Development logging (`Log.d`, `println`) is essential during development but harmful in production:
- Logs can capture PII (user IDs, email addresses, names, transaction data) that appears in bug reports and device logs accessible to the OS or third-party tools
- Excessive logging degrades performance on low-end devices
- Logs left in production violate GDPR and similar privacy regulations if they capture personal data

We also needed a policy on analytics events: what to track, what never to track.

## Decision

**Development logs:**
- `Log.d`, `Log.e`, `Log.i`, `Log.w`, `Log.v`, `println` are permitted in development but must be **removed before any PR is merged**
- The debug log detector hook fires on any `.kt` file edit (excluding test files) and flags these calls
- No logging library (Timber, etc.) is used currently — if added, it must support production build stripping

**What must never appear in any log, at any level, in any build:**
- User email addresses
- User names or usernames
- Friend lists or social graph data
- Transaction amounts, card names, or trade history
- Auth tokens, session IDs, or any credentials
- Device identifiers beyond what the OS platform provides

**Analytics events:**
- Analytics events (when integrated — see ADR backlog) must not include PII as event properties
- Allowed: event name, timestamp, screen name, action type, anonymous feature flags
- Forbidden: user-identifiable fields in event properties

**Crash reports:**
- Crash reporting tools (Crashlytics, Sentry — when integrated) must be configured to scrub PII from stack traces and breadcrumbs before upload

## Consequences

**Easier:**
- GDPR compliance baseline maintained by policy + hook enforcement
- No PII leak risk from development logs left in production

**Harder:**
- Developers must remove all logging before PR — the hook is a reminder, not a blocker; code review is the final gate
- Debugging production issues requires structured logging or remote config rather than `Log.d`

**Common mistake to avoid:** Using `Log.d("Auth", "token=${token}")` or `Log.d("User", "email=${user.email}")`. Even debug-level logs appear in `adb logcat` output and may be captured by third-party SDKs. If you need to trace a value, use a local breakpoint during development, not a log statement.
