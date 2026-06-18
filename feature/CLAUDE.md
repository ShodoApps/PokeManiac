# PokeManiac — Feature Module Guide

> See root `CLAUDE.md` for project-wide architecture rules.
> See `docs/harness/patterns/feature-patterns.md` for detailed patterns with full code examples.

---

## Key Rules

These rules are non-obvious and must always be in context:

- `@Immutable` on all sealed `UiState` classes and UI model `data class` — **Android side only**, never in `commonMain`
- `PersistentList<>` for all collection fields — never plain `List<>` in UI models
- `CancellationException` must **always** be rethrown — catching and swallowing it breaks structured concurrency
- `observeWithLifecycle()` for all `SharedFlow` collection — never `collect()` directly
- Never reference `Context` or `Activity` in a `ViewModel` or `ScreenModel` — causes memory leaks in Compose

---

## Reference

| Need | Read |
|---|---|
| Screen/View pattern, ViewModel, coroutines, recomposition, navigation, previews | `docs/harness/patterns/feature-patterns.md` |
| DI / Koin module wiring | `docs/harness/patterns/di-wiring.md` |
| Adding a new feature module (full setup + registration checklist) | `docs/harness/checklists/new-feature.md` |
| Writing unit tests (Turbine, Mockito, SharedFlow timing) | `docs/harness/checklists/unit-tests.md` |
| Architecture compliance + code review checklist | `docs/harness/checklists/code-review.md` |
| Naming conventions | `docs/harness/patterns/naming-conventions.md` |
