# PokeManiac — Kotlin Multiplatform migration (strategy & plan)

This document records the **agreed rules** and **step-by-step plan** for moving PokeManiac toward Kotlin Multiplatform (KMP), while shipping **Android only** for now and keeping the structure **easy to extend with iOS** later.

It complements `.cursor/rules/pokemaniac-architecture.mdc` and `.cursor/rules/pokemaniac-guide.mdc`. When KMP modules land, those rules should be updated to reference this document where layering differs.

---

## 1. Goals

### 1.1 Near term (current phase)

- Use a **KMP-shaped project**: `commonMain` for shared Kotlin, **`androidTarget()` only** — **no Apple targets** until iOS work starts.
- Keep **Gradle health**: `./gradlew testDebugUnitTest` and `./gradlew assembleRelease` stay green after each incremental change.
- Avoid **big-bang** refactors; migrate **one vertical slice or one layer** per meaningful PR.

### 1.2 Long term

- **Shared business and presentation logic** consumable from **Jetpack Compose** (Android) and **SwiftUI** (iOS).
- **Screen logic** (**`ScreenModel`** in `:shared:presentation`) and **use cases** (when added) live in **shared modules**, not only in Android feature code.
- **AndroidX `ViewModel`** can remain a **thin lifecycle shell** on Android; the portable coordinator is **`XxxScreenModel`** (plain Kotlin). iOS (later) wires scope without AndroidX.

---

## 2. Layering & modules (target shape)

Dependency direction (high level):

```
Android UI (Compose, navigation, Activities)
        → shared presentation (ScreenModels, UiState, events)
        → shared application / use cases (orchestration; optional per feature)
        → shared domain (entities, repository interfaces, errors)
        ↑ implemented by ↑
Data access (network, DB, platform I/O) — Android first; KMP or expect/actual later per slice
```

### 2.1 Recommended module roles

| Layer | Responsibility | Typical module name (to introduce incrementally) |
|--------|----------------|--------------------------------------------------|
| **Domain** | Entities, value types, **repository interfaces**, domain errors | **`:shared:domain`** (KMP; lives under `shared/domain/`) |
| **Application** | Use cases / orchestration (no UI, no platform APIs) | `:shared:application` or part of domain if you want fewer modules |
| **Presentation** | **`ScreenModel`** (shared KMP), **UiState**, user events, `StateFlow`/`Flow`; Android may add thin **`ViewModel`** | **`:shared:presentation`** (KMP; first spike: Search Friend) |
| **Android UI** | Composables, Activities, navigators, Android-specific I/O edges | `:app`, `:feature:*`, `:coreui` |

**Note:** Feature modules can remain **thin Android UI shells** that depend on **shared presentation** — a single monolithic `app` module for all screens is **optional**, not required for KMP.

### 2.2 “App” vs “presentation”

- **Presentation** = shared **`ScreenModel` + `UiState`** (MVVM / MVI oriented); Android **`ViewModel`** is optional glue.
- **App** is not used here as a layer name for screen coordinators; **application** means **use-case / orchestration** layer if split from domain.

---

## 3. Naming conventions (agreed)

### 3.1 Android `ViewModel` vs shared **ScreenModel** (coordinator naming)

- **Android feature module:** keep **`XxxViewModel`** as the type that extends **`androidx.lifecycle.ViewModel`** (Koin, `koinViewModel()`, process retention). It should stay **thin**: forward to shared logic and supply **`viewModelScope`**.
- **Shared (`:shared:presentation`):** use **`XxxScreenModel`** for the KMP screen coordinator (state + flows + user actions). It **must not** extend AndroidX `ViewModel`; the platform passes a **`CoroutineScope`** (on Android, `viewModelScope` from the wrapper).

