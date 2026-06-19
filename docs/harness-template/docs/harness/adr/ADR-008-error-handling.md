# ADR-008: Error Handling Strategy (UiError)

**Status:** Accepted
**Date:** 2026-06-18

## Context

Screens receive errors from multiple sources: network failures, database errors, parsing failures, business rule violations. Before this decision, each ViewModel emitted errors differently:
- Some emitted raw exceptions: `SharedFlow<Exception>`
- Some emitted string messages: `SharedFlow<String>`
- Some put error state in the `UiState` sealed class

The inconsistency caused screens to handle errors differently, and one-shot events encoded as `UiState.Error` were sometimes dropped (StateFlow conflation) when two errors fired in quick succession.

## Decision

We chose a **unified `UiError` type** emitted on a dedicated `SharedFlow`.

```kotlin
data class UiError(val message: String)

// In every ScreenModel:
private val _error = MutableSharedFlow<UiError>()
val error = _error.asSharedFlow()

// In catch blocks:
catch (e: CancellationException) { throw e }   // always rethrow
catch (e: Exception) { _error.emit(UiError.from(e)) }
```

`UiError.from(e)` maps any exception to a non-null message. The Screen collects errors via `observeWithLifecycle()` and shows them in a `SnackbarHost`.

`UiState` sealed classes never contain an Error branch for transient errors — only for persistent empty/error states that the whole screen should show.

## Consequences

**Easier:**
- Error display is consistent across all screens (always a Snackbar)
- `SharedFlow` with `replay = 0` ensures errors are never dropped or conflated
- Tests can assert `error.message` without knowledge of the original exception type

**Harder:**
- Must always rethrow `CancellationException` — catching it breaks structured concurrency silently. The architecture guard does not catch this; developers must remember it. `feature/CLAUDE.md` lists it as a key rule.
- `observeWithLifecycle()` must be called in the Screen, not the View — errors must be observed at the lifecycle-aware layer

**Common mistake to avoid:** Collecting error flow with `collect()` instead of `observeWithLifecycle()`. `collect()` does not pause when the app goes to background, which can cause Snackbars to show on resume from a state that no longer applies. Always use `observeWithLifecycle()` from `coreui.extensions`.
