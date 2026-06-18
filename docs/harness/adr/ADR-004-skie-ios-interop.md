# ADR-004: SKIE for iOS Interop

**Status:** Accepted
**Date:** 2026-06-18

## Context

KMP compiles Kotlin to a native framework for iOS, but the Kotlin/Native interop has significant limitations for coroutines:
- `suspend` functions are not directly callable from Swift without extra wrappers
- `Flow<T>` and `SharedFlow<T>` have no direct Swift equivalent — they require manual bridging to `AsyncStream` or callbacks
- Raw Kotlin interop generates verbose, unidiomatic Swift APIs

Without a bridging tool, every Flow collection from Swift would require manual `AsyncStream` adapters and explicit scope management — error-prone and verbose.

Two options were evaluated:
1. Write manual Swift wrappers per flow — full control, no extra dependency, very verbose
2. Use SKIE (Swift Kotlin Interface Enhancer) — generates idiomatic Swift APIs automatically

## Decision

We chose **SKIE** as the Kotlin/Swift interop layer.

SKIE transforms KMP's generated Swift API at compile time:
- `StateFlow<T>` becomes a Swift `@Published` property via `KotlinViewAdapter`
- `SharedFlow<T>` can be collected with `collectAsAsyncStream()` inside a `.task { }` modifier
- `sealed class` hierarchies become Swift `enum`-like types with `onEnum(of:)` for exhaustive switching

Pattern in use:

```swift
.task {
    for await event in collectAsAsyncStream(adapter.viewModel.uiEvent) {
        switch onEnum(of: event) {
        case .navigateToDashboard: onNavigateToDashboard()
        case .showMessage(let data): alertText = data.message.message
        }
    }
}
```

`.task` is cancelled automatically when the view disappears — no manual scope management.

## Consequences

**Easier:**
- Flow collection in Swift is idiomatic and lifecycle-safe
- Sealed class exhaustiveness is enforced at the Swift call site
- No manual `AsyncStream` wrappers to maintain

**Harder:**
- SKIE version must be kept in sync with the KMP version — check release notes on KMP upgrades
- Compile times are slightly longer (SKIE runs at compile time)
- SKIE is a third-party dependency; if it becomes unmaintained, we need a migration plan

**Common mistake to avoid:** Managing `CoroutineScope` manually in Swift. `KotlinViewAdapter` creates and cancels the scope on `deinit`. Creating a second scope or cancelling the adapter's scope manually causes double-cancel bugs or memory leaks.
