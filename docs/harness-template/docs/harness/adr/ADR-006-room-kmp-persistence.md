# ADR-006: Room for KMP Persistence

**Status:** Accepted
**Date:** 2026-06-18

## Context

[PROJECT_NAME] needs local persistence for caching data between sessions and supporting offline use.

During Phase D of the KMP migration, the data layer moved to `commonMain`. We needed a database solution that:
- Works in KMP `commonMain` + `androidTarget` (and eventually `iosTarget`)
- Is familiar to Android developers (no rewrite of knowledge)
- Supports `Flow<>` for reactive queries

Options evaluated:
1. **Room KMP** (official Google library, multiplatform since 2.7.0-alpha) — familiar API, Flow support, schema validation
2. **SQLDelight** — cross-platform SQL, generates type-safe Kotlin, less familiar
3. **Realm Kotlin** — object database, different mental model, heavier runtime

## Decision

We chose **Room KMP** (`androidx.room:room-runtime` with `commonMain` + `androidTarget` source sets).

Room entities live in `shared:database` `commonMain`. DAOs use `Flow<>` for reads and `suspend` for writes. The `[PROJECT_NAME]Database` is built in `app/di/DatabaseModule.kt` using `Room.databaseBuilder(androidApplication(), ...)` — the Android-specific construction stays at the app level.

DataStore **interfaces** live in `shared:data`; DataStore **implementations** live in `shared:database` and are registered in `app/di/DatabaseModule.kt`.

## Consequences

**Easier:**
- Same Room API Android developers already know
- `Flow<>` queries automatically reemit when the underlying table changes
- Schema validation at compile time catches missing migrations before runtime

**Harder:**
- Room KMP requires specific Gradle configuration (`commonMain + androidTarget` source sets with KSP)
- Schema changes to existing tables require explicit migrations — Room will crash on startup if the version is bumped without a migration
- The database builder must run on Android (uses `androidApplication()`) — cannot be in `commonMain`

**Common mistake to avoid:** Bumping `@Database(version = N)` without adding a migration in `DatabaseModule.kt`. The Room migration reminder hook fires when `*Base.kt` or `*Dao.kt` is modified. New tables added to the `entities` array are handled automatically; only modifications to existing table schemas require migrations.
