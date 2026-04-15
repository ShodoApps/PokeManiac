# PokeManiac Cursor AI Documentation

This document describes the Cursor AI configuration for the PokeManiac project. These rules and skills enable efficient collaboration with the AI on development tasks.

## Overview

We have created a comprehensive set of **Cursor Rules** and **Skills** that encode PokeManiac's architecture, patterns, and best practices. This enables the AI to:

- Understand the project structure deeply
- Generate code that follows project conventions
- Provide architecture-aware guidance
- Suggest improvements based on established patterns
- Reduce manual context setup in conversations

## Created Resources

### 🎯 Rules (`.cursor/rules/`)

Rules are persistent guidelines that apply automatically to relevant files. They're checked before code generation and suggestions.

#### 1. **pokemaniac-guide.mdc** ⭐ START HERE
- **Scope:** Always apply
- **Purpose:** High-level project overview and quick navigation
- **Contains:** Module architecture, file conventions, quick links to other rules/skills
- **Read first** when starting work on the project

#### 2. **pokemaniac-architecture.mdc**
- **Scope:** Always apply
- **Purpose:** Enforce clean architecture principles
- **Key topics:**
  - Dependency direction (Presentation → Domain → Data)
  - Module organization and responsibilities
  - Key patterns (ViewModel, Repository, DataStore)
  - Naming conventions
  - When adding new features

#### 3. **viewmodel-patterns.mdc**
- **Scope:** When editing `*ViewModel.kt` files
- **Purpose:** Consistent ViewModel implementation patterns
- **Key topics:**
  - State management structure (sealed classes, StateFlow, SharedFlow)
  - Lifecycle handling (ON_START callback)
  - Data mapping patterns
  - Coroutine best practices
  - Testing ViewModels

#### 4. **compose-patterns.mdc**
- **Scope:** When editing Compose files (all `.kt` UI files)
- **Purpose:** Screen vs View pattern, state collection, previews
- **Key topics:**
  - Screen (container) vs View (stateless) split
  - Composable organization
  - Theme & styling patterns
  - State collection best practices
  - Type-safe navigation patterns

#### 5. **kotlin-android-standards.mdc**
- **Scope:** When editing Kotlin files
- **Purpose:** General Kotlin & Android coding standards
- **Key topics:**
  - Immutability first
  - Null safety patterns
  - Coroutines usage
  - Sealed classes vs flags
  - Extension functions
  - String formatting
  - Visibility modifiers
  - Naming conventions

### 🛠️ Skills (`.cursor/skills/`)

Skills are step-by-step guides for complex tasks. The AI reads them when needed and follows the workflows.

#### 1. **pokemaniac-new-feature/** (Skill)
- **When to use:** Adding a new feature module to the app
- **What it covers:**
  - Step-by-step module creation
  - Activity, Screen, ViewModel templates
  - Navigator interface setup
  - UI state sealed class patterns
  - DI module Koin bindings
  - Gradle configuration
  - Application registration
  - Testing setup

#### 2. **pokemaniac-data-layer/** (Skill)
- **When to use:** Extending data access functionality (repos, APIs, database)
- **What it covers:**
  - Repository interface definition (Domain)
  - Request interface (API calls)
  - DataStore interface (Local persistence)
  - Repository implementation (combining sources)
  - API service & request implementation
  - Room DAO & entity patterns
  - DataStore implementation
  - Entity mapping patterns
  - Koin registration

#### 3. **pokemaniac-testing/** (Skill)
- **When to use:** Writing unit tests for features, repos, composables
- **What it covers:**
  - ViewModel testing with Mockito & Turbine
  - Repository testing patterns
  - Compose preview testing
  - Koin test setup
  - Common test patterns (Given/When/Then)
  - Error handling tests
  - Espresso UI testing

## How to Use

### For New Development

1. **Starting a new task:**
   - First read: `pokemaniac-guide.mdc` for overview
   - Then read: The specific rule/skill for your task

2. **Adding a feature:**
   - Read skill: `pokemaniac-new-feature`
   - Follow step-by-step instructions
   - AI will automatically follow patterns from rules

3. **Adding data operations:**
   - Read skill: `pokemaniac-data-layer`
   - Covers repositories, APIs, databases

4. **Writing tests:**
   - Read skill: `pokemaniac-testing`
   - Test patterns are enforced

### In Cursor Conversations

Rules apply **automatically** when:
- You open a ViewModel file → `viewmodel-patterns` applies
- You open a Compose file → `compose-patterns` applies
- You open any Kotlin file → `kotlin-android-standards` applies
- Any code generation → `pokemaniac-architecture` applies

Skills are used by telling the AI:
- "Add a new feature for [feature name]" → AI reads `pokemaniac-new-feature`
- "Add API endpoint for [entity]" → AI reads `pokemaniac-data-layer`
- "Write tests for [component]" → AI reads `pokemaniac-testing`

## File Structure

