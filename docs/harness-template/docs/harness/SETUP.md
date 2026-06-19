# AI Harness — Developer Setup

Welcome to the [PROJECT_NAME] AI harness. This guide covers everything you need to work with Claude Code on this project. Read it once when you join. After reading, the only other file you need for day-to-day work is `docs/harness/WORKFLOW.md`.

---

## What Is the Harness?

The harness is the collection of rules, documentation, and automation that makes Claude Code a reliable co-developer on this project. It has five parts:

| Part | What it does |
|---|---|
| **CLAUDE.md files** | Rules loaded automatically into every Claude session — architecture constraints, key patterns, navigation pointers |
| **`docs/harness/`** | Reference library — detailed patterns, checklists, ADRs, workflow. Read by Claude on demand when the task calls for it. |
| **`docs/features/`** | Functional specs — what each feature does, for users and for QA |
| **`docs/qa/`** | QA handoff docs — generated before each ship |
| **`.claude/settings.json`** | Hooks — automated guards that run on every file edit |

---

## 1. Install Claude Code

Download the desktop app or VS Code extension from https://claude.ai/code.

---

## 2. Install Required Plugins

Add the following to your `~/.claude/settings.json` (global settings, not the project file):

```json
{
  "enabledPlugins": {
    "superpowers@claude-plugins-official": true,
    "swiftui-pro@swiftui-agent-skill": true,
    "swift-concurrency-pro@swift-concurrency-agent-skill": true
  },
  "extraKnownMarketplaces": {
    "swiftui-agent-skill": {
      "source": { "source": "github", "repo": "twostraws/SwiftUI-Agent-Skill" }
    },
    "swift-concurrency-agent-skill": {
      "source": { "source": "github", "repo": "twostraws/Swift-Concurrency-Agent-Skill" }
    }
  }
}
```

---

## 3. Install Local Linters

Hooks run on every file edit and require two linters:

**ktlint** (formats and lints Kotlin files):
```bash
brew install ktlint
ktlint --version   # verify
```

**SwiftLint** (formats and lints Swift files):
```bash
brew install swiftlint
swiftlint version  # verify
```

If either is missing, its hook silently skips — no errors, but no formatting either.

---

## 4. Skills — Your AI Tools

Skills are slash commands that give Claude specialized capabilities. Two kinds are available in this project:

**Plugin skills** (from installed plugins — superpowers, swiftui-pro, swift-concurrency-pro):

| Skill | When to use | What it does |
|---|---|---|
| `/brainstorming` | Starting a feature, architectural change, or design workshop | Explores the problem, proposes 2-3 approaches, produces a design spec, hands off to writing-plans |
| `/writing-plans` | After a spec is approved | Reads the spec, produces a step-by-step implementation plan |
| `/code-review` | Before marking a feature ready to ship | Reviews branch changes — architecture, quality, test coverage |
| `swiftui-pro` | iOS SwiftUI views and state management | Expert SwiftUI guidance for this project's architecture |
| `swift-concurrency-pro` | iOS async/await and structured concurrency | Expert Swift concurrency guidance |

**Project-local skill** (defined in `.claude/skills/` — specific to this project):

| Skill | When to use | What it does |
|---|---|---|
| `/write-feature-spec` | Documenting any feature — solo, in grooming, or in a workshop | Interviews you one question at a time (purpose → flows → screens → rules → edge cases → out of scope), then writes a spec to `docs/features/` |

**Workshop / grooming use of `/write-feature-spec`:** Run this skill during a team grooming session or design workshop. Claude asks questions that surface gaps the team may not have thought of, captures the answers as the discussion happens, and the spec is ready when the session ends. No notes to transcribe afterward.

**How to invoke `/write-feature-spec`:** Type `/write-feature-spec` in Claude Code. If the slash command isn't available (project-local skill discovery depends on Claude Code version), ask Claude: "use the write-feature-spec skill to document [feature name]" — Claude will read `.claude/skills/write-feature-spec.md` and run the interview.

---

## 5. Hooks — Automatic Guards

The project hooks in `.claude/settings.json` run automatically on every file edit. No configuration required.

| Hook | Fires when | What it does |
|---|---|---|
| **No-commit guard** | Claude tries `git commit` | Blocks it — you always commit manually |
| **ktlint** | Any `.kt` file edited | Auto-formats, then reports remaining violations |
| **SwiftLint** | Any `.swift` file edited | Auto-fixes, then reports remaining violations |
| **Architecture guard** | `.kt` in `feature/` or `coreui/` edited | Blocks imports from `:shared:data/api/database` |
| **commonMain purity** | `.kt` in `commonMain/` edited | Blocks `android.*` / `androidx.*` imports |
| **ViewModel memory leak** | `*ViewModel.kt` edited | Detects `Context` / `Activity` references |
| **Debug log detector** | Any `.kt` edited (non-test) | Flags `Log.*` / `println` left in production code |
| **Hardcoded string detector** | `.kt` in `feature/` or `coreui/` edited | Flags `Text("literal")` that should use `stringResource` |
| **Room migration reminder** | `*Base.kt` or `*Dao.kt` edited | Reminds to bump `@Database(version)` and add migration |
| **Gradle change reminder** | `build.gradle.kts` edited | Reminds to run tests + lint + release build + check ProGuard |
| **iOS permission check** | `.swift` edited with permission APIs | Reminds to add `NS*UsageDescription` to `Info.plist` |
| **Missing test reminder** | New `*ViewModel.kt` or `*ScreenModel.kt` created | Reminds to create the corresponding test file |

---

## 6. Development Workflow

See `docs/harness/WORKFLOW.md` for the complete loop with all steps, rules, and grooming/workshop variants.

Short version:
```
brainstorm → spec → plan → implement → test gate → review → ship
```

---

## 7. Where to Find Things

| Resource | Location | Purpose |
|---|---|---|
| Architecture rules | `CLAUDE.md` | Always-loaded — Claude sees this every session |
| Feature module patterns | `docs/harness/patterns/feature-patterns.md` | Screen/View, ViewModel, coroutines, recomposition |
| KMP patterns | `docs/harness/patterns/kmp-patterns.md` | ScreenModel/ViewModel, commonMain, expect/actual |
| iOS patterns | `docs/harness/patterns/ios-patterns.md` | KotlinViewAdapter, SKIE, SwiftUI, permission flow |
| DI patterns | `docs/harness/patterns/di-wiring.md` | Koin wiring for features and shared modules |
| Naming conventions | `docs/harness/patterns/naming-conventions.md` | All naming rules + module tree |
| New feature checklist | `docs/harness/checklists/new-feature.md` | Step-by-step: add a feature module |
| Data layer checklist | `docs/harness/checklists/data-layer.md` | Step-by-step: add API + repository + DB |
| Test checklist | `docs/harness/checklists/unit-tests.md` | Android ViewModel tests + KMP ScreenModel tests |
| Code review checklist | `docs/harness/checklists/code-review.md` | Architecture + Compose + coroutines review |
| QA handoff template | `docs/harness/checklists/qa-handoff-template.md` | Copy + fill before shipping |
| Why decisions were made | `docs/harness/adr/README.md` | Architecture Decision Records index |
| Feature functional specs | `docs/features/` | What each feature does (product perspective) |
| QA handoff docs | `docs/qa/` | Generated before each ship |
| Product wiki / roadmap | (add your own link here) | Business plan, feature list, remaining features |
