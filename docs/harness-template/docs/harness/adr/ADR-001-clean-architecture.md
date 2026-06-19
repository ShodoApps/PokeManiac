# ADR-001: Clean Architecture + SOLID (domain has zero dependencies)

**Status:** Accepted
**Date:** [fill in]

## Context

[PROJECT_NAME] has multiple clients (Android, iOS), multiple data sources (remote API, local database), and business logic that must be testable independently of frameworks. Early versions of many apps mix network code, database code, and UI code in the same classes, which makes testing slow and brittle.

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
