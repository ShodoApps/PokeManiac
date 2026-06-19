# KMP Harness Template — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create `docs/harness-template/` — a copy-once KMP + Android Compose + iOS SwiftUI harness that a colleague can duplicate at project kickoff.

**Architecture:** All 27 files live under `docs/harness-template/` in the PokeManiac repo. A developer copies the folder to a new project root and does a global search-and-replace on two tokens: `[PROJECT_NAME]` (e.g. `MyApp`) and `[PACKAGE_NAME]` (e.g. `com.acme.myapp`). Pre-filled ADRs carry forward architectural reasoning from PokeManiac, generalized to apply to any KMP Clean Architecture project. `docs/philosophy.md` is the "first thing to read" essay for a developer who has never heard of the harness.

**Tech Stack:** Markdown, JSON. No code, no compilation. Verification = file existence + grep for key strings.

---

## File Map

All paths are relative to `docs/harness-template/` (the directory to be created).

| File | Action | Source |
|---|---|---|
| `README.md` | Write (new) | — |
| `CLAUDE.md` | Write (generalize) | `CLAUDE.md` |
| `feature/CLAUDE.md` | Write (generalize) | `feature/CLAUDE.md` |
| `shared/CLAUDE.md` | Write (generalize) | `shared/CLAUDE.md` |
| `iosApp/CLAUDE.md` | Write (generalize) | `iosApp/CLAUDE.md` |
| `.claude/settings.json` | Write (generalize) | `.claude/settings.json` |
| `.claude/skills/write-feature-spec.md` | Copy verbatim | `.claude/skills/write-feature-spec.md` |
| `docs/philosophy.md` | Write (new) | — |
| `docs/harness/SETUP.md` | Write (generalize) | `docs/harness/SETUP.md` |
| `docs/harness/WORKFLOW.md` | Copy verbatim | `docs/harness/WORKFLOW.md` |
| `docs/harness/adr/README.md` | Write (generalize) | `docs/harness/adr/README.md` |
| `docs/harness/adr/ADR-001-clean-architecture.md` | Write (generalize context) | `docs/harness/adr/ADR-001-*` |
| `docs/harness/adr/ADR-002-kmp-strategy.md` | Write (generalize context) | `docs/harness/adr/ADR-002-*` |
| `docs/harness/adr/ADR-003-screenmodel-viewmodel-split.md` | Copy verbatim | `docs/harness/adr/ADR-003-*` |
| `docs/harness/adr/ADR-004-skie-ios-interop.md` | Copy verbatim | `docs/harness/adr/ADR-004-*` |
| `docs/harness/adr/ADR-005-sealed-uistate.md` | Copy verbatim | `docs/harness/adr/ADR-005-*` |
| `docs/harness/adr/ADR-006-room-kmp-persistence.md` | Copy verbatim | `docs/harness/adr/ADR-006-*` |
| `docs/harness/adr/ADR-007-platform-targets.md` | Write (generalize) | `docs/harness/adr/ADR-007-*` |
| `docs/harness/adr/ADR-008-error-handling.md` | Copy verbatim | `docs/harness/adr/ADR-008-*` |
| `docs/harness/adr/ADR-009-offline-cache-strategy.md` | Copy verbatim | `docs/harness/adr/ADR-009-*` |
| `docs/harness/adr/ADR-010-accessibility-baseline.md` | Copy verbatim | `docs/harness/adr/ADR-010-*` |
| `docs/harness/adr/ADR-011-logging-policy.md` | Copy verbatim | `docs/harness/adr/ADR-011-*` |
| `docs/harness/adr/ADR-012-privacy-data-handling.md` | Copy verbatim | `docs/harness/adr/ADR-012-*` |
| `docs/harness/patterns/naming-conventions.md` | Write (generalize) | `docs/harness/patterns/naming-conventions.md` |
| `docs/harness/patterns/feature-patterns.md` | Copy verbatim | `docs/harness/patterns/feature-patterns.md` |
| `docs/harness/patterns/kmp-patterns.md` | Write (generalize) | `docs/harness/patterns/kmp-patterns.md` |
| `docs/harness/patterns/ios-patterns.md` | Copy verbatim | `docs/harness/patterns/ios-patterns.md` |
| `docs/harness/patterns/di-wiring.md` | Write (generalize) | `docs/harness/patterns/di-wiring.md` |
| `docs/harness/checklists/new-feature.md` | Write (generalize) | `docs/harness/checklists/new-feature.md` |
| `docs/harness/checklists/data-layer.md` | Copy verbatim | `docs/harness/checklists/data-layer.md` |
| `docs/harness/checklists/unit-tests.md` | Copy verbatim | `docs/harness/checklists/unit-tests.md` |
| `docs/harness/checklists/code-review.md` | Copy verbatim | `docs/harness/checklists/code-review.md` |
| `docs/harness/checklists/qa-handoff-template.md` | Copy verbatim | `docs/harness/checklists/qa-handoff-template.md` |
| `docs/features/_TEMPLATE.md` | Copy verbatim | `docs/features/_TEMPLATE.md` |
| `docs/features/.gitkeep` | Create empty | — |
| `docs/qa/.gitkeep` | Create empty | — |

---

## Task 1: Create directory structure

**Files:** all subdirectories under `docs/harness-template/`

- [ ] **Step 1: Create all directories**

```bash
mkdir -p docs/harness-template/feature \
         docs/harness-template/shared \
         docs/harness-template/iosApp \
         docs/harness-template/.claude/skills \
         docs/harness-template/docs/harness/adr \
         docs/harness-template/docs/harness/patterns \
         docs/harness-template/docs/harness/checklists \
         docs/harness-template/docs/features \
         docs/harness-template/docs/qa
```

