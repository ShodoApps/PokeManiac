# [PROJECT_NAME] — Claude Code Guide

## Engineering Philosophy

Three principles override every other rule in this codebase.

### 1. Pragmatism over perfection
Apply a pattern **only when it solves a real, visible problem**. Every abstraction has a complexity cost.
- If it makes code harder to read for no visible gain → don't apply it
- When in doubt between two solutions, prefer the simpler one

### 2. Readable by a senior Android developer who doesn't know this codebase
They know Kotlin, Jetpack Compose, coroutines, MVVM — but not our project-specific patterns. Complexity that would prompt a code review question is a signal to simplify.

### 3. YAGNI — You Aren't Gonna Need It
Don't implement features or abstractions until they are concretely needed. Three similar lines is better than a premature abstraction. No half-finished implementations.

---

## Golden Rule: Dependency Direction

**Presentation → Domain → Data**

```
feature:* / app / coreui
    ↓ depends on
shared:domain  (interfaces + entities only)
    ↓ implemented by
shared:data / shared:api / shared:database
```

- ❌ Feature modules **never** import from `:shared:data`, `:shared:api`, or `:shared:database`
- ❌ Features never instantiate repository implementations directly
- ✅ Features import from `:shared:domain`, `coreui`, `:shared:presentation`, `:shared:tracking` (only `TrackingRepository` interface from domain, not impl types)
- ✅ All dependencies resolved via Koin DI

---

## KMP Migration Status

Update this section as you migrate:

- ⏳ Phase A–C — `:shared:domain` KMP commonMain + Apple targets
- ⏳ Phase D — `:shared:api`, `:shared:data`, `:shared:database`, `:shared:tracking`
- ⏳ Phase E — `:shared:di` commonMain Koin + `app` bootstrap
- ⏳ Phase F — all features have shared `ScreenModel` in `:shared:presentation`
- ⏳ Phase G — iOS SwiftUI app, Apple targets

---

## Security Rules

- Never log PII or sensitive data (`Log.d`, `println`, analytics events)
- Never store sensitive data in plain `SharedPreferences` or unencrypted `DataStore`

---

## Available Skills

**Plugin skills** (from installed plugins):

| Skill | When to use |
|---|---|
| `/brainstorming` | Starting a new feature, architectural change, or design workshop |
| `/writing-plans` | After brainstorming — creates a step-by-step implementation plan |
| `/code-review` | Before shipping — full review of branch changes |
| `swiftui-pro` | iOS SwiftUI questions and patterns |
| `swift-concurrency-pro` | iOS async/await and concurrency questions |

**Project-local skill** (in `.claude/skills/` — specific to this project):

| Skill | When to use |
|---|---|
| `/write-feature-spec` | Documenting a feature: solo, in grooming, or during a workshop. Interviews you one question at a time → writes `docs/features/<name>.md` |

Full guide: `docs/harness/SETUP.md`

---

## Quick Navigation

| Task | Read |
|---|---|
| Adding a new feature module | `docs/harness/checklists/new-feature.md` |
| Modifying the data layer | `docs/harness/checklists/data-layer.md` |
| Writing or modifying tests | `docs/harness/checklists/unit-tests.md` |
| Code review | `docs/harness/checklists/code-review.md` |
| Compose / ViewModel patterns | `docs/harness/patterns/feature-patterns.md` |
| KMP / ScreenModel patterns | `docs/harness/patterns/kmp-patterns.md` |
| iOS / SwiftUI patterns | `docs/harness/patterns/ios-patterns.md` |
| DI / Koin wiring | `docs/harness/patterns/di-wiring.md` |
| Naming conventions | `docs/harness/patterns/naming-conventions.md` |
| Why a decision was made | `docs/harness/adr/README.md` |
| Feature functional specs | `docs/features/` |
| QA handoff docs | `docs/qa/` |
| Development workflow | `docs/harness/WORKFLOW.md` |

---

## ✅ Before Pushing to Remote

- [ ] `./gradlew testDebugUnitTest` passes
- [ ] `./gradlew lint` passes
- [ ] `./gradlew assembleRelease` passes
- [ ] No hardcoded strings — use `stringResource(R.string.key)`
- [ ] No debug code (`Log.d`, `println`)
- [ ] No TODOs or FIXMEs (or tracked as issues)
- [ ] No dead code or unused imports

## ✅ After Feature Completion

- [ ] Removed debug code and unused imports
- [ ] `docs/harness/adr/` updated if a new architectural pattern was introduced
- [ ] Feature reachable from other features via its navigator
- [ ] QA handoff doc generated: `docs/qa/YYYY-MM-DD-feature-name.md`