```
.cursor/
├── rules/
│   ├── pokemaniac-guide.mdc              [Overview & navigation]
│   ├── pokemaniac-architecture.mdc       [Clean architecture]
│   ├── viewmodel-patterns.mdc            [ViewModel standards]
│   ├── compose-patterns.mdc              [Compose patterns]
│   └── kotlin-android-standards.mdc      [Kotlin/Android standards]
│
├── skills/
│   ├── pokemaniac-new-feature/
│   │   └── SKILL.md                      [Add new feature]
│   ├── pokemaniac-data-layer/
│   │   └── SKILL.md                      [Data layer operations]
│   └── pokemaniac-testing/
│       └── SKILL.md                      [Testing patterns]
│
└── PROJECT_DOCUMENTATION.md              [This file]
```

## Quick Reference

### When You Want To...

| Task | Read This |
|------|-----------|
| Understand the project | `pokemaniac-guide.mdc` |
| Add a new feature module | Skill: `pokemaniac-new-feature` |
| Add API/database functionality | Skill: `pokemaniac-data-layer` |
| Write unit tests | Skill: `pokemaniac-testing` |
| Create a ViewModel | Rule: `viewmodel-patterns.mdc` |
| Build Compose screens | Rule: `compose-patterns.mdc` |
| Follow Kotlin conventions | Rule: `kotlin-android-standards.mdc` |
| Understand architecture | Rule: `pokemaniac-architecture.mdc` |

## Project Architecture Summary

```
┌─────────────────────────────────────────────────┐
│     Presentation Layer (Features)               │
│  app + feature:* + coreui                       │
└────────────┬────────────────────────────────────┘
             │ depends on
┌────────────▼────────────────────────────────────┐
│     Domain Layer                                │
│  Repository interfaces + Entities               │
└────────────┬────────────────────────────────────┘
             │ depends on
┌────────────▼────────────────────────────────────┐
│     Data Access Layer                           │
│  data + api + database (implementations)        │
└─────────────────────────────────────────────────┘
```

**Golden Rule:** Dependency direction is strictly enforced. Presentation never imports Data layer directly.

## Key Technologies

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose + Material3
- **Architecture:** Clean Architecture, MVVM
- **State Management:** Coroutines + Flow (StateFlow, SharedFlow)
- **Dependency Injection:** Koin 3
- **Networking:** Retrofit + OkHttp3 + Moshi
- **Persistence:** Room
- **Navigation:** Compose Navigation (type-safe with @Serializable)
- **Testing:** JUnit, Mockito, Turbine, Espresso

## Conventions & Patterns

### Code Organization
- Screen: Container composable that collects state & handles lifecycle
- View: Stateless composable that renders UI
- ViewModel: Manages UI state via StateFlow + SharedFlow
- Repository: Combines API + local data sources

### Naming
- UI models: `*UI` or `*Ui`
- Sealed state classes: `*UiState`
- ViewModels: `*ViewModel`
- Repositories: `*Repository` (interface), `*RepositoryImpl` (impl)
- Screens: `*Screen.kt`, Views: `*View.kt`
- Navigators: `*Navigator` (interface), `*NavigatorImpl` (impl)
- DataStores: `*DataStore` (interface), `*DataStoreImpl` (impl)
- Room entities: `*Base`
- DAOs: `*Dao`

### Best Practices
- Use sealed classes for UI state (not boolean flags)
- Make immutable collections default (PersistentList)
- Map entities at layer boundaries
- Use Flow for continuous streams, suspend for one-time ops
- Always use viewModelScope for coroutines
- Collect StateFlow with collectAsState()
- Reload data on lifecycle ON_START event

## Future: KMP Migration

Authoritative plan: **`docs/kmp-migration-plan.md`**.

- ✅ **Domain** — **`:shared:domain`** (KMP `commonMain`; repository interfaces + entities).
- ✅ **Phase D (Android)** — **`:shared:api`**, **`:shared:data`**, **`:shared:database`**, **`:shared:tracking`**: remote, repositories, Room, and tracking are KMP-shaped with **`androidTarget()`**; see **§7 Phase D** in the plan (status **done**).
- ✅ **Presentation (spike)** — **`:shared:presentation`** + Search Friend **ScreenModel** (**§7 Phase B**).
- ⏳ **Next** — **Phase E** (Koin / DI for multiplatform), **Phase F** (iOS + Apple targets on shared modules where needed); UI stays Compose on Android until then.

## Maintenance

### Updating Rules/Skills

If project patterns change, update the corresponding rule/skill file:

1. Edit the `.mdc` or `SKILL.md` file
2. Update the description/examples
3. The changes apply automatically in new conversations
4. Optionally commit to git: `git add .cursor/ && git commit -m "docs: update AI guidelines"`

### Adding New Guidelines

To add a new rule or skill:

1. Create in `.cursor/rules/` (rules) or `.cursor/skills/yourskill/` (skills)
2. Include clear YAML frontmatter with description
3. Follow existing format/style
4. Document in this file

## Support & Questions

For questions about:
- **Architecture:** See `pokemaniac-architecture.mdc`
- **Patterns:** See the specific rule file (viewmodel, compose, kotlin-android)
- **Workflows:** See the specific skill file (new-feature, data-layer, testing)
- **Project Overview:** See `pokemaniac-guide.mdc`

---

**Last Updated:** April 3, 2026  
**Status:** Complete - Ready for development  
**Version:** 1.0
