# Architecture Decision Records

ADRs document the reasoning behind architectural decisions in [PROJECT_NAME]. Before questioning or changing an architectural decision, check the index below.

---

## Format: Nygard Standard

The [Nygard format](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions) — de-facto industry standard, recognised by senior engineers, no learning curve.

**Template** (copy when writing a new ADR):

```markdown
# ADR-XXX: [Title]

**Status:** Accepted | Deprecated | Superseded by ADR-YYY
**Date:** YYYY-MM-DD

## Context
What problem, constraint, or situation made this decision necessary.

## Decision
What was chosen. Active voice: "We chose X."

## Consequences
What becomes easier and what becomes harder as a result.
Include common mistakes to avoid — this is what helps Claude not undo the decision.
```

---

## Index

| ADR | Decision | Read when… |
|---|---|---|
| [ADR-001](ADR-001-clean-architecture.md) | Clean Architecture + SOLID (domain has zero deps) | Touching module dependencies |
| [ADR-002](ADR-002-kmp-strategy.md) | KMP strategy + phase plan | Working in any `shared/*` module |
| [ADR-003](ADR-003-screenmodel-viewmodel-split.md) | ScreenModel/ViewModel split | Adding or modifying a ViewModel |
| [ADR-004](ADR-004-skie-ios-interop.md) | SKIE for iOS interop | Working in `iosApp/` |
| [ADR-005](ADR-005-sealed-uistate.md) | Sealed UiState + Single Source of Truth | Designing a new UiState |
| [ADR-006](ADR-006-room-kmp-persistence.md) | Room for KMP persistence | Modifying the database layer |
| [ADR-007](ADR-007-platform-targets.md) | Platform & Form Factor Targets | Building any UI screen |
| [ADR-008](ADR-008-error-handling.md) | Error handling strategy (UiError) | Handling errors in ViewModel/ScreenModel |
| [ADR-009](ADR-009-offline-cache-strategy.md) | Offline/cache strategy | Modifying repository or database layer |
| [ADR-010](ADR-010-accessibility-baseline.md) | Accessibility baseline | Building any UI screen |
| [ADR-011](ADR-011-logging-policy.md) | Logging policy | Writing any `Log.*` call or analytics event |
| [ADR-012](ADR-012-privacy-data-handling.md) | Privacy & data handling | Handling user data or billing |

---

## Deferred ADR Backlog (write when the feature is being implemented)

| Topic | Write when… |
|---|---|
| Authentication & session management | Implementing auth flows |
| Build variants / environments | Setting up multi-environment builds |
| Analytics & crash reporting | Integrating analytics tooling |
