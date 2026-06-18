# PokeManiac — Harness v2 Design Spec

**Date:** 2026-06-18
**Status:** Approved

---

## Goal

Reduce context rot and maintenance burden in the Claude Code harness by splitting the existing CLAUDE.md files into a lean always-loaded core and an on-demand reference library. Add an ADR system, a documented development workflow, improved hooks, and mobile best-practice rules missing from v1.

**Primary pain points addressed:**
- Root CLAUDE.md and feature/CLAUDE.md too large — everything always loaded regardless of task
- No documented reasoning behind architectural decisions (Claude may question or undo them)
- Hooks written while learning — contain bugs and gaps
- No SwiftLint equivalent for iOS files
- Missing mobile best-practice rules (memory leaks, PII in logs, permissions, ProGuard)
- No written functional specs for existing features
- No QA handoff document generated at end of feature implementation

---

## Chosen Approach: Lean Core + Reference Library

CLAUDE.md files become rules + navigation only. `docs/harness/` is the reference library. Claude reads reference files when the task calls for it, guided by navigation pointers in CLAUDE.md. No explicit trigger phrases required — file pointers in the Quick Navigation table are sufficient.

**Why this over alternatives:**
- Explicit trigger tables (Approach B) require maintaining a trigger for every task type — more maintenance than the problem it solves
- Keeping the current structure trimmed (Approach C) doesn't address the root issue: checklists and patterns are always loaded even when irrelevant

---

## File Structure

```
PokeManiac/
├── CLAUDE.md                            ~65 lines  (was 282)
├── feature/CLAUDE.md                    ~50 lines  (was 455)
├── shared/CLAUDE.md                     ~45 lines  (was 296)
├── iosApp/CLAUDE.md                     ~35 lines  (was 166)
│
├── README.md                            Add "## AI Harness" section
│
└── docs/
    ├── features/                        Functional specs — one per feature
    │   ├── _TEMPLATE.md                 Template for new functional specs
    │   ├── welcome.md
    │   ├── dashboard.md
    │   ├── search-friend.md
    │   ├── my-friends.md
    │   ├── my-profile.md
    │   ├── post-transaction.md
    │   └── billing.md
    ├── qa/                              QA handoff documents — one per feature/fix
    │   └── YYYY-MM-DD-feature-name.md
    └── harness/
        ├── SETUP.md                     Human onboarding — plugins, settings, workflow entry point
        ├── WORKFLOW.md                  AI-driven development loop
        ├── adr/
        │   ├── README.md               ADR index + format rationale + template + backlog
        │   ├── ADR-001-clean-arch.md
        │   ├── ADR-002-kmp-strategy.md
        │   ├── ADR-003-screenmodel-viewmodel.md
        │   ├── ADR-004-skie.md
        │   ├── ADR-005-sealed-uistate.md
        │   ├── ADR-006-room-persistence.md
        │   ├── ADR-007-platform-targets.md
        │   ├── ADR-008-error-handling.md
        │   ├── ADR-009-offline-cache-strategy.md
        │   ├── ADR-010-accessibility.md
        │   ├── ADR-011-logging-policy.md
        │   └── ADR-012-privacy.md
        ├── checklists/
        │   ├── new-feature.md
        │   ├── data-layer.md
        │   ├── unit-tests.md
        │   ├── code-review.md
        │   └── qa-handoff-template.md
        └── patterns/
            ├── feature-patterns.md
            ├── kmp-patterns.md
            ├── ios-patterns.md
            ├── di-wiring.md
            └── naming-conventions.md
```

**Always-loaded context:** ~195 lines total across 4 CLAUDE.md files (down from ~1200).

---

## Section 1 — CLAUDE.md Files

### What stays vs. moves

The rule: only content Claude must never operate without stays in CLAUDE.md. Everything else moves to `docs/harness/`.

#### Root `CLAUDE.md` (~65 lines)

**Stays:**
- Engineering philosophy: pragmatism over perfection, readability by a senior dev who doesn't know the codebase, YAGNI (new — was implicit, now named explicitly)
- Golden rule: Presentation → Domain → Data with ❌/✅ examples
- KMP migration current status (5 lines)
- Available skills section (new): superpowers, swiftui-pro, swift-concurrency-pro — what they are and when to invoke
- Quick navigation table (rewritten as file pointers to `docs/harness/`)
- ✅ Before-push checklist (already short — keep)
- ✅ After-feature checklist (already short — keep)
- Security rules (new): no PII in logs, no sensitive data in plain SharedPreferences/DataStore

