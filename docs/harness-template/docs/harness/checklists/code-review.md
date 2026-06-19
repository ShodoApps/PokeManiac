# Checklist: Code Review

> Run this before marking a PR ready for review.
> Hooks catch many of these automatically — this list covers what hooks cannot detect.

---

## Architecture & Code Quality

### Architecture compliance
- [ ] Features import from `domain`, `coreui`, `presentation`, `tracking` only
- [ ] ❌ No imports from `:shared:data`, `:shared:api`, `:shared:database`
- [ ] UI state is a sealed class — no boolean flags
- [ ] `StateFlow` private mutable, public immutable
- [ ] `SharedFlow` for one-shot events (errors, navigation)
- [ ] Screen: `collectAsStateWithLifecycle`, `observeWithLifecycle`, `OnLifecycleEventEffect`
- [ ] View: no state collection, no side effects, pure UI
- [ ] `@Immutable` on `sealed class UiState` and UI model `data class` (Android side)
- [ ] `PersistentList<>` for all collection fields in UI models
- [ ] All `SharedFlow` collected via `observeWithLifecycle()` from coreui

### Code review
- [ ] No circular dependencies between modules
- [ ] Dependency direction respected: Presentation → Domain → Data
- [ ] No direct imports between feature modules (only via navigator interfaces)
- [ ] `UiError` used (not raw `Exception`); `error.message` in snackbar (not `.toString()`)
- [ ] `OnLifecycleEventEffect` used (not raw `DisposableEffect + LifecycleEventObserver`)
- [ ] `CancellationException` always rethrown in catch blocks
- [ ] Recomposition Tier 1 applied everywhere
- [ ] Recomposition Tier 2 applied on your Tier 2 screens (LazyList/LazyGrid/animations)
- [ ] No lazy layout nested inside a scrollable container
- [ ] Navigation routes carry IDs only — no display data
- [ ] Preview composables for all UiState branches
- [ ] Tests follow Given / When / Then; SharedFlow subscribed before trigger
