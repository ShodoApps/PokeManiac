# ADR-003: ScreenModel / ViewModel Split

**Status:** Accepted
**Date:** 2026-06-18

## Context

Phase F of the KMP migration moved screen logic to `commonMain` so iOS could reuse it. The problem: `androidx.lifecycle.ViewModel` is Android-only and cannot be in `commonMain`. We needed a way to:
- Keep state management logic in shared code so iOS uses the same logic
- Keep Android's lifecycle management (scoped coroutines, `viewModelScope`, process death handling)
- Not require the iOS app to use any Android lifecycle framework

Several options were considered:
1. Use a third-party multiplatform ViewModel library (Decompose, Voyager) — significant API change, learning curve
2. Put all logic in a plain Kotlin class in `commonMain`; Android wraps it with a thin ViewModel — minimal disruption, uses what we already know

## Decision

We chose option 2: **ScreenModel + thin ViewModel**.

**`*ScreenModel`** (in `:shared:presentation` `commonMain`):
- Plain Kotlin class — no framework dependencies
- Constructor receives a `CoroutineScope` — does not create its own
- Holds `StateFlow<*UiState>` and `SharedFlow<UiError>`; implements all state transitions and user action handling
- Tested with `TestScope` in `commonTest` — no Android, no emulator

**`*ViewModel`** (in `feature:*` Android module):
- Extends `androidx.lifecycle.ViewModel`
- Single responsibility: create the ScreenModel with `viewModelScope`, forward all calls
- `private val screenModel by lazy { factory.create(viewModelScope) }`
- No business logic lives here

**ScreenModelFactory** (`fun interface`):
- Allows Koin to supply repository dependencies at factory registration time
- `create(coroutineScope: CoroutineScope): *ScreenModel`
- Android ViewModel calls `factory.create(viewModelScope)` at first access

**iOS**:
- `KotlinViewAdapter<ScreenModel, UiState>` manages a `CoroutineScope` and exposes `@Published` state
- iOS constructs ScreenModel directly via the factory lambda — no ViewModel

Reference implementation: `SearchFriendScreenModel`, `SearchFriendViewModel`, `SearchFriendScreenModelFactory`.

## Consequences

**Easier:**
- ScreenModel logic is tested in `commonTest` with pure Kotlin — fast, no instrumentation
- iOS reuses the exact same state transitions as Android
- Adding a new action or state requires touching only the ScreenModel and the platform-specific View

**Harder:**
- Two files per screen instead of one (`ScreenModel` + `ViewModel`)
- Must remember to pass `coroutineScope` into ScreenModel, not create one inside it
- The `fun interface` factory pattern requires a specific Koin registration — see `docs/harness/patterns/di-wiring.md`

**Common mistake to avoid:** Putting business logic in the ViewModel instead of the ScreenModel. The ViewModel must stay a thin shell — if it has a `when` expression or a `try/catch`, the logic belongs in the ScreenModel.
