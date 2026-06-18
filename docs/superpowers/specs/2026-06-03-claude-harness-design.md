# Claude Code Harness — Design Spec

**Date:** 2026-06-03  
**Status:** Approved

## Goal

Migrate the existing Cursor harness (`.cursor/rules/` + `.cursor/skills/`) to a Claude Code harness (`CLAUDE.md` files). Claude Code reads the codebase actively, making templated skills redundant; checklists and constraint rules are folded directly into scoped `CLAUDE.md` files.

---

## File Structure

```
PokeManiac/
├── CLAUDE.md                   ← always loaded; project overview, architecture, DI, KMP status, push/completion checklists
├── feature/
│   └── CLAUDE.md               ← loaded when working in any feature module; Compose, ViewModel, new-feature + test checklists
├── shared/
│   └── CLAUDE.md               ← loaded when working in shared KMP modules; ScreenModel, data layer checklist
└── iosApp/
    └── CLAUDE.md               ← loaded when working in the iOS app; SwiftUI + SKIE patterns
```

No `.claude/skills/` directory. Skills were useful in Cursor because context had to be explicitly provided; Claude Code reads existing implementations directly (e.g. `feature/searchfriend/`) and infers patterns. Checklists are the only part of skills that add value beyond what the codebase already shows, so they live in the nearest relevant `CLAUDE.md`.

---

## Content Breakdown

### Root `CLAUDE.md` — always loaded

- Engineering philosophy (pragmatism over perfection; readable by a senior Android dev who doesn't know the codebase)
- Module architecture at a glance (ASCII tree: app, feature:*, coreui, shared:*)
- Golden rule: Presentation → Domain → Data (with ❌/✅ examples)
- Naming conventions table (ViewModel, ScreenModel, UiState, Repository, Screen, View, Navigator, Koin module, DAO…)
- State management pattern (sealed UiState, single StateFlow, SharedFlow for errors)
- Screen/View composition pattern (Screen = container, View = stateless)
- Koin DI wiring (shared:di commonMain + app Android edge)
- Key libraries table
- i18n, dark mode, tracking quick refs
- KMP migration status (Phase D/E done, F done, G in progress)
- Troubleshooting (ViewModel not receiving data, Koin injection fails, Compose preview not showing)
- Quick navigation ("I'm doing X → read Y" table)
- **Checklist: Before Pushing to Remote**
- **Checklist: After Feature Completion**

### `feature/CLAUDE.md` — loaded when working in any feature module

- Compose Screen vs View pattern (full detail with code examples)
- ViewModel patterns:
  - `@Immutable` on sealed UiState and UI model data classes (Android side only)
  - `UiError` typed error flow (not raw `Exception`)
  - Lifecycle with `OnLifecycleEventEffect` (not raw `DisposableEffect`)
  - `collectAsStateWithLifecycle()` for StateFlow
  - `observeWithLifecycle()` for SharedFlow
- Recomposition:
  - Tier 1 (always apply): `@Immutable`/`@Stable`, `PersistentList`, `key=` on lazy items, `remember(param){}` for derived values
  - Tier 2 (LazyList/Grid screens only): `remember(viewModel, context){}` for lambdas, pass only used fields to leaf composables
  - Which screens are Tier 2: Dashboard, SearchFriend, MyFriendList, MyFriendDetail, MyProfile
- Coroutine dispatcher rules (Main/IO/Default) + always rethrow `CancellationException`
- Type-safe navigation (IDs only in routes, never display data)
- Preview pattern (one preview per UiState branch, wrapped in PokeManiacTheme)
- **Checklist: Adding a New Feature Module**
- **Checklist: Adding Unit Tests**
- **Checklist: Architecture Compliance**
- **Checklist: Code Review (Compose + Coroutines + Testing)**

### `shared/CLAUDE.md` — loaded when working in shared KMP modules

- Module roles table (domain / api / data / database / tracking / di / presentation)
- ScreenModel vs ViewModel: ScreenModel is shared coordinator, ViewModel is thin Android lifecycle shell
- `ScreenModelFactory` + `create(viewModelScope)` pattern (reference: `SearchFriendScreenModelFactory`)
- `UiState`/`UiModel` in `commonMain` = plain Kotlin — no `@Immutable`/`@Stable` in shared; keep those Android-side
- KMP dependency rules (what can import what; features never import from :shared:data/api/database)
- `expect/actual` guidance (`LocalImageCapture` pattern for platform-specific capabilities)
- **Checklist: Adding API Endpoint & Data Operations**

### `iosApp/CLAUDE.md` — loaded when working in the iOS app

- SwiftUI Screen + View split (mirrors Android pattern)
- SKIE flow collection (`SkieFlowCollect.swift` pattern)
- `KotlinViewAdapter` usage
- Lifecycle: `.task`/`onDisappear` for ScreenModel scope
- Existing screens as reference: Welcome, SearchFriend, Dashboard

---

## Source Mapping (Cursor → Claude)

| Cursor file | Destination |
|---|---|
| `pokemaniac-guide.mdc` | Root `CLAUDE.md` |
| `pokemaniac-architecture.mdc` | Root `CLAUDE.md` |
| `viewmodel-patterns.mdc` | `feature/CLAUDE.md` |
| `compose-patterns.mdc` | `feature/CLAUDE.md` |
| `kotlin-android-standards.mdc` | `feature/CLAUDE.md` (condensed) + root `CLAUDE.md` (naming/immutability) |
| `development-checklists.mdc` (new-feature, data, test) | Respective nearest `CLAUDE.md` |
| `development-checklists.mdc` (arch compliance, code review, push, completion) | `feature/CLAUDE.md` + root `CLAUDE.md` |
| `skills/pokemaniac-new-feature/SKILL.md` | Checklist only → `feature/CLAUDE.md` |
| `skills/pokemaniac-data-layer/SKILL.md` | Checklist only → `shared/CLAUDE.md` |
| `skills/pokemaniac-testing/SKILL.md` | Checklist only → `feature/CLAUDE.md` |

---

## Key Decisions

1. **No skills directory.** Claude Code reads existing implementations as reference; templated code walkthroughs add no value over `feature/searchfriend/` or `shared/presentation/`.
2. **Checklists are kept.** They catch registration steps (Koin, Gradle, Application) that aren't inferable from code.
3. **Scoped CLAUDE.md files over @import.** Claude Code auto-loads the nearest CLAUDE.md hierarchy, giving natural scoping without per-file triggers.
4. **No commits by Claude.** User always commits manually.