This avoids **two classes named `SearchFriendViewModel`** and matches a common **KMP migration** approach: AndroidX `ViewModel` is the lifecycle/scope shell; **`ScreenModel`** is the portable “MVVM ViewModel” logic. Other teams use **`Presenter`**, **`Interactor`**, or **`DefaultXxxViewModel`** instead of `ScreenModel`; pick one project-wide (**we use `ScreenModel`**).

Long term, if the project drops AndroidX `ViewModel` entirely, the Android wrapper may go away and UI layers would hold the **`ScreenModel`** (or renamed type) with an explicitly scoped coroutine context.

### 3.2 Screen coordinator vs `XxxUiModel` (naming)

- **`XxxScreenModel`** (shared) — the **screen coordinator** (plain Kotlin). Do **not** name this type **`UiModel`**; that would confuse **controller** vs **row/screen data**.
- **`XxxUiModel`** — **presentation data** the UI renders: labels, flags, formatted fields, row/card content. These types live inside **`XxxUiState`** branches (e.g. a `Data` state holding `List<FriendUiModel>`).
- **Legacy / existing code** may still use **`XxxUI` / `XxxUi`** under feature **`uimodel/`**; new **shared presentation** code should prefer **`XxxUiModel`** where it matches this pipeline.

### 3.3 Types across layers — mapping pipeline (agreed)

End-to-end shape (DTO inward → UI outward):

| Layer | Typical types | Role |
|--------|----------------|------|
| **Data / datasources** | **`XxxDto`**, Room entities, wire formats | Storage and transport shapes only |
| **Repository implementation** | (internal DTOs) | Maps **Dto → domain**; **DTOs do not cross** the repository interface |
| **Domain** (repos + use cases) | **`XxxModel`** where useful, or **named entities** (`User`, `NewActivity`, …) | Business meaning; **shared `commonMain`** for KMP |
| **Presentation** | **`XxxUiModel`** | What the screen needs; built by **ScreenModel** (or coordinator) from domain models |
| **Presentation** | **`XxxUiState`** | **Sealed** screen state: `Loading`, `Empty`, `Error`, **`Data(...)`** holding **`XxxUiModel`(s)** as needed |

**Flow in words:**

1. **Datasources** expose **DTOs** (or equivalents) **only inside the data layer**.
2. **Repository implementations** map **Dto → domain**; **repository interfaces** (in domain) expose **domain types only** — never DTOs.
3. **Use cases** (when present) consume/return **domain** types (entities or use-case-specific results).
4. **Screen coordinators** (`XxxScreenModel` in shared) receive **domain** models from repositories/use cases, **map domain → `XxxUiModel`**, and expose **`StateFlow<XxxUiState>`** (or equivalent) so **views** observe **UiState** that carries **UiModels** where relevant.

**Practical notes (agreed):**

- Not every domain type needs a **`Model`** suffix — **`User`**, **`NewActivity`**, etc. are fine; use **`XxxModel`** when it clarifies a composite or non-entity concept.
- **`XxxUiState`** is not only a bag of UiModels: it includes **loading**, **empty**, **error**, and optional **metadata** (e.g. message text). The **content** branches hold **`XxxUiModel`** lists or single models.
- **One-shot concerns** (navigation, snackbars, IME) often belong in **`UiEvent`**, **callbacks**, or **platform-injected ports** — not necessarily as fields on **`XxxUiModel`**, to avoid bloating presentation models.

### 3.4 UiState location (shared modules)

- **`XxxUiState`**, **`XxxUiModel`**, and **`XxxScreenModel`** live in **shared presentation** (`commonMain`) for parity across **Jetpack Compose** and **SwiftUI** later.
- This matches **MVVM** / **MVI**: **Jetpack Compose** uses `collectAsState()` / `StateFlow`; **SwiftUI** will use the project-chosen Flow interop when iOS starts.

---

## 4. MVVM / MVI and shared code

