![pokemaniac](https://github.com/user-attachments/assets/a9b47eed-3174-41b6-8840-5ad9ea2f5c0b)

# Pokemaniac – Proof of Concept (POC)

Welcome to the Pokemaniac POC!
This document provides a complete overview of the app’s current state, including technical implementation, business logic, and what’s coming next.

The whole documentation is available in [the Github's Wiki](https://github.com/Fabryo/PokeManiac/wiki).


## How to Run the App

Instructions for installing and launching the app locally are available [here](https://github.com/Fabryo/PokeManiac/wiki/Setup).

## Architecture & Tech Stack

 * Modular architecture following Clean Architecture principles
 * Fully built with Jetpack Compose
 * Key patterns: MVVM, StateFlow, DI with Koin, etc.
 * Libraries: Retrofit, Room, Coil, Coroutines, Flow, Koin, etc.

#### Module Dependency Diagram
   
```mermaid
flowchart TD

    %% Global style for dark mode
    classDef darkMode fill:#000000,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF;
    
    subgraph Presentation_Layer
        direction TB
        App["App (Main entry)"]
        Dashboard["Dashboard Feature"]
        SearchFriends["SearchFriends Feature"]
        MyFriends["MyFriends Feature"]
        MyProfile["MyProfile Feature"]
        NewPost["NewPost Feature"]
        CoreUI["CoreUI Module (Shared Compose Views)"]
    end

    subgraph Domain_Layer
        direction TB
        Domain["Domain (Repository Interfaces + Entities)"]
    end

    subgraph Data_Layer
        direction TB
        Data["Data Module (Repository Implementations)"]
        Api["API Module (Networking Services)"]
        Database["Database Module (Room Persistence)"]
    end

    subgraph Tracking_Layer
        Tracking["Tracking Module (Analytics + Events Storage)"]
    end

    %% -- Flows inside Presentation Layer
    App --> Dashboard
    App --> SearchFriends
    App --> MyFriends
    App --> MyProfile
    App --> NewPost

    App --> CoreUI
    Dashboard --> CoreUI
    SearchFriends --> CoreUI
    MyFriends --> CoreUI
    MyProfile --> CoreUI
    NewPost --> CoreUI

    %% -- External Flows
    Presentation_Layer --> Domain_Layer
    Presentation_Layer --> Tracking_Layer
    Tracking_Layer --> Domain_Layer

    Data_Layer --> Domain_Layer
    Database --> Data

    %% Apply dark mode style
    linkStyle default stroke:#FFFFFF,stroke-width:1.5px
    class App,Dashboard,SearchFriends,MyFriends,MyProfile,NewPost,CoreUI,Domain,Data,Api,Database,Tracking darkMode
```

#### Architecture Data Flow


```mermaid
flowchart TD

    %% Global style for dark mode
    classDef darkMode fill:#000000,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF;

    subgraph Presentation_Module
        direction TB
        Activity["Activity / Composable"]
        ViewModel["ViewModel"]
    end

    subgraph Domain_Module
        direction TB
        RepositoryInterface["Repository Interface"]
    end

    subgraph Data_Module
        direction TB
        RepositoryImpl["Repository Implementation"]
        RequestInterface["Request Interface"]
        DataStoreInterface["DataStore Interface"]
    end

    subgraph Database_Module
        direction TB
        DataStoreImpl["DataStore Implementation"]
        RoomDatabase["Room Database"]
    end

    subgraph API_Module
        direction TB
        RequestImpl["Request Implementation"]
    end

    subgraph External_Services
        direction TB
        RemoteApi["Remote API"]
    end

    %% Calls (flow top-down)
    Activity -->|User Actions| ViewModel
    ViewModel -->|Call Repository| Domain_Module
    RepositoryInterface -->|Implementation| RepositoryImpl
    RepositoryImpl -->|Fetch Remote Data| RequestInterface
    RepositoryImpl -->|Fetch Local Data| DataStoreInterface
    RequestInterface -->|Implementation| RequestImpl
    DataStoreInterface --> |Implementation| DataStoreImpl
    RequestImpl -->|Call External API| RemoteApi
    DataStoreImpl -->|Fetch Local Data| RoomDatabase

    %% Responses (horizontal dotted lines, no "up")
    ViewModel -.->|StateFlow/SharedFlow| Activity
    Domain_Module -.->|Flow Data| ViewModel
    RemoteApi -.->|API Response| RequestImpl
    RoomDatabase -.->|Flow DB Entities| DataStoreImpl
    RequestInterface -.->|Remote Data| RepositoryImpl
    DataStoreInterface -.->|Flow Local Data| RepositoryImpl

    %% Styling for dark mode
    linkStyle default stroke:#FFFFFF,stroke-width:1.5px
    class Activity,ViewModel,RepositoryInterface,RepositoryImpl,RequestInterface,DataStoreInterface,RequestImpl,DataStoreImpl,RoomDatabase,RemoteApi darkMode
```

The Architecture in detail is available [here](https://github.com/Fabryo/PokeManiac/wiki/Architecture-&-Tech-choices#architecture).

More information about the Tech stack is available [here](https://github.com/Fabryo/PokeManiac/wiki/Architecture-&-Tech-choices#tech-choices).

## Concept & Business Plan
 * A social network dedicated to Pokémon card collectors
 * Key features: card library, friend interactions, transaction sharing, and a future marketplace
 * Monetization options: subscriptions, ads, transaction fees

The whole Concept & Business Plan are available [here](https://github.com/Fabryo/PokeManiac/wiki/Concept-and-Business-Plan).

## Business Features Implemented
 * NewsFeed with friend transactions
 * Friend search and subscriptions
 * Profile screen with personal transaction history
 * Transaction posting flow (photo, Pokémon name, price)
 * Local data persistence

More information, screens and videos about the implemented Business Features are available [here](https://github.com/Fabryo/PokeManiac/wiki/Implemented-Business-Features).

## Technical Features Implemented
 * i18n: French 🇫🇷 and English 🇬🇧 supported
 * Dark Mode support
 * Tracking module (mocked, ready to connect to Firebase, Segment…)
 * Unit testing examples across all layers (Request, Repository, UseCase, ViewModel)
 * Jetpack Compose Previews for UI testing

More information about the implemented Tech Features are available [here](https://github.com/Fabryo/PokeManiac/wiki/Tech-Features#implemented-tech-features-).

## Technical Features To Be Added
 * Crash reporting & code quality tools: Crashlytics, Sonar, Lint…
 * Accessibility support
 * Proguard / R8 obfuscation for code security
More information about the backlogged Tech Features are available [here](https://github.com/Fabryo/PokeManiac/wiki/Tech-Features#technical-features-to-add).

## Key Business Features To Be Implemented
 * Full Sign In / Sign Up flow
 * Onboarding journey & subscription paywall
 * Bottom navigation with tabs (Home, Pokédex, Card Search, Marketplace)
 * Marketplace with secure transactions
 * Tablet layout & responsive design
 * AI card recognition and valuation

More information about the backlogged Business Features are available [here](https://github.com/Fabryo/PokeManiac/wiki/Remaining-Features-to-implement)

---

## AI Harness

This project is developed with [Claude Code](https://claude.ai/code) using a structured harness. The harness makes Claude a reliable, consistent co-developer — it knows the architecture, enforces rules automatically, and documents decisions.

**New to this project?** Read `docs/harness/SETUP.md` first — it covers installation, available tools, and how the workflow operates.

---

### How It Works

The harness has five layers:

**1. CLAUDE.md files — always-loaded rules**
Four scoped files loaded automatically into every Claude session:
- `CLAUDE.md` — project-wide: architecture, golden rule, KMP status, available skills, navigation
- `feature/CLAUDE.md` — feature module rules (loaded when working in `feature/`)
- `shared/CLAUDE.md` — KMP shared module rules (loaded when working in `shared/`)
- `iosApp/CLAUDE.md` — iOS rules (loaded when working in `iosApp/`)

**2. `docs/harness/` — reference library (read on demand)**
Detailed documentation Claude reads when the task calls for it:
- `patterns/` — full code patterns with examples: Compose, KMP, iOS, DI, naming
- `checklists/` — step-by-step task guides: new feature, data layer, unit tests, code review, QA handoff
- `adr/` — Architecture Decision Records: why decisions were made, what not to undo
- `WORKFLOW.md` — the development loop, grooming workflow, workshop workflow
- `SETUP.md` — how to configure your environment and what tools are available

**3. `docs/features/` — functional specs**
One markdown file per feature describing what it does for users: flows, screens, business rules, edge cases. Written via the `/write-feature-spec` skill — works solo, in grooming sessions, or during design workshops.

**4. `docs/qa/` — QA handoff documents**
Generated by Claude before each ship. Tells QA what changed, what to test, what the before/after is, and a "don't forget" checklist (airplane mode, small screens, dark mode, accessibility, etc.).

**5. `.claude/settings.json` — automated hooks**
Guards that run on every file edit — no configuration required:

| Guard | What it catches |
|---|---|
| Architecture guard | Feature modules importing from data/api/database layers |
| commonMain purity | Android imports leaking into shared KMP code |
| ktlint | Kotlin style violations (auto-formats, reports remainder) |
| SwiftLint | Swift style violations (auto-fixes, reports remainder) |
| ViewModel memory leak | `Context`/`Activity` references in ViewModels |
| Debug log detector | `Log.*` / `println` left in production code |
| Hardcoded string detector | UI string literals that should use `stringResource` |
| Room migration reminder | Entity/DAO changes that may need a database migration |
| iOS permission check | Permission API usage without `Info.plist` declaration |
| No-commit guard | Claude attempting to commit (all commits are made by the developer) |

---

### Skills Available

**Plugin skills** (require plugin installation — see `docs/harness/SETUP.md`):

| Skill | What it does |
|---|---|
| `/brainstorming` | Design session → spec file. Also runs architecture workshops. |
| `/writing-plans` | Spec → step-by-step implementation plan |
| `/code-review` | Full review of current branch before shipping |
| `swiftui-pro` | Expert SwiftUI guidance |
| `swift-concurrency-pro` | Expert Swift concurrency guidance |

**Project-local skill** (in `.claude/skills/` — no plugin needed):

| Skill | What it does |
|---|---|
| `/write-feature-spec` | Conversational interview → functional spec. Works solo, in grooming, or in workshops. |

---

### Development Workflow (short version)

```
brainstorm → spec → plan → implement → test gate → review → ship
```

Full workflow including grooming and workshop variants: `docs/harness/WORKFLOW.md`