**Moves:**
- Module architecture tree → `docs/harness/patterns/naming-conventions.md`
- Naming conventions table → `docs/harness/patterns/naming-conventions.md`
- State management pattern → `docs/harness/patterns/feature-patterns.md`
- Screen/View composition overview → `docs/harness/patterns/feature-patterns.md`
- Koin DI wiring → `docs/harness/patterns/di-wiring.md`
- Tracking snippet → `docs/harness/patterns/feature-patterns.md`

**Removed:**
- Key libraries table (derivable from `build.gradle.kts`)
- i18n / dark mode (1–2 lines, obvious from existing code)
- Troubleshooting section (Claude reads the code)

#### `feature/CLAUDE.md` (~50 lines)

**Stays (4 non-obvious rules):**
- `@Immutable` on sealed UiState and UI model data classes — Android side only, not in commonMain
- `PersistentList<>` for all collection fields — never plain `List<>`
- `CancellationException` must always be rethrown — never swallow
- `observeWithLifecycle()` for all SharedFlow — never `collect()`
- No `Context` or `Activity` reference in ViewModel or ScreenModel (new — memory leak)
- Navigation pointer to `docs/harness/patterns/feature-patterns.md` and `docs/harness/checklists/`

**Moves:**
- All code examples → `docs/harness/patterns/feature-patterns.md`
- Screen vs View pattern (detailed) → `docs/harness/patterns/feature-patterns.md`
- ViewModel patterns → `docs/harness/patterns/feature-patterns.md`
- Coroutine dispatcher rules → `docs/harness/patterns/feature-patterns.md`
- Recomposition optimization tiers → `docs/harness/patterns/feature-patterns.md`
- State collection patterns → `docs/harness/patterns/feature-patterns.md`
- Type-safe navigation → `docs/harness/patterns/feature-patterns.md`
- Preview patterns → `docs/harness/patterns/feature-patterns.md`
- All 4 checklists → `docs/harness/checklists/`

#### `shared/CLAUDE.md` (~45 lines)

**Stays (3 rules):**
- `:shared:presentation` never depends on `:shared:data / :shared:api / :shared:database`
- No `@Immutable` / `@Stable` in `commonMain` — Android side only
- DTOs never cross the repository interface boundary
- Navigation pointer to `docs/harness/patterns/kmp-patterns.md` and `docs/harness/checklists/data-layer.md`

**Moves:**
- Module roles table → `docs/harness/patterns/kmp-patterns.md`
- Type pipeline (DTO → Domain → UiModel) → `docs/harness/patterns/kmp-patterns.md`
- ScreenModel vs ViewModel detail → `docs/harness/patterns/kmp-patterns.md`
- UiState/UiModel in commonMain → `docs/harness/patterns/kmp-patterns.md`
- KMP dependency rules (detailed) → `docs/harness/patterns/kmp-patterns.md`
- expect/actual guidance → `docs/harness/patterns/kmp-patterns.md`
- Data layer checklist → `docs/harness/checklists/data-layer.md`

#### `iosApp/CLAUDE.md` (~35 lines)

**Stays (2 rules + reference table):**
- Never manage `CoroutineScope` manually — `KotlinViewAdapter` owns it
- `.task` handles lifecycle — no manual `onDisappear` cancellation needed
- Reference screens table: Welcome / SearchFriend / Dashboard → which Swift file to read
- Navigation pointer to `docs/harness/patterns/ios-patterns.md`

**Moves:**
- KotlinViewAdapter detail → `docs/harness/patterns/ios-patterns.md`
- SKIE flow collection → `docs/harness/patterns/ios-patterns.md`
- Screen/View code examples → `docs/harness/patterns/ios-patterns.md`
- iOS composition helper (Koin deps for ScreenModel) → `docs/harness/patterns/ios-patterns.md`

---

## Section 2 — ADR System

### Format: Nygard standard

The Nygard format was chosen because it is the de-facto industry standard for ADRs, widely recognised by senior engineers, and requires no learning curve for new team members. It is intentionally minimal: no scoring matrices, no alternatives section, no approval workflows.

**Template:**

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