- **Single source of truth** for screen state: **`StateFlow<XxxUiState>`** (or equivalent) on the shared **`XxxScreenModel`**.
- **Events**: explicit functions or a sealed **event** type, depending on feature complexity (prefer simple methods unless MVI unification helps).
- **Side effects** (navigation, one-shot errors): design **platform-agnostic** contracts (interfaces / callbacks) injected from Android (later iOS), not Android classes referenced from `commonMain`.

---

## 5. Lifecycle (explicit tradeoff)

- Shared **`XxxScreenModel`** uses a **`CoroutineScope`** passed in by the **platform** (starts/cancelled with that scope).
- **Android (current pattern):** a thin **`XxxViewModel`** supplies **`viewModelScope`** to the **`ScreenModel`**; shared code never references AndroidX.
- **iOS (later)**: SwiftUI **`.task` / `onDisappear`** (or equivalent) mirrors the same contract.

This is an **accepted** tradeoff: less magic than AndroidX `ViewModel`, more **explicit** cross-platform control.

---

## 6. Dependency rules (KMP-aware)

- **Presentation** depends on **`:shared:domain`** (and **application** if split), never on **`:shared:data` / `:shared:api` / `:database`**.
- **Android UI** depends on **presentation** + **coreui** + **tracking** (tracking may stay Android-only initially or gain a KMP interface later).
- **Data** implements **domain** repository interfaces; **dependency injection** wires implementations (Koin KMP or equivalent when iOS joins).

Existing golden rule **Presentation → Domain → Data** remains; **shared presentation** is still “presentation” for dependency purposes.

---

## 7. Phased migration plan

### Phase A — KMP foundation (Android target only)

1. Convert **`:shared:domain`** (path `shared/domain/`) to **`kotlin-multiplatform`** + **`com.android.library`**.
2. Move pure Kotlin sources to **`commonMain/kotlin`** (same packages).
3. **`androidTarget()` only** — no iOS targets.
4. Verify **`testDebugUnitTest`** and **`assembleRelease`**.

**Outcome:** Domain is **multiplatform-ready**; adding `iosArm64()` / `iosSimulatorArm64()` later is mostly Gradle/CI work.

**Status — done:** **`:shared:domain`** uses **`org.jetbrains.kotlin.multiplatform`** + **`com.android.library`**, sources in **`src/commonMain/kotlin`**, manifest in **`src/androidMain`**, **`androidTarget()`** only. Version catalog: **`kotlin-multiplatform`** plugin; root `build.gradle.kts` applies it with **`apply false`**.

### Phase B — Shared presentation module (spike)

1. Add **`:shared:presentation`** (KMP, `commonMain` + `androidTarget()`).
2. Migrate **one feature**: shared **`XxxScreenModel`** + **`XxxUiState`**, Android feature module keeps **Compose + thin `ViewModel` + wiring** as needed.
3. Define **minimal** navigation / error ports as interfaces implemented on Android.

**Outcome:** Pattern proven before wide migration.

**Status — done (spike):** **`:shared:presentation`** added; **Search Friend** logic lives in **`com.shodo.android.presentation.searchfriend`** as **`SearchFriendScreenModel`**, plus **`SearchFriendUiState`**, **`SearchFriendUiModel`**, mappers. **`PresentationError`** is the shared user-facing error type. **`feature:searchfriend`** exposes **`SearchFriendViewModel`** (AndroidX) that forwards to **`SearchFriendScreenModel`** using **`viewModelScope`**. Compose imports **`UiState` / `UiModel`** from the shared module.

### Phase C — Shared application / use cases (**optional, defer until needed**)

**Agreement (same as §8 — pragmatism):**

- **No** use-case classes for **trivial** flows (single repository call, no meaningful orchestration or mapping).
- **Shared `ScreenModel`s may call repository interfaces directly** for simple screens; that stays valid for the **current** app complexity.
- Introduce **use cases** only when they **earn their keep**, for example:
  - **Cross-feature** orchestration reused from several screen coordinators
  - **Non-trivial** rules, branching, or composition of several repositories
  - **Testing** a policy in isolation without a heavy `ScreenModel` test