- [ ] **Step 2: Verify**

```bash
find docs/harness-template -type d | sort
```
Expected: 11 directories listed.

---

## Task 2: Write `docs/harness-template/README.md`

**Files:**
- Create: `docs/harness-template/README.md`

- [ ] **Step 1: Write the file**

```markdown
# KMP Harness Template

A copy-once harness for KMP + Android Compose + iOS SwiftUI projects following Clean Architecture.

## What This Is

This folder is the complete starting harness for a new project. Copy it once, adapt the placeholders, and you have a fully-wired Claude Code setup from day one: architecture rules, automated guards, ADRs, checklists, patterns, a development workflow, and a skill for writing feature specs.

It is a **copy-once** template — no git subtree, no sync mechanism. Your project owns its copy. Adapt it freely.

## How to Use This

1. **Copy the folder** to your new project root:
   ```bash
   cp -r docs/harness-template/. ~/Projects/[PROJECT_NAME]/
   ```

2. **Replace the two placeholder tokens** globally:

   | Token | Replace with | Example |
   |---|---|---|
   | `[PROJECT_NAME]` | Your app name (PascalCase) | `PokeManiac`, `ShodoNotes` |
   | `[PACKAGE_NAME]` | Your Android package (dot-separated) | `com.shodo.pokemaniac` |

   ```bash
   # From your new project root:
   grep -rl '\[PROJECT_NAME\]' . | xargs sed -i '' 's/\[PROJECT_NAME\]/YourAppName/g'
   grep -rl '\[PACKAGE_NAME\]' . | xargs sed -i '' 's/\[PACKAGE_NAME\]/com.your.package/g'
   ```

3. **Update the KMP migration status** in `CLAUDE.md` as you complete each phase.

4. **Read `docs/harness/SETUP.md`** — it tells you which Claude Code plugins to install.

5. **Read `docs/philosophy.md`** if you want to understand why the harness is structured this way.

## What's Included

```
CLAUDE.md                     — always-loaded rules for Claude (root)
feature/CLAUDE.md             — feature module rules
shared/CLAUDE.md              — KMP shared module rules
iosApp/CLAUDE.md              — iOS app rules
.claude/settings.json         — automated hooks (arch guard, ktlint, SwiftLint, no-commit)
.claude/skills/
  write-feature-spec.md       — project-local skill: interview → functional spec
docs/
  philosophy.md               — the "capable junior with guardrails" essay (read once)
  harness/
    SETUP.md                  — plugin installation + tool reference
    WORKFLOW.md               — development loop
    adr/                      — 12 pre-filled Architecture Decision Records
    patterns/                 — detailed patterns: Compose, KMP, iOS, DI, naming
    checklists/               — step-by-step task guides
  features/
    _TEMPLATE.md              — feature spec template
  qa/                         — QA handoff docs (generated per feature)