### ADR index (`docs/harness/adr/README.md`)

The README opens with the format rationale and template, then lists the index. Claude reads the index to find the right ADR without reading all of them.

```markdown
| ADR | Decision | Read when… |
|---|---|---|
| ADR-001 | Clean Architecture + SOLID | touching module dependencies |
| ADR-002 | KMP strategy + phase plan | working in any shared/* module |
| ADR-003 | ScreenModel/ViewModel split | adding or modifying a ViewModel |
| ADR-004 | SKIE for iOS interop | working in iosApp/ |
| ADR-005 | Sealed UiState + Single Source of Truth | designing a new UiState |
| ADR-006 | Room for KMP persistence | modifying the database layer |
| ADR-007 | Platform & Form Factor Targets | building any UI screen |
| ADR-008 | Error handling strategy (UiError) | handling errors in ViewModel/ScreenModel |
| ADR-009 | Offline/cache strategy | modifying repository or database layer |
| ADR-010 | Accessibility baseline | building any UI screen |
| ADR-011 | Logging policy | writing any Log.* call or analytics event |
| ADR-012 | Privacy & data handling | handling user data or billing |
```

### ADR content notes

**ADR-001 — Clean Architecture:**
- Context must reference the key Clean Architecture and SOLID principles motivating the decision, especially Dependency Inversion (D) and Single Responsibility (S)
- Decision must state that domain has **zero dependencies** on any other module
- Consequences must include: "every Repository interface must have a corresponding test class"

**ADR-005 — Sealed UiState:**
- Single Source of Truth principle belongs here (not in ADR-001) — it applies specifically to UI state management

**ADR-007 — Platform & Form Factor Targets:**
- Specify Android minimum API level and iOS minimum version
- Smartphone portrait only: no landscape, no tablet, no foldable
- UI must be responsive for small screens: scrollable content, no clipped text, no fixed-height containers

### ADR Backlog

Three ADRs deferred until the relevant feature is implemented. Write each one just before or just after implementing the corresponding feature:

| Topic | Write when… |
|---|---|
| Authentication & session management | implementing auth flows |
| Build variants / environments | setting up multi-environment builds |
| Analytics & crash reporting | integrating analytics tooling |

---

## Section 3 — WORKFLOW.md

### Development loop

```
brainstorm → spec → plan → implement → test gate → review → ship → retrospective
     ↑                                                                     |
     └─────────────────── loop if new pattern emerged ────────────────────┘
```

### Steps

| Step | Tool / Skill | Output | Done when… |
|---|---|---|---|
| Brainstorm | `/brainstorming` skill | Approved design in conversation | User explicitly approves the design |
| Spec | `/brainstorming` (continues) | `docs/superpowers/specs/YYYY-MM-DD-topic-design.md` | User reviews and approves the spec file |
| Plan | `/writing-plans` skill | `docs/superpowers/plans/YYYY-MM-DD-topic.md` | Plan written and ready |
| Implement | Subagents or direct | Code changes | All plan tasks checked off |
| Test gate | `./gradlew testDebugUnitTest` + `./gradlew lint` | Green build | Tests pass, lint clean, no ktlint/SwiftLint violations, no arch hook warnings |
| Review | `/code-review` or manual | Feedback addressed | No blocking issues |
| Ship | User commits + pushes | Commit on remote | User pushes (Claude never commits) |
| Retrospective | Manual | New/updated ADR if needed | "Did this introduce a new pattern?" → if yes, write ADR |

### Rules

- Never start implementing before the spec is approved
- Never skip brainstorm for a non-trivial feature (anything touching more than 2 files)
- The test gate is not optional — a feature is not done until `testDebugUnitTest` + `lint` pass
- Claude never commits or pushes — the user owns the git history
- If a feature required a workaround or introduced a new architectural pattern, the retrospective step creates or updates an ADR

---

## Section 4 — Hooks Redesign

Current hooks were written while learning the hooks system and contain several issues. The desired behavior is defined here; implementation details (exact shell commands) belong in the implementation plan.

### Additional hooks from mobile community best practices

Beyond fixing the existing hooks, the following new hooks are added based on what senior Android/KMP/iOS engineers consistently guard against:

| Hook | Trigger | Why it matters |
|---|---|---|
| Hardcoded string detector | PostToolUse Edit/Write on `.kt` in `feature/` or `coreui/` | Claude frequently forgets `stringResource()` — detects string literals that belong in `strings.xml` |
| commonMain purity check | PostToolUse Edit/Write on `src/commonMain/**/*.kt` | Detects `android.*`, `androidx.*`, `Context` imports leaking into shared KMP code |
| Room migration reminder | PostToolUse Edit/Write on `*Base.kt` or `*Dao.kt` | Changing an entity without bumping `@Database(version)` crashes on upgrade |
| ViewModel memory leak check | PostToolUse Edit/Write on `*ViewModel.kt` | Detects `Context` / `Activity` references — silent memory leak in Compose |
| Missing test reminder | PostToolUse Write on new `*ViewModel.kt` or `*ScreenModel.kt` | Reminds that a corresponding `*Test.kt` should be created |
| Debug log detector | PostToolUse Edit/Write on `.kt` | Flags `Log.d`, `Log.e`, `println` left in production code |
| Info.plist permission check | PostToolUse Edit/Write on `.swift` referencing permission APIs | Reminds to add `NS*UsageDescription` — missing key causes crash on device |

### Current issues

| Hook | Problem |
|---|---|
| ktlint | Path hardcoded to `/usr/local/bin/ktlint` — breaks on other machines |
| Architecture check | Only fires on `Edit`, not `Write` — bypassed by Write calls |
| Architecture check | Only checks `feature/` — `coreui/` is also a presentation module |
| Gradle reminder | Uses wrong output format (`hookSpecificOutput.additionalContext`) — unreliable |
| Gradle reminder | Missing `./gradlew lint` in the reminder |
| SwiftLint | No equivalent hook for `.swift` files |

### Desired hook behavior

| Hook | Trigger | Desired behavior |
|---|---|---|
| No-commit | PreToolUse — Bash | Block any `git commit` attempt. Keep current logic. |
| ktlint | PostToolUse — Edit + Write on `.kt` | Auto-format, then report remaining violations. Resolve ktlint path dynamically (`which ktlint`). |
| SwiftLint | PostToolUse — Edit + Write on `.swift` | Auto-fix, then report remaining violations. Requires SwiftLint installed in project. |
| Architecture guard | PostToolUse — Edit + Write on `.kt` in `feature/` or `coreui/` | Detect imports from `:shared:data`, `:shared:api`, `:shared:database`. Output via `systemMessage`. |
| Gradle change | PostToolUse — Edit + Write on `build.gradle.kts` | Remind to run `testDebugUnitTest` + `lint` + `assembleRelease`. Output via `systemMessage`. |

---

## Section 5 — Mobile Best Practices Additions

Rules and checklist items from the Android/iOS/KMP community that were missing from v1.

### New always-loaded rules

**Root `CLAUDE.md`:**
- Never log PII or sensitive data (`Log.d`, `println`, analytics events)
- Never store sensitive data in plain `SharedPreferences` or unencrypted `DataStore`

**`feature/CLAUDE.md`:**
- Never reference `Context` or `Activity` in a `ViewModel` or `ScreenModel` — memory leak

### New checklist items (`docs/harness/checklists/new-feature.md`)

- **Adding a new library:** Check if it requires ProGuard/R8 rules — add them before testing release build
- **Android runtime permission:** Follow the three-state flow: request → show rationale if denied once → direct to Settings if permanently denied
- **iOS runtime permission:** Declare `NS*UsageDescription` in `Info.plist` before requesting. Permission is one-shot — after denial, direct user to Settings via `UIApplication.openSettingsURLString`. Prefer `PHPickerViewController` for photo selection (no permission required on iOS 14+)

### New ADRs

- **ADR-011 — Logging policy:** What can go in logs, what cannot (PII, tokens, billing data)
- **ADR-012 — Privacy & data handling:** What stays on device vs. goes to server; relevant given billing feature

### New pattern (`docs/harness/patterns/ios-patterns.md`)

- PHPicker as preferred approach over UIImagePickerController for photo selection
- iOS permission flow: Info.plist requirement, one-shot nature, Settings fallback

---

## Section 6 — README.md AI Harness Section

Add a new `## AI Harness` section to the existing `README.md`. Content:

- One paragraph explaining what the harness is and why it exists
- "New to this project? Start with `docs/harness/SETUP.md`"
- Map of harness components:

```
CLAUDE.md files           — rules + navigation for Claude (always loaded)
docs/harness/
  SETUP.md                — how to configure your AI toolchain (humans read this once)
  WORKFLOW.md             — the AI-driven development loop
  adr/                    — why architectural decisions were made
  checklists/             — step-by-step task guides (Claude reads on demand)
  patterns/               — detailed code patterns and examples (Claude reads on demand)
.claude/settings.json     — hooks: arch guard, ktlint, SwiftLint, no-commit
```

---

## Section 7 — SETUP.md

`docs/harness/SETUP.md` is read once by a human developer joining the project. Content:

- Which plugins to install: superpowers, swiftui-pro, swift-concurrency-pro (with install commands)
- Global `~/.claude/settings.json` configuration required (plugin enablement)
- Project hooks already in `.claude/settings.json` — no action needed
- SwiftLint installation requirement for iOS hooks
- ktlint installation requirement for Android hooks
- How to use skills: `/brainstorming`, `/writing-plans`, `/code-review`
- Pointer to `WORKFLOW.md` for the development loop

---

## Section 8 — Write-Feature-Spec Skill

### Purpose

A lightweight conversational skill that interviews the developer about a future feature and produces a functional spec from `docs/features/_TEMPLATE.md`. Deliberately lighter than `/brainstorming` — no architecture, no implementation plan, no approach proposals. Pure product conversation → spec file.

### Implementation strategy

Try **Option A** (project-local skill) first. If superpowers does not discover project-level skills, fall back to **Option C** (process document).

**Option A — Project-local skill (primary)**
File: `.claude/skills/write-feature-spec.md`
Invoked with `/write-feature-spec`. The developer describes the feature informally; the skill runs the interview and saves the spec.

**Option C — Process document (fallback)**
File: `docs/harness/write-feature-spec.md`
Referenced in root `CLAUDE.md`: "To create a feature spec, read `docs/harness/write-feature-spec.md` and follow the process." No slash command — developer just asks Claude to write a feature spec.

### Skill behaviour (same regardless of option)

1. Developer describes the feature informally — no template required
2. Skill interviews them **one question at a time**: purpose → main user flows → screens → business rules → error states → out of scope → implementation status
3. After each answer, Claude asks the next question or seeks clarification
4. Once all sections are covered, Claude drafts the spec using `docs/features/_TEMPLATE.md`
5. Saves to `docs/features/<feature-name>.md`
6. Asks developer to review before closing

### Skill file structure

```markdown
---
name: write-feature-spec
description: Interview the developer about a feature and produce a functional spec in docs/features/
---

[Interview process + instructions to read docs/features/_TEMPLATE.md + save path logic]
```

---

## Section 9 — Feature Functional Specs

### Location

```
docs/features/
├── _TEMPLATE.md          — template for all future feature specs
├── welcome.md
├── dashboard.md
├── search-friend.md
├── my-friends.md         — covers both list and detail screens
├── my-profile.md
├── post-transaction.md   — covers both step 1 (camera) and step 2 (form)
└── billing.md
```

Separate from `docs/superpowers/specs/` (AI design sessions) and `docs/harness/` (Claude tooling). Feature specs are product documentation — they describe what the app does for users, not how it is built.

### Template (`docs/features/_TEMPLATE.md`)

```markdown
# Feature: [Name]

**Status:** Live | Partial | Stub
**Last updated:** YYYY-MM-DD

## Purpose
What problem this solves for the user. One paragraph.

## User flows
Numbered steps for each main flow. One flow per section.

### Flow 1: [Name]
1. Step one
2. Step two

## Screens
| Screen | What it shows | Entry point |
|---|---|---|
| ... | ... | ... |

## Business rules
Constraints and logic that govern the feature. Bullet list.

## States
| State | When | What the user sees |
|---|---|---|
| Loading | ... | Spinner |
| Empty | ... | Empty state message |
| Error | ... | Snackbar with message |
| Data | ... | Main content |

## Error states
What happens for each error scenario.

## Edge cases
- Empty states
- Long texts
- Slow / no network
- Minimum OS version behaviour

## Out of scope
What this feature deliberately does NOT do. Prevents scope creep during implementation.
```

### Source of truth for feature specs