**When you add them:**

1. Add **`:shared:application`** (or fold use cases into **`:shared:domain`** if you prefer fewer modules — both are acceptable).
2. **Extract incrementally** from `ScreenModel`s / Android wrappers where a use case clearly clarifies reuse or tests; do **not** create a mandatory “every screen has a use case” layer.

**For PokeManiac today:** Phase C can remain **theoretical** until a concrete need appears; **A → B → D** (and later E/F) do **not** require a use-case module first.

### Phase D — Data layer (per vertical slice)

1. For each feature: move or wrap **network/DB** behind **domain** contracts using **KMP-friendly** stacks or **`expect`/`actual`**, as pragmatic per slice.
2. Keep **Android Retrofit/Room** behind **`actual`** until a slice justifies **common** networking/storage.

**Status — in progress:**

- **Remote (friends search):** **`:shared:api`** — KMP (`commonMain` + platform engines), Ktor + DTOs; **`FriendsRequest`** implementation lives there.
- **Repository orchestration:** **`:shared:data`** — KMP (`commonMain` + `androidTarget()` only, same pattern as **`:shared:presentation`**). Repository implementations and **datastore interfaces** (`FriendsDataStore`, `MyActivitiesDataStore`, `TrackingDataStore`) are in **`commonMain`**; **`:database`** stays Android (Room) as the **`actual`** side of those contracts.
- **Next slices (when you pick them):** e.g. tighten **`:database`** boundaries only if a feature needs shared persistence types; otherwise leave Room Android-only per §2 above.

### Phase E — DI for multiplatform

1. Evolve **Koin** (or chosen DI) toward **KMP** so iOS can assemble the same graph for shared code.

### Phase F — iOS

1. Add Apple targets to relevant shared modules.
2. SwiftUI screens consume **same `ScreenModel` + `UiState`**; platform code provides **expect/actual** capabilities (e.g. local image capture per project guide).

---

## 8. Pragmatism (project philosophy)

- Do not add **use-case classes** that only wrap a single repository call unless they **earn their keep** (testing, policy, reuse, **cross-feature** orchestration). **`ScreenModel` → repository** is fine for simple flows — see **§7 Phase C**.
- Prefer **explicit, readable** Kotlin over heavy abstraction.
- **Platform-specific capability** stays at **edges** (`expect`/`actual`, not inside dumb entities) — see **pokemaniac-guide** (`ImageSource.FileSource`, future `LocalImageCapture`).

---

## 9. Document maintenance

- When a phase completes, update this file with **“Done”** notes or links to PRs if the team tracks that way.
- When module names differ from the table in §2.1, adjust the table to match **reality** (this doc should stay the **single narrative** for KMP direction).

---

## 10. Summary (agreement checklist)

| Topic | Agreement |
|--------|-----------|
| KMP now, **Android-only targets** initially | Yes — `commonMain` + `androidTarget()` |
| **Screen coordinators** in **presentation** (`ScreenModel` + optional Android `ViewModel`) | Yes |
| **Android** screen type **`XxxViewModel`** (AndroidX) + **shared** **`XxxScreenModel`** (KMP coordinator) | Yes — avoids duplicate simple names |
| Name type **`UiModel`** for the coordinator | No — use **`XxxScreenModel`**; **`XxxUiModel`** is **presentation data** inside **UiState** |
| **DTO → domain (repo impl) → ScreenModel → UiModel in UiState** pipeline | Yes |
| **`XxxUiState` + `XxxUiModel` + `XxxScreenModel` in shared** | Yes — MVVM/MVI friendly for Compose & SwiftUI |
| **Incremental** migration, green CI | Yes |
| **iOS** | Deferred; structure stays **easy to add** |
| **Use cases** | **Optional** — no layer for one-liners; add only when reuse, policy, or real orchestration justify it (**§7 Phase C**, **§8**) |