```

## What's Not Included

- Feature specs (`docs/features/*.md`) — write these for your own features with `/write-feature-spec`
- QA handoff docs (`docs/qa/`) — generated by Claude as you ship features
- Filled ADRs for project-specific decisions (auth, build variants, analytics) — write when the feature is built
```

- [ ] **Step 2: Verify**

```bash
wc -l docs/harness-template/README.md
```
Expected: 65–75 lines.

---

## Task 3: Write `docs/harness-template/docs/philosophy.md`

**Files:**
- Create: `docs/harness-template/docs/philosophy.md`

- [ ] **Step 1: Write the file**

```markdown
# AI-Augmented Mobile Development: The Capable Junior with Guardrails

> This essay explains why the harness exists and how it works. Read it once.

---

## The Problem with Undirected AI Coding

Large language models are technically capable. They can write idiomatic Kotlin, structure a Clean Architecture module, collect a Kotlin Flow in SwiftUI, wire a Koin module — all without prompting. The problem is context.

Without context, Claude generates code that is technically correct but architecturally inconsistent. It invents patterns that diverge from your codebase. It makes decisions that contradict choices you made two months ago and documented nowhere. It answers each session as if it is the first, because for it, it is.

The result: you spend as much time reviewing and correcting as you would writing the code yourself.

---

## The "Capable Junior" Model

Think of Claude as a capable junior developer who joins your team knowing the technology stack — Kotlin, Compose, KMP, SwiftUI — but knowing nothing about your specific project.

A capable junior is not supervised step-by-step. They are given:
- The rules they must always follow (architecture constraints, naming conventions)
- Reference material to read when starting a new task (patterns, checklists)
- A documented history of decisions so they don't relitigate the past (ADRs)
- Guardrails that catch mistakes automatically before they reach review (hooks)

That is the harness.

---

## The Five Layers

**Layer 1 — CLAUDE.md files (always-loaded rules)**
Four scoped files Claude loads at the start of every session. They stay small — only rules Claude must never operate without. Architecture constraints, the dependency direction golden rule, the non-obvious Kotlin/Compose pitfalls.

**Layer 2 — `docs/harness/` (reference library)**
Detailed patterns, checklists, and ADRs Claude reads on demand when the task calls for it. A new feature? Read the new-feature checklist. Modifying the data layer? Read the data-layer checklist and the relevant ADRs. Claude is directed to the right document by navigation pointers in the CLAUDE.md files.

**Layer 3 — `docs/features/` (functional specs)**
One markdown file per feature. Describes what each feature does for users: flows, screens, business rules, edge cases. Claude reads these to understand product intent before writing code. Written with `/write-feature-spec` — a conversational interview that works solo, in grooming sessions, or during design workshops.

**Layer 4 — `docs/qa/` (QA handoff documents)**
Generated by Claude before each ship. Tells QA what changed, what to test, what the before/after is, and a don't-forget checklist.

**Layer 5 — `.claude/settings.json` (hooks)**
Shell commands that run automatically on every file edit. They catch architecture violations, Android imports leaking into KMP shared code, debug logging left in production files, hardcoded UI strings, missing database migrations, and more — without any manual review step.

---

## Why Copy-Once?

A git subtree or submodule approach means the template evolves and your project must keep up. That creates a maintenance dependency between projects that share nothing except their starting point.

Copy-once is different: you take ownership of the harness the moment you copy it. Your project's constraints diverge from the next project's. You add ADRs specific to your authentication strategy, your analytics tooling, your API contract. The template is the starting line, not an ongoing dependency.

The cost is that improvements to the template don't automatically reach existing projects. The benefit is that your project is never blocked by a template change that doesn't apply to you.

---

## What It Enables

With the harness in place:

- **Consistent architecture across features.** Claude doesn't reinvent patterns — it reads your patterns and follows them.
- **Decisions documented and respected.** ADRs tell Claude why the architecture is the way it is. It stops questioning settled decisions.
- **Automated catches before they cost time.** Architecture violations, style violations, and missing migrations are caught on every file edit, not in code review.
- **Onboarding that scales.** A new team member reads `SETUP.md` and the ADRs. They understand the codebase in hours, not days.
- **Feature specs as living documentation.** The `/write-feature-spec` interview produces a document that QA, product, and future engineers can read without opening Xcode or Android Studio.

The harness does not make Claude infallible. It makes Claude reliable enough to be a genuine force multiplier on a professional mobile project.
```

- [ ] **Step 2: Verify**

```bash
wc -l docs/harness-template/docs/philosophy.md
```
Expected: 80–100 lines.

---

## Task 4: Write the four generalized CLAUDE.md files

**Files:**
- Create: `docs/harness-template/CLAUDE.md`
- Create: `docs/harness-template/feature/CLAUDE.md`
- Create: `docs/harness-template/shared/CLAUDE.md`
- Create: `docs/harness-template/iosApp/CLAUDE.md`

Source: current PokeManiac CLAUDE.md files. Generalizations applied:
- `PokeManiac` → `[PROJECT_NAME]`
- KMP migration status: all phases set to ⏳ (new project starts at zero)
- Remove `Authoritative plan: docs/kmp-migration-plan.md` line (project-specific)

- [ ] **Step 1: Write `docs/harness-template/CLAUDE.md`**

```markdown
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
```

- [ ] **Step 2: Write `docs/harness-template/feature/CLAUDE.md`**

```markdown
# [PROJECT_NAME] — Feature Module Guide

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
```

- [ ] **Step 3: Write `docs/harness-template/shared/CLAUDE.md`**

```markdown
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
```

- [ ] **Step 4: Write `docs/harness-template/iosApp/CLAUDE.md`**

```markdown
# [PROJECT_NAME] — iOS App Guide

> See root `CLAUDE.md` for project-wide architecture.
> See `shared/CLAUDE.md` for ScreenModel/UiState patterns.
> See `docs/harness/patterns/ios-patterns.md` for full KotlinViewAdapter + SKIE code examples + permission flow.

---

## Key Rules

- Never manage `CoroutineScope` manually — `KotlinViewAdapter` creates and cancels it automatically on `deinit`
- `.task` handles view lifecycle — no manual `onDisappear` cancellation needed

---

## Reference Screens

Add your own reference screens here as you build them:

| Screen | Files | Pattern used |
|---|---|---|
| (add your first screen here) | | KotlinViewAdapter + StateFlow |

Full patterns: `docs/harness/patterns/ios-patterns.md`
```

- [ ] **Step 5: Verify all four files exist**

```bash
ls docs/harness-template/CLAUDE.md \
   docs/harness-template/feature/CLAUDE.md \
   docs/harness-template/shared/CLAUDE.md \
   docs/harness-template/iosApp/CLAUDE.md
```
Expected: all four paths listed without error.

---

## Task 5: Write generalized `.claude/settings.json`

**Files:**
- Create: `docs/harness-template/.claude/settings.json`

Source: `.claude/settings.json`. Generalizations:
- Architecture guard: replace `com\\.shodo\\.android\\.(data|api|database)\\.` with `[PACKAGE_NAME]\\.(data|api|database)\\.`
- Room migration reminder: replace `PokeManiacDatabase.kt` and `DatabaseModule.kt` with `[PROJECT_NAME]Database.kt` and `DatabaseModule.kt`

- [ ] **Step 1: Write the file**

```json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Bash",
        "hooks": [
          {
            "type": "command",
            "command": "CMD=$(jq -r '.tool_input.command // empty'); if echo \"$CMD\" | grep -qE '^\\s*git\\s+commit'; then echo '{\"hookSpecificOutput\":{\"hookEventName\":\"PreToolUse\",\"permissionDecision\":\"deny\",\"permissionDecisionReason\":\"Manual commits only — you always commit yourself in this project.\"}}'; fi",
            "statusMessage": "Checking manual-commit policy..."
          }
        ]
      }
    ],
    "PostToolUse": [
      {
        "matcher": "Edit",
        "hooks": [
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); [[ \"$FILE\" == *.kt ]] || exit 0; KTLINT=$(which ktlint 2>/dev/null); [[ -n \"$KTLINT\" ]] || exit 0; \"$KTLINT\" --format \"$FILE\" 2>/dev/null; VIOLATIONS=$(\"$KTLINT\" \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ ktlint violations remaining after auto-format:\\n\\($v)\"}'",
            "statusMessage": "Running ktlint..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); [[ \"$FILE\" == *.swift ]] || exit 0; SWIFTLINT=$(which swiftlint 2>/dev/null); [[ -n \"$SWIFTLINT\" ]] || exit 0; \"$SWIFTLINT\" --fix --quiet \"$FILE\" 2>/dev/null; VIOLATIONS=$(\"$SWIFTLINT\" lint --quiet \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ SwiftLint violations remaining after auto-fix:\\n\\($v)\"}'",
            "statusMessage": "Running SwiftLint..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); ([[ \"$FILE\" == *\"/feature/\"* ]] || [[ \"$FILE\" == *\"/coreui/\"* ]]) && [[ \"$FILE\" == *.kt ]] || exit 0; VIOLATIONS=$(grep -nE 'import [PACKAGE_NAME]\\.(data|api|database)\\.' \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg f \"$FILE\" --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ ARCHITECTURE VIOLATION in \\($f):\\n\\($v)\\n\\nfeature:* and coreui must NOT import from :shared:data, :shared:api, or :shared:database.\"}'",
            "statusMessage": "Checking architecture boundaries..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); [[ \"$FILE\" == */commonMain/*.kt ]] || exit 0; VIOLATIONS=$(grep -nE 'import (android|androidx)\\.' \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg f \"$FILE\" --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ commonMain PURITY VIOLATION in \\($f):\\n\\($v)\\n\\ncommonMain must not import Android-only APIs. Use expect/actual or move to androidMain.\"}'",
            "statusMessage": "Checking commonMain purity..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); [[ \"$FILE\" == *ViewModel.kt ]] || exit 0; VIOLATIONS=$(grep -nE '\\b(Context|Activity|Fragment)\\b' \"$FILE\" 2>/dev/null | grep -v '//'); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg f \"$FILE\" --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ MEMORY LEAK RISK in \\($f):\\n\\($v)\\n\\nViewModels must not hold Context or Activity references.\"}'",
            "statusMessage": "Checking ViewModel for memory leaks..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); [[ \"$FILE\" == *.kt ]] && [[ \"$FILE\" != */test/* ]] && [[ \"$FILE\" != */androidTest/* ]] || exit 0; VIOLATIONS=$(grep -nE '\\bLog\\.(d|e|i|w|v|wtf)\\b|\\bprintln\\b' \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ DEBUG LOGGING detected — remove before shipping:\\n\\($v)\"}'",
            "statusMessage": "Checking for debug logs..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); ([[ \"$FILE\" == *\"/feature/\"* ]] || [[ \"$FILE\" == *\"/coreui/\"* ]]) && [[ \"$FILE\" == *.kt ]] || exit 0; VIOLATIONS=$(grep -nE '\\bText\\(\"[A-Za-z]' \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ POSSIBLE HARDCODED UI STRING:\\n\\($v)\\n\\nUse stringResource(R.string.key) instead of string literals in composables.\"}'",
            "statusMessage": "Checking for hardcoded strings..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); ([[ \"$FILE\" == *Base.kt ]] || [[ \"$FILE\" == *Dao.kt ]]) || exit 0; jq -n '{\"systemMessage\": \"⚠️ Room entity or DAO modified. Check:\\n1. Did you bump @Database(version = N) in [PROJECT_NAME]Database.kt?\\n2. Did you add a migration in DatabaseModule.kt?\\n(New tables are handled automatically — only schema changes to existing tables need migrations.)\"}'",
            "statusMessage": "Checking Room migration..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); [[ \"$FILE\" == *build.gradle.kts ]] || exit 0; jq -n '{\"systemMessage\": \"build.gradle.kts modified. Remember to:\\n• Run ./gradlew testDebugUnitTest\\n• Run ./gradlew lint\\n• Run ./gradlew assembleRelease\\n• Check if new libraries need ProGuard/R8 rules in proguard-rules.pro\"}'",
            "statusMessage": "Noting Gradle change..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); [[ \"$FILE\" == *.swift ]] || exit 0; VIOLATIONS=$(grep -nE '(PHPhotoLibrary|AVCaptureDevice|CLLocationManager|CNContactStore|CMMotionManager)' \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n '{\"systemMessage\": \"⚠️ iOS permission API detected. Verify Info.plist has the corresponding NS*UsageDescription key — missing key causes a crash on the first permission request on device.\"}'",
            "statusMessage": "Checking iOS permission declaration..."
          }
        ]
      },
      {
        "matcher": "Write",
        "hooks": [
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); [[ \"$FILE\" == *.kt ]] || exit 0; KTLINT=$(which ktlint 2>/dev/null); [[ -n \"$KTLINT\" ]] || exit 0; \"$KTLINT\" --format \"$FILE\" 2>/dev/null; VIOLATIONS=$(\"$KTLINT\" \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ ktlint violations remaining after auto-format:\\n\\($v)\"}'",
            "statusMessage": "Running ktlint..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); [[ \"$FILE\" == *.swift ]] || exit 0; SWIFTLINT=$(which swiftlint 2>/dev/null); [[ -n \"$SWIFTLINT\" ]] || exit 0; \"$SWIFTLINT\" --fix --quiet \"$FILE\" 2>/dev/null; VIOLATIONS=$(\"$SWIFTLINT\" lint --quiet \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ SwiftLint violations remaining after auto-fix:\\n\\($v)\"}'",
            "statusMessage": "Running SwiftLint..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); ([[ \"$FILE\" == *\"/feature/\"* ]] || [[ \"$FILE\" == *\"/coreui/\"* ]]) && [[ \"$FILE\" == *.kt ]] || exit 0; VIOLATIONS=$(grep -nE 'import [PACKAGE_NAME]\\.(data|api|database)\\.' \"$FILE\" 2>/dev/null); [[ -z \"$VIOLATIONS\" ]] && exit 0; jq -n --arg f \"$FILE\" --arg v \"$VIOLATIONS\" '{\"systemMessage\": \"⚠️ ARCHITECTURE VIOLATION in \\($f):\\n\\($v)\\n\\nfeature:* and coreui must NOT import from :shared:data, :shared:api, or :shared:database.\"}'",
            "statusMessage": "Checking architecture boundaries..."
          },
          {
            "type": "command",
            "command": "FILE=$(jq -r '.tool_input.file_path // empty'); ([[ \"$FILE\" == *ViewModel.kt ]] || [[ \"$FILE\" == *ScreenModel.kt ]]) || exit 0; BASENAME=$(basename \"$FILE\" .kt); FOUND=$(find \"$(dirname \"$FILE\")\" -name \"${BASENAME}Test.kt\" 2>/dev/null | head -1); [[ -n \"$FOUND\" ]] && exit 0; jq -n --arg f \"$BASENAME\" '{\"systemMessage\": \"⚠️ New \\($f).kt created with no matching test file. Create \\($f)Test.kt in src/test/ — see docs/harness/checklists/unit-tests.md\"}'",
            "statusMessage": "Checking for missing test file..."
          }
        ]
      }
    ]
  }
}
```

**Important:** After doing the global `[PACKAGE_NAME]` replacement in a new project, verify this hook:
```bash
grep "PACKAGE_NAME" .claude/settings.json
```
Expected: no results (all replaced).

- [ ] **Step 2: Validate JSON**

```bash
jq . docs/harness-template/.claude/settings.json > /dev/null && echo "✅ JSON valid" || echo "❌ INVALID"
```
Expected: `✅ JSON valid`

---

## Task 6: Copy verbatim files

**Files to copy (no changes needed — fully generic already):**
- `.claude/skills/write-feature-spec.md` → `docs/harness-template/.claude/skills/write-feature-spec.md`
- `docs/harness/WORKFLOW.md` → `docs/harness-template/docs/harness/WORKFLOW.md`
- `docs/harness/patterns/feature-patterns.md` → `docs/harness-template/docs/harness/patterns/feature-patterns.md`
- `docs/harness/patterns/ios-patterns.md` → `docs/harness-template/docs/harness/patterns/ios-patterns.md`
- `docs/harness/checklists/code-review.md` → `docs/harness-template/docs/harness/checklists/code-review.md`
- `docs/harness/checklists/qa-handoff-template.md` → `docs/harness-template/docs/harness/checklists/qa-handoff-template.md`
- `docs/harness/checklists/data-layer.md` → `docs/harness-template/docs/harness/checklists/data-layer.md`
- `docs/harness/checklists/unit-tests.md` → `docs/harness-template/docs/harness/checklists/unit-tests.md`
- `docs/features/_TEMPLATE.md` → `docs/harness-template/docs/features/_TEMPLATE.md`
- `docs/harness/adr/ADR-003-screenmodel-viewmodel-split.md` → `docs/harness-template/docs/harness/adr/ADR-003-screenmodel-viewmodel-split.md`
- `docs/harness/adr/ADR-004-skie-ios-interop.md` → `docs/harness-template/docs/harness/adr/ADR-004-skie-ios-interop.md`
- `docs/harness/adr/ADR-005-sealed-uistate.md` → `docs/harness-template/docs/harness/adr/ADR-005-sealed-uistate.md`
- `docs/harness/adr/ADR-006-room-kmp-persistence.md` → `docs/harness-template/docs/harness/adr/ADR-006-room-kmp-persistence.md`
- `docs/harness/adr/ADR-008-error-handling.md` → `docs/harness-template/docs/harness/adr/ADR-008-error-handling.md`
- `docs/harness/adr/ADR-009-offline-cache-strategy.md` → `docs/harness-template/docs/harness/adr/ADR-009-offline-cache-strategy.md`
- `docs/harness/adr/ADR-010-accessibility-baseline.md` → `docs/harness-template/docs/harness/adr/ADR-010-accessibility-baseline.md`
- `docs/harness/adr/ADR-011-logging-policy.md` → `docs/harness-template/docs/harness/adr/ADR-011-logging-policy.md`
- `docs/harness/adr/ADR-012-privacy-data-handling.md` → `docs/harness-template/docs/harness/adr/ADR-012-privacy-data-handling.md`

- [ ] **Step 1: Copy all verbatim files**

```bash
cp .claude/skills/write-feature-spec.md docs/harness-template/.claude/skills/write-feature-spec.md
cp docs/harness/WORKFLOW.md docs/harness-template/docs/harness/WORKFLOW.md
cp docs/harness/patterns/feature-patterns.md docs/harness-template/docs/harness/patterns/feature-patterns.md
cp docs/harness/patterns/ios-patterns.md docs/harness-template/docs/harness/patterns/ios-patterns.md
cp docs/harness/checklists/code-review.md docs/harness-template/docs/harness/checklists/code-review.md
cp docs/harness/checklists/qa-handoff-template.md docs/harness-template/docs/harness/checklists/qa-handoff-template.md
cp docs/harness/checklists/data-layer.md docs/harness-template/docs/harness/checklists/data-layer.md
cp docs/harness/checklists/unit-tests.md docs/harness-template/docs/harness/checklists/unit-tests.md
cp docs/features/_TEMPLATE.md docs/harness-template/docs/features/_TEMPLATE.md
cp docs/harness/adr/ADR-003-screenmodel-viewmodel-split.md docs/harness-template/docs/harness/adr/ADR-003-screenmodel-viewmodel-split.md
cp docs/harness/adr/ADR-004-skie-ios-interop.md docs/harness-template/docs/harness/adr/ADR-004-skie-ios-interop.md
cp docs/harness/adr/ADR-005-sealed-uistate.md docs/harness-template/docs/harness/adr/ADR-005-sealed-uistate.md
cp docs/harness/adr/ADR-006-room-kmp-persistence.md docs/harness-template/docs/harness/adr/ADR-006-room-kmp-persistence.md
cp docs/harness/adr/ADR-008-error-handling.md docs/harness-template/docs/harness/adr/ADR-008-error-handling.md
cp docs/harness/adr/ADR-009-offline-cache-strategy.md docs/harness-template/docs/harness/adr/ADR-009-offline-cache-strategy.md
cp docs/harness/adr/ADR-010-accessibility-baseline.md docs/harness-template/docs/harness/adr/ADR-010-accessibility-baseline.md
cp docs/harness/adr/ADR-011-logging-policy.md docs/harness-template/docs/harness/adr/ADR-011-logging-policy.md
cp docs/harness/adr/ADR-012-privacy-data-handling.md docs/harness-template/docs/harness/adr/ADR-012-privacy-data-handling.md
```

- [ ] **Step 2: Verify count**

```bash
find docs/harness-template -name "*.md" | wc -l
```
Expected: at least 20 files at this point (counting Task 2–5 files + verbatim copies).

---

## Task 7: Write generalized `docs/harness/SETUP.md`

**Files:**
- Create: `docs/harness-template/docs/harness/SETUP.md`

Source: `docs/harness/SETUP.md`. Generalizations:
- `PokeManiac` → `[PROJECT_NAME]`
- Remove PokeManiac wiki URL from "Where to Find Things" table (last row)

- [ ] **Step 1: Read the source**

Read `docs/harness/SETUP.md` fully.

- [ ] **Step 2: Write the generalized file**

Copy the full content of `docs/harness/SETUP.md` with these changes:
1. Replace `PokeManiac AI harness` → `[PROJECT_NAME] AI harness` in the opening sentence
2. In section `## 7. Where to Find Things`, remove the last row: `| Product concept + roadmap | https://github.com/... | ...` — this is project-specific; the new project will add its own wiki link here.

```markdown
# AI Harness — Developer Setup

Welcome to the [PROJECT_NAME] AI harness. This guide covers everything you need to work with Claude Code on this project. Read it once when you join. After reading, the only other file you need for day-to-day work is `docs/harness/WORKFLOW.md`.

---

## What Is the Harness?

[...copy the rest of the file verbatim except for the two changes above...]
```

The exact content to remove at the end of section 7:

Old last row:
```
| Product concept + roadmap | https://github.com/ShodoApps/PokeManiac/wiki | Business plan, feature list, remaining features |
```

Replace with:
```
| Product wiki / roadmap | (add your own link here) | Business plan, feature list, remaining features |
```

- [ ] **Step 3: Verify**

```bash
grep -c "PokeManiac" docs/harness-template/docs/harness/SETUP.md
```
Expected: `0` — no PokeManiac references remain.

---

## Task 8: Write generalized patterns files

### 8a — `naming-conventions.md`

**Files:**
- Create: `docs/harness-template/docs/harness/patterns/naming-conventions.md`

Source: `docs/harness/patterns/naming-conventions.md`. Generalizations:
- Replace the specific feature list in the module tree (feature:welcome, feature:dashboard, etc.) with a generic placeholder list.

- [ ] **Step 1: Write the file**

Read `docs/harness/patterns/naming-conventions.md`, then write it with the module tree section changed to:

```markdown
Presentation Layer
├── app                    Main entry point; wires all feature Koin modules
├── feature:[feature1]     (replace with your actual feature names)
├── feature:[feature2]
├── feature:[feature3]
└── coreui                 Shared Compose components, theme, navigator interfaces

Shared KMP Modules (commonMain unless noted)
├── shared:domain          Repository interfaces + entities; commonMain + Apple targets
├── shared:presentation    ScreenModels, UiState, UiModels; commonMain
├── shared:data            Repository impls + DataStore interfaces; commonMain + androidTarget
├── shared:api             Ktor clients, DTOs, *RequestImpl; commonMain + androidTarget
├── shared:database        Room, DAOs, DataStore impls; commonMain + androidTarget
├── shared:tracking        TrackingRepository impl + Koin trackingModule
└── shared:di              Koin apiModule + dataModule in commonMain

Android Bootstrap
└── app/di/DatabaseModule  databaseModule (Room builder, DataStoreImpls) + startKoin
```

Keep the naming conventions table verbatim — the patterns (`*ViewModel`, `*ScreenModel`, etc.) are fully generic.

- [ ] **Step 2: Verify**

```bash
grep -c "welcome\|dashboard\|searchfriend\|myfriends\|posttransaction\|billing" docs/harness-template/docs/harness/patterns/naming-conventions.md
```
Expected: `0`

---

### 8b — `kmp-patterns.md`

**Files:**
- Create: `docs/harness-template/docs/harness/patterns/kmp-patterns.md`

Source: `docs/harness/patterns/kmp-patterns.md`. Generalizations:
- Replace `PokeManiacDatabase` → `[PROJECT_NAME]Database`
- Replace `PokeManiacApplication` → `[PROJECT_NAME]Application` (if present)

- [ ] **Step 1: Read the source**

Read `docs/harness/patterns/kmp-patterns.md`.

- [ ] **Step 2: Copy with replacements**

Copy verbatim with these two sed replacements:

```bash
sed 's/PokeManiacDatabase/[PROJECT_NAME]Database/g; s/PokeManiacApplication/[PROJECT_NAME]Application/g' \
  docs/harness/patterns/kmp-patterns.md \
  > docs/harness-template/docs/harness/patterns/kmp-patterns.md
```

- [ ] **Step 3: Verify**

```bash
grep -c "PokeManiac" docs/harness-template/docs/harness/patterns/kmp-patterns.md
```
Expected: `0`

---

### 8c — `di-wiring.md`

**Files:**
- Create: `docs/harness-template/docs/harness/patterns/di-wiring.md`

Source: `docs/harness/patterns/di-wiring.md`. Generalizations:
- Replace `PokeManiacApplication` → `[PROJECT_NAME]Application`
- Replace `PokeManiacDatabase` → `[PROJECT_NAME]Database`
- Replace `com.shodo.android` → `[PACKAGE_NAME]` (if present in any import examples)
- Replace `SearchFriendScreenModelFactory` / `SearchFriendModule` reference text → `MyFeatureScreenModelFactory` / `myFeatureModule`

- [ ] **Step 1: Read the source**

Read `docs/harness/patterns/di-wiring.md`.

- [ ] **Step 2: Copy with replacements**

```bash
sed 's/PokeManiacApplication/[PROJECT_NAME]Application/g
     s/PokeManiacDatabase/[PROJECT_NAME]Database/g
     s/com\.shodo\.android/[PACKAGE_NAME]/g
     s/SearchFriendScreenModelFactory/MyFeatureScreenModelFactory/g
     s/SearchFriendModule/myFeatureModule/g
     s/searchFriendModule/myFeatureModule/g' \
  docs/harness/patterns/di-wiring.md \
  > docs/harness-template/docs/harness/patterns/di-wiring.md
```

- [ ] **Step 3: Verify**

```bash
grep -c "PokeManiac\|shodo\|SearchFriend\|searchFriend" docs/harness-template/docs/harness/patterns/di-wiring.md
```
Expected: `0`

---

## Task 9: Write generalized `checklists/new-feature.md`

**Files:**
- Create: `docs/harness-template/docs/harness/checklists/new-feature.md`

Source: `docs/harness/checklists/new-feature.md`. Generalizations:
- Replace `PokeManiacTheme` → `[PROJECT_NAME]Theme`
- Replace `PokeManiacApplication` → `[PROJECT_NAME]Application`

- [ ] **Step 1: Read the source**

Read `docs/harness/checklists/new-feature.md`.

- [ ] **Step 2: Copy with replacements**

```bash
sed 's/PokeManiacTheme/[PROJECT_NAME]Theme/g
     s/PokeManiacApplication/[PROJECT_NAME]Application/g' \
  docs/harness/checklists/new-feature.md \
  > docs/harness-template/docs/harness/checklists/new-feature.md
```

- [ ] **Step 3: Verify**

```bash
grep -c "PokeManiac" docs/harness-template/docs/harness/checklists/new-feature.md
```
Expected: `0`

---

## Task 10: Write generalized ADR files

### 10a — ADR README

**Files:**
- Create: `docs/harness-template/docs/harness/adr/README.md`

Source: `docs/harness/adr/README.md`. No project-specific content in this file — copy verbatim.

- [ ] **Step 1: Copy**

```bash
cp docs/harness/adr/README.md docs/harness-template/docs/harness/adr/README.md
```

---

### 10b — ADR-001 (Clean Architecture)

**Files:**
- Create: `docs/harness-template/docs/harness/adr/ADR-001-clean-architecture.md`

Source: `docs/harness/adr/ADR-001-clean-architecture.md`. Generalization: the Context section opens with "PokeManiac is a social app for Pokémon card collectors" — replace with a generic project description. The Decision and Consequences sections are fully generic.

- [ ] **Step 1: Write the file**

```markdown
# ADR-001: Clean Architecture + SOLID (domain has zero dependencies)

**Status:** Accepted
**Date:** [update with your project start date]

## Context

[PROJECT_NAME] targets multiple clients (Android, iOS) with multiple data sources (remote API, local database). Business logic must be testable independently of any framework or platform. Early projects mixing network, database, and UI code in the same classes made testing slow and brittle.

We needed an organisation that:
- Allows business logic to be tested without an Android device or emulator
- Isolates the app from changes in external libraries (e.g. switching HTTP clients)
- Gives new contributors a predictable place to find and add code

## Decision

We chose Clean Architecture with three layers, enforced as Gradle modules:

**Domain layer** (`shared:domain`) — repository interfaces and entities only. Zero dependencies on any framework, library, or other module. It defines what the app does, not how.

**Data layer** (`shared:data`, `shared:api`, `shared:database`) — implements the domain interfaces. All technology choices (Ktor, Room, Moshi) are confined here. DTOs and Room entities never cross the repository interface boundary; the implementation maps them to domain types before returning.

**Presentation layer** (`feature:*`, `app`, `coreui`) — depends on domain only, never on data. Receives domain types from repositories, maps them to UI models inside ViewModels/ScreenModels.

Dependency direction: Presentation → Domain ← Data. Domain knows nothing about either side.

## Consequences

**Easier:**
- Domain and ScreenModel logic is testable with plain JVM JUnit tests — no Android setup, no mocking of Room or Ktor
- Adding a new data source (e.g. GraphQL) means writing a new `*RequestImpl`, not touching the domain or UI
- Contributors know exactly where each type of code lives

**Harder:**
- Every data type crosses at least one boundary; mapping code is required at each boundary (DTO → domain → UiModel)
- More files than a single-layer approach — each feature has entities, repository interface, repository impl, DAO, DTO, ScreenModel, ViewModel, View
- Repository methods must return domain types, so DTOs cannot be returned even if convenient

**Common mistake to avoid:** Adding an import from `:shared:data` or `:shared:api` inside a feature module. The architecture guard hook catches this and blocks the edit.
```

---

### 10c — ADR-002 (KMP Strategy)

**Files:**
- Create: `docs/harness-template/docs/harness/adr/ADR-002-kmp-strategy.md`

Source: `docs/harness/adr/ADR-002-kmp-strategy.md`. Generalization: Context section references PokeManiac's "initially Android-only" history. Replace with generic framing. Phase statuses → all ⏳.

- [ ] **Step 1: Write the file**

```markdown
# ADR-002: Kotlin Multiplatform (KMP) Strategy + Phase Plan

**Status:** Accepted
**Date:** [update with your project start date]

## Context

[PROJECT_NAME] targets both Android and iOS. The app uses a clean domain/data/presentation separation (see ADR-001), which makes KMP migration possible without rewriting from scratch. Adding iOS as a native SwiftUI app required a choice:

1. Rewrite all business logic in Swift (duplication, two bugs for every one bug fixed)
2. Wrap the Android app in a WebView (no native feel)
3. Share Kotlin code via KMP

## Decision

We chose Kotlin Multiplatform, migrating module by module in a defined phase plan:

| Phase | What moved to KMP | Status |
|---|---|---|
| A–C | `:shared:domain` — interfaces + entities | ⏳ |
| D | `:shared:api`, `:shared:data`, `:shared:database`, `:shared:tracking` | ⏳ |
| E | `:shared:di` — Koin modules in commonMain | ⏳ |
| F | `:shared:presentation` — ScreenModels + UiState | ⏳ |
| G | iOS SwiftUI app consuming shared ScreenModels via SKIE | ⏳ |

Each phase is designed to be independently shippable — Android continues working throughout.

Update the statuses in `CLAUDE.md` as phases complete.

## Consequences

**Easier:**
- Business logic bugs are fixed once and fixed on both platforms
- iOS gets the same data layer as Android — no separate Swift networking or persistence
- Domain and ScreenModel tests run on JVM (fast, no emulator) for both platforms

**Harder:**
- Build configuration is more complex: `commonMain`, `androidMain`, `iosMain` source sets
- Some Android-only APIs (e.g. `Context`) cannot be used in `commonMain` — use `expect/actual` or move to `androidMain`
- KMP library support is narrower than pure Android; check multiplatform compatibility before adding any new dependency
- Requires SKIE for clean Swift interop with Kotlin Flows (see ADR-004)

**Common mistake to avoid:** Adding `android.*` or `androidx.*` imports in `commonMain` files. The commonMain purity hook catches this. Use `expect/actual` to bridge platform-specific capabilities instead.
```

---

### 10d — ADR-007 (Platform Targets)

**Files:**
- Create: `docs/harness-template/docs/harness/adr/ADR-007-platform-targets.md`

Source: `docs/harness/adr/ADR-007-platform-targets.md`. Generalization: replace any specific API level or iOS version numbers with `[MIN_ANDROID_API]` and `[MIN_IOS_VERSION]` placeholders.

- [ ] **Step 1: Read the source**

Read `docs/harness/adr/ADR-007-platform-targets.md`.

- [ ] **Step 2: Copy with replacements**

Copy the file content, replacing specific version numbers (e.g. `API 24`, `iOS 16`) with `[MIN_ANDROID_API]` and `[MIN_IOS_VERSION]`. Keep everything else verbatim.

If the file contains `PokeManiac`, replace with `[PROJECT_NAME]`.

- [ ] **Step 3: Verify**

```bash
grep -c "PokeManiac\|API 2[0-9]\|iOS 1[0-9]" docs/harness-template/docs/harness/adr/ADR-007-platform-targets.md
```
Expected: `0`

---

## Task 11: Create empty placeholder files

**Files:**
- Create: `docs/harness-template/docs/features/.gitkeep`
- Create: `docs/harness-template/docs/qa/.gitkeep`

- [ ] **Step 1: Create the files**

```bash
touch docs/harness-template/docs/features/.gitkeep
touch docs/harness-template/docs/qa/.gitkeep
```

---

## Task 12: Final verification

- [ ] **Step 1: Count all files**

```bash
find docs/harness-template -type f | sort
```
Expected: 37 files total (35 `.md` files + `settings.json` + `.gitkeep`×2).

- [ ] **Step 2: Scan for remaining PokeManiac references**

```bash
grep -rl "PokeManiac\|shodo\|Fabryo" docs/harness-template/ 2>/dev/null
```
Expected: no output (zero files match).

- [ ] **Step 3: Verify both placeholder tokens appear in CLAUDE.md**

```bash
grep -c "\[PROJECT_NAME\]" docs/harness-template/CLAUDE.md
grep -c "\[PACKAGE_NAME\]" docs/harness-template/.claude/settings.json
```
Expected: both return a count ≥ 1.

- [ ] **Step 4: Validate settings.json**

```bash
jq . docs/harness-template/.claude/settings.json > /dev/null && echo "✅ JSON valid"
```
Expected: `✅ JSON valid`

---

## Self-Review

**Spec coverage (from brainstorming decisions):**
- ✅ Copy-once distribution: `README.md` explains the `cp -r` workflow
- ✅ Audience: Fabrice + colleague (private, no public docs overhead)
- ✅ Pre-filled generic ADRs: all 12 included, ADR-001 and ADR-002 generalized; rest verbatim
- ✅ Full philosophy essay in `docs/philosophy.md`, not in README
- ✅ Short practical README with quickstart
- ✅ Lives in `docs/harness-template/` under `main`

**Placeholder scan:** No TBDs. All new files written in full. All "copy with replacements" tasks specify exact sed commands. All "read source" steps precede writing steps.

**Type consistency:** No function signatures or type references across tasks — documentation only, no consistency risk.

**What's NOT in this template (intentionally):**
- Feature specs (`docs/features/*.md`) — project-specific
- QA regression files (`docs/qa/*.md`) — project-specific
- Filled ADRs for auth, build variants, analytics — write when the feature ships
- The article was not found in git history — `docs/philosophy.md` is written fresh and covers the same ground
