# ADR-005: Sealed UiState + Single Source of Truth

**Status:** Accepted
**Date:** 2026-06-18

## Context

Early screens modelled their UI state with multiple boolean flags on a data class:

```kotlin
data class SearchFriendUiState(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val isEmpty: Boolean = false,
    val users: List<User> = emptyList()
)
```

This caused several problems:
- Impossible states were representable: `isLoading = true` and `hasError = true` simultaneously
- `when` expressions couldn't be exhaustive — you had to decide which flag wins
- Compose's recomposition skipping couldn't infer stability (all those `var`/`val` combinations)
- Tests had to assert combinations of flags rather than states

## Decision

We chose **sealed class `UiState`** as the exclusive state container.

```kotlin
@Immutable
sealed class SearchFriendUiState {
    data object Loading : SearchFriendUiState()
    data object EmptySearch : SearchFriendUiState()
    data class Data(val users: PersistentList<SearchFriendUiModel>) : SearchFriendUiState()
}
```

Rules:
- One `StateFlow<*UiState>` per screen — the single source of truth
- `@Immutable` on the sealed class (Android side only, never in `commonMain`) — enables Compose recomposition skipping
- `PersistentList<>` for all collection fields — `kotlinx.collections.immutable`, stable and `@Immutable`-compatible
- One-shot events (errors, navigation triggers) go on a **separate** `SharedFlow<UiError>` — they are not state

## Consequences

**Easier:**
- Impossible states are unrepresentable — the compiler enforces exhaustiveness in `when` expressions
- Compose can skip recomposition when state hasn't changed (stability guaranteed by `@Immutable`)
- Test assertions are clean: `assertEquals(SearchFriendUiState.Loading, awaitItem())`

**Harder:**
- Adding a field requires touching the sealed class definition (intentional — forces deliberate state design)
- Must remember to use `PersistentList` not `List` — plain `List<>` breaks `@Immutable`
- `@Immutable` belongs only on the Android side; `commonMain` UiState classes are plain Kotlin

**Common mistake to avoid:** Encoding one-shot events (snackbar messages, navigation) as state fields. When two events fire in quick succession, a state field will miss one because `StateFlow` conflates equal values. Use `SharedFlow<UiError>` with `replay = 0` for events, and collect with `observeWithLifecycle()`.
