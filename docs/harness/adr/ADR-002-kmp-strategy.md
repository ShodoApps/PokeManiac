# ADR-002: Kotlin Multiplatform (KMP) Strategy + Phase Plan

**Status:** Accepted
**Date:** 2026-06-18

## Context

PokeManiac targets both Android and iOS. Initially it was Android-only. Adding iOS as a native SwiftUI app meant either:
1. Rewriting all business logic in Swift (duplication, two bugs for every one bug fixed)
2. Wrapping the Android app in a WebView (no native feel)
3. Sharing Kotlin code via KMP

The app already had a clean domain/data/presentation separation (see ADR-001), which made KMP migration possible without rewriting from scratch.

## Decision

We chose Kotlin Multiplatform, migrating module by module in a defined phase plan:

| Phase | What moved to KMP | Status |
|---|---|---|
| A–C | `:shared:domain` — interfaces + entities | ✅ Done |
| D | `:shared:api`, `:shared:data`, `:shared:database`, `:shared:tracking` | ✅ Done |
| E | `:shared:di` — Koin modules in commonMain | ✅ Done |
| F | `:shared:presentation` — ScreenModels + UiState | ✅ Done |
| G | iOS SwiftUI app consuming shared ScreenModels via SKIE | ⏳ In progress |

Each phase was designed to be independently shippable — Android continued working throughout.

This ADR is the authoritative reference for the KMP phase plan.

## Consequences

**Easier:**
- Business logic bugs are fixed once and fixed on both platforms
- iOS gets the same data layer as Android — no separate Swift networking or persistence
- Domain and ScreenModel tests run on JVM (fast, no emulator) for both platforms

**Harder:**
- Build configuration is more complex: `commonMain`, `androidMain`, `iosMain` source sets
- Some Android-only APIs (e.g. `Context`) cannot be used in `commonMain` — use `expect/actual` or move to `androidMain`
- KMP library support is narrower than pure Android; check multiplatform compatibility before adding any new dependency
- Requires SKIE for clean Swift interop with Kotlin Flows (see ADR-004)

**Common mistake to avoid:** Adding `android.*` or `androidx.*` imports in `commonMain` files. The commonMain purity hook catches this. Use `expect/actual` to bridge platform-specific capabilities instead.
