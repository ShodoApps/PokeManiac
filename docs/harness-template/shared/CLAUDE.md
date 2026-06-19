# [PROJECT_NAME] — Shared KMP Modules Guide

> See root `CLAUDE.md` for project-wide rules.
> See `docs/harness/patterns/kmp-patterns.md` for detailed patterns and module roles.

---

## Key Rules

These rules are non-obvious and must always be in context:

- `:shared:presentation` **never** depends on `:shared:data`, `:shared:api`, or `:shared:database` — presentation layer is domain-only
- No `@Immutable` or `@Stable` in `commonMain` — these are Android Compose annotations; add them in `feature:*` and `coreui` only
- DTOs **never** cross the repository interface boundary — map to domain types inside the repository implementation; the interface exposes domain types only

---

## Reference

| Need | Read |
|---|---|
| Module roles, type pipeline (DTO→Domain→UiModel), ScreenModel/ViewModel split, expect/actual | `docs/harness/patterns/kmp-patterns.md` |
| Adding API endpoint, repository, Room entity, DataStore | `docs/harness/checklists/data-layer.md` |
| Writing shared ScreenModel tests (commonTest, manual fakes, `@BeforeTest`) | `docs/harness/checklists/unit-tests.md` |
| Koin wiring for shared modules | `docs/harness/patterns/di-wiring.md` |
| Why KMP decisions were made | `docs/harness/adr/README.md` |