Two sources combined:
- **GitHub wiki** (`https://github.com/ShodoApps/PokeManiac/wiki`) — product intent, business context, roadmap. Use as primary source for purpose, user flows, and business rules.
- **`shared/presentation/` ScreenModels and UiState files** — current technical state, actual states implemented, known stubs.

### Product context (from wiki)

PokeManiac is a **social network for Pokémon card collectors** — adults 20-40 seeking community, collection management, and a marketplace. Revenue: subscriptions + newsfeed ads + transaction markups. Friend data comes from SuperheroAPI. Transactions are hardcoded in repositories (POC state).

### Existing features — known status

| Feature | Status | Notes |
|---|---|---|
| Welcome | Partial | Sign In → Dashboard works. Sign Up → placeholder. No real auth yet. |
| Dashboard | Complete | News feed of friends' purchases/sales. Pull-to-refresh. Interleaved ads (stub). |
| SearchFriend | Complete | Debounced search (500ms). Subscribe/unsubscribe with optimistic state. |
| MyFriends | Complete | Friend list + detail. Cards with vote counts. Transactions mocked for select users. |
| MyProfile | Partial | Shows own cards from transactions. Name/photo null placeholders. Vote count 0. |
| PostTransaction | Complete | 2-step: camera capture → form (name, number, type, price) → save to feed. |
| Billing | Stub | Subscription paywall screen. Stuck on Loading — not implemented. |

### Roadmap features (out of scope for specs today — captured as "Out of scope" in each spec)

- Full auth: real sign in/sign up, profile setup with photo/username/bio, onboarding tutorial
- NewsFeed likes and comments
- Transaction Detail screen
- Bottom navigation bar: Home / Pokédex / Card Search / Marketplace
- Paid subscription: AI card recognition, unlimited friends, advanced search, no ads
- Tablet layout (explicitly out of scope per ADR-007)

Specs for all 7 features will be written during implementation, combining wiki product intent with current code state.

---

## Section 9 — QA Handoff Documents

### Location

```
docs/qa/
└── YYYY-MM-DD-feature-name.md    — one per feature or significant bug fix
```

### When generated

As part of the workflow: after "review", before "ship". Claude generates the document from the template.

### Template (`docs/harness/checklists/qa-handoff-template.md`)

```markdown
# QA Handoff — [Feature Name] — [Date]

## What changed
1–3 sentence summary of what was implemented or fixed.

## Before / After
| Scenario | Before | After |
|---|---|---|
| [scenario] | [old behaviour] | [new behaviour] |

## What to test
- [ ] [Specific scenario 1]
- [ ] [Specific scenario 2]
- [ ] [Specific scenario 3]

## Regression areas
Screens or flows that could be affected by this change and should be re-tested.

## Don't forget
- [ ] Airplane mode / no network
- [ ] Small screen (SE-size iPhone, compact Android)
- [ ] Long texts and edge case content
- [ ] Error states (server error, timeout, empty response)
- [ ] Dark mode
- [ ] Back navigation and deep back stack
- [ ] Minimum OS version (Android API XX / iOS XX)
- [ ] Accessibility (TalkBack / VoiceOver basics)
- [ ] Slow network (throttled)
- [ ] Portrait orientation only
```

---

## Key Decisions Summary

| Decision | Rationale |
|---|---|
| Lean Core + Reference Library (Approach A) | Lowest maintenance — navigation pointer beats trigger table |
| Nygard ADR format | Industry standard, no learning curve, minimal ceremony |
| YAGNI added to Engineering Philosophy | Was implicit in "pragmatism over perfection" — naming it makes it actionable |
| SwiftLint hook added | Mirrors ktlint — iOS code must be as guarded as Android code |
| Hooks redesigned | Written while learning — contain path bugs, output format bugs, coverage gaps |
| 7 new hooks added | Mobile community best practices: hardcoded strings, commonMain purity, Room migration, memory leaks, missing tests, debug logs, Info.plist |
| ProGuard/permission/PII rules added | Common mobile mistakes Claude would make without explicit guidance |
| Workflow retrospective step added | Ensures new patterns are captured as ADRs before a feature is closed |
| write-feature-spec skill added | Lightweight conversational interview → functional spec; Option A (project-local) with Option C fallback |
| Feature specs added to `docs/features/` | Functional documentation separate from harness tooling and AI design sessions |
| QA handoff template added | Generated by Claude between review and ship — standardises what QA needs to know |
