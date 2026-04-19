# PokeManiac — Kotlin Multiplatform migration (strategy & plan)

This document records the **agreed rules** and **step-by-step plan** for moving PokeManiac toward Kotlin Multiplatform (KMP). **Android** stays the primary shipped UI (**Compose** in `feature:*` / `coreui`). **iOS** (**Phase G**) proceeds **in parallel**: Apple targets and `iosMain` code land on shared modules as needed; Android-only screens can keep evolving without blocking iOS integration work.

It complements `.cursor/rules/pokemaniac-architecture.mdc` and `.cursor/rules/pokemaniac-guide.mdc`. When KMP modules land, those rules should be updated to reference this document where layering differs.

---

## 1. Goals

### 1.1 Near term (current phase)

- Use a **KMP-shaped project**: `commonMain` for shared Kotlin; **`androidTarget()`** on shared modules today; **Apple targets** (`iosArm64`, `iosSimulatorArm64`, …) are added **incrementally** on each shared module as **§7 Phase G** work touches it (**`:shared:domain`** already declares Apple targets).
- Keep **Gradle health**: `./gradlew testDebugUnitTest` and `./gradlew assembleRelease` stay green after each incremental change; add **iOS compile/link** (or Xcode) checks when Apple targets exist so `iosMain` does not rot.
- Avoid **big-bang** refactors; migrate **one vertical slice or one layer** per meaningful PR.
- **Phase D** and **Phase E (Android)** are **done** for the current app scope — see **§7**. **Phase F** (shared **`ScreenModel`** / **`UiState`** for features) and **Phase G** (iOS app + shared `iosMain`) may **overlap**: Android UI stays in Android modules; **Phase F’s last step** (single Android deployable module shape — **TBD**) is **not** a hard gate to **start** Phase G. New data/API slices can land anytime using the same patterns as Phase D.

### 1.2 Long term

- **Shared business and presentation logic** consumable from **Jetpack Compose** (Android) and **SwiftUI** (iOS).
- **Screen logic** (**`ScreenModel`** in `:shared:presentation`) and **use cases** (when added) live in **shared modules**, not only in Android feature code.
- **AndroidX `ViewModel`** can remain a **thin lifecycle shell** on Android; the **shared** screen coordinator is **`XxxScreenModel`** (plain Kotlin). iOS (later) wires scope without AndroidX.

---

## 2. Layering & modules (target shape)

Dependency direction (high level):

```
Android UI (Compose, navigation, Activities)
        → shared presentation (ScreenModels, UiState, events)
        → shared application / use cases (orchestration; optional per feature)
        → shared domain (entities, repository interfaces, errors)
        ↑ implemented by ↑
Data access — **`:shared:api`** (HTTP + DTOs), **`:shared:data`** (repository impls + datastore interfaces), **`:shared:database`** (Room + datastore impls), **`:shared:tracking`** (`TrackingRepository` impl + Koin module); all use **`commonMain` + `androidTarget()`** today unless noted below.
```

### 2.1 Recommended module roles

| Layer | Responsibility | Module in this repo |
|--------|----------------|---------------------|
| **Domain** | Entities, value types, **repository interfaces**, domain errors | **`:shared:domain`** (`shared/domain/`); **`androidTarget()`** + **Apple targets** (early iOS prep) |
| **Application** | Use cases / orchestration (no UI, no platform APIs) | *Optional* — `:shared:application` or fold into domain (**§7 Phase C**); not used yet |
| **Presentation** | **`ScreenModel`**, **UiState**, `StateFlow`/`Flow`; Android may add thin **`ViewModel`** | **`:shared:presentation`** (`shared/presentation/`) |
| **Remote HTTP** | Ktor clients, DTOs, `*RequestImpl` | **`:shared:api`** (`shared/api/`) |
| **Repository orchestration** | Repository implementations; **DataStore** *interfaces* consumed by repos | **`:shared:data`** (`shared/data/`) |
| **Local DB** | Room entities, DAOs, **DataStore** *implementations* | **`:shared:database`** (`shared/database/`) |
| **Analytics wiring** | **`TrackingRepository`** implementation + **`trackingModule`** (Koin) | **`:shared:tracking`** (`shared/tracking/`) |
| **Android UI** | Composables, Activities, navigators, Android-specific I/O edges | `:app`, `:feature:*`, `:coreui` |
| **DI (`commonMain`)** | Koin **`koin-core`** modules (API + repository factories, no `Context`) | **`:shared:di`** |
| **DI (Android edge)** | `startKoin`, `androidContext`, `Room.databaseBuilder`, feature `viewModelOf` | **`app`** (`PokeManiacApplication` + `di/`) |

**Note:** Feature modules can remain **thin Android UI shells** that depend on **shared presentation** until **§7 Phase F** (final step) refactors the Android surface into **one deployable module** (exact layout **TBD**).

### 2.2 “App” vs “presentation”

- **Presentation** = shared **`ScreenModel` + `UiState`** (MVVM / MVI oriented); Android **`ViewModel`** is optional glue.
- **App** is not used here as a layer name for screen coordinators; **application** means **use-case / orchestration** layer if split from domain.

---

## 3. Naming conventions (agreed)

### 3.1 Android `ViewModel` vs shared **ScreenModel** (coordinator naming)

- **Android feature module:** keep **`XxxViewModel`** as the type that extends **`androidx.lifecycle.ViewModel`** (Koin, `koinViewModel()`, process retention). It should stay **thin**: forward to shared logic and supply **`viewModelScope`**.
- **Shared (`:shared:presentation`):** use **`XxxScreenModel`** for the KMP screen coordinator (state + flows + user actions). It **must not** extend AndroidX `ViewModel`; the platform passes a **`CoroutineScope`** (on Android, `viewModelScope` from the wrapper).

This avoids **two classes named `SearchFriendViewModel`** and matches a common **KMP migration** approach: AndroidX `ViewModel` is the lifecycle/scope shell; **`ScreenModel`** is the **shared** “MVVM ViewModel” logic. Other teams use **`Presenter`**, **`Interactor`**, or **`DefaultXxxViewModel`** instead of `ScreenModel`; pick one project-wide (**we use `ScreenModel`**).

Long term, if the project drops AndroidX `ViewModel` entirely, the Android wrapper may go away and UI layers would hold the **`ScreenModel`** (or renamed type) with an explicitly scoped coroutine context.

### 3.2 Screen coordinator vs `XxxUiModel` (naming)

- **`XxxScreenModel`** (shared) — the **screen coordinator** (plain Kotlin). Do **not** name this type **`UiModel`**; that would confuse **controller** vs **row/screen data**.
- **`XxxUiModel`** — **presentation data** the UI renders: labels, flags, formatted fields, row/card content. These types live inside **`XxxUiState`** branches (e.g. a `Data` state holding `List<FriendUiModel>`).
- **Legacy / existing code** may still use **`XxxUI` / `XxxUi`** under feature **`uimodel/`**; new **shared presentation** code should prefer **`XxxUiModel`** where it matches this pipeline.

### 3.2b Compose stability (`@Immutable` / `@Stable`) — Android habit vs shared `commonMain`

- **Goal:** Keep **recomposition-friendly** types on the **Android / Compose** side where possible.
- **Phase F (current):** **`XxxUiModel` / `XxxUiState` in `:shared:presentation`** stay **plain Kotlin** (no Compose runtime in shared). **Do not** add `@Immutable` / `@Stable` in `commonMain` for this migration step.
- **Hygiene:** On **Android** (`feature:*`, `coreui`), continue to use **`@Immutable` / `@Stable`** on types that **Composables** consume directly when it helps the compiler — **feature-local** wrappers or existing feature `uimodel` types if you split them from shared. Mitigate with **primitives in leaf composables** and **`PersistentList`** (see **`compose-patterns.mdc`**).

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

- **Presentation** depends on **`:shared:domain`** (and **application** if split), never on **`:shared:data` / `:shared:api` / `:shared:database`**.
- **Android UI** depends on **presentation** + **coreui**; add **`implementation(project(":shared:tracking"))`** only when the feature needs **`TrackingRepository`** on the classpath. Feature code imports **`TrackingRepository`** from **`:shared:domain`** only — not implementation types from **`:shared:tracking`** (see architecture rules).
- **Data** implements **domain** repository interfaces; **dependency injection** wires implementations (Koin KMP or equivalent when iOS joins).

Existing golden rule **Presentation → Domain → Data** remains; **shared presentation** is still “presentation” for dependency purposes.

---

## 7. Phased migration plan

### Phase A — KMP foundation

1. Convert **`:shared:domain`** (path `shared/domain/`) to **`kotlin-multiplatform`** + **`com.android.library`**.
2. Move pure Kotlin sources to **`commonMain/kotlin`** (same packages).
3. Ship **`androidTarget()`** first; **Apple targets** on **`:shared:domain`** are optional early prep for **Phase G (iOS)** (other shared modules still **`androidTarget()`-only** until iOS work).
4. Verify **`testDebugUnitTest`** and **`assembleRelease`**.

**Outcome:** Domain is **multiplatform-ready**; shared data/presentation modules can add Apple targets when **Phase G** starts.

**Status — done:** **`:shared:domain`** uses **`org.jetbrains.kotlin.multiplatform`** + **`com.android.library`**, sources in **`src/commonMain/kotlin`**, manifest in **`src/androidMain`**, **`androidTarget()`** plus **`iosArm64()`** / **`iosSimulatorArm64()`**. Version catalog: **`kotlin-multiplatform`** plugin; root `build.gradle.kts` applies it with **`apply false`**.

### Phase B — Shared presentation module (spike)

1. Add **`:shared:presentation`** (KMP, `commonMain` + `androidTarget()`).
2. Migrate **one feature**: shared **`XxxScreenModel`** + **`XxxUiState`**, Android feature module keeps **Compose + thin `ViewModel` + wiring** as needed.
3. Define **minimal** navigation / error ports as interfaces implemented on Android.

**Outcome:** Pattern proven before **§7 Phase F** (wide feature migration).

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

**For PokeManiac today:** Phase C can remain **theoretical** until a concrete need appears; **A → B → D** (and later **E → F → G**) do **not** require a use-case module first.

### Phase D — Data layer (per vertical slice)

1. For each feature: move or wrap **network/DB** behind **domain** contracts using **KMP-friendly** stacks or **`expect`/`actual`**, as pragmatic per slice.
2. Prefer **shared `commonMain`** for wire formats and repository orchestration when the stack supports it; keep **platform-only** code (e.g. `Room.databaseBuilder(Context)`) at the **Android edge** (**`app`**, future `expect`/`actual`).

**Status — done (Android KMP data stack, current app scope):**

| Slice | Module | Notes |
|--------|--------|--------|
| **Friends / hero search (HTTP)** | **`:shared:api`** | KMP `commonMain` + platform engines; **Ktor**; DTOs + **`FriendsRequestImpl`**; domain **`FriendsRequest`** stays in **`:shared:domain`**. |
| **Repositories** | **`:shared:data`** | **`UserRepositoryImpl`**, **`NewsFeedRepositoryImpl`**, **`MyProfileRepositoryImpl`**; **DataStore interfaces** (`FriendsDataStore`, `MyActivitiesDataStore`, `TrackingDataStore`) in **`commonMain`**. |
| **Room + DataStore impls** | **`:shared:database`** | KMP **`commonMain`** + **`androidTarget()`**; **Room** plugin + **`kspAndroid`**; **`androidx.sqlite:sqlite-bundled`**; type converters use **`kotlinx.serialization`**. **`Room.databaseBuilder`** lives in **`app`** (`DatabaseModule`). |
| **Tracking repository** | **`:shared:tracking`** | **`TrackingRepositoryImpl`** + **`trackingModule`** (Koin); depends on **`:shared:data`** / **`:shared:domain`**. |

**Verification (agreed in §1.1):** `./gradlew testDebugUnitTest` and `./gradlew assembleRelease` should stay **green** after data-layer changes.

**Deferred (not part of “Phase D done”):**

- **Apple targets** on **`:shared:api`**, **`:shared:data`**, **`:shared:database`**, **`:shared:tracking`** — when iOS ships; persistence needs platform DB builders per [Room KMP](https://developer.android.com/kotlin/multiplatform/room).
- **New vertical slices** — additional HTTP backends or persistence when new features require them (same patterns as above).

### Phase E — DI for multiplatform

1. Evolve **Koin** (or chosen DI) toward **KMP**: split **what can live in `commonMain`** from **what must stay on the platform edge** (Android `Context`, `Room.databaseBuilder`, feature `viewModelOf`, etc.).

**Agreement:** **iOS `startKoin` and platform-specific Koin modules** are **deferred to Phase G** — they require an **iOS app / entrypoint**, which does not exist yet. Phase E is **complete for the current Android-only product** once the split below is in place.

**Status — done (Android, current repo):**

- **`:shared:di`** (KMP, `commonMain` + `androidTarget()`): **`apiModule`** + **`dataModule`** in `commonMain` (`koin-core` only); **`sharedKoinArchiModules`** list intended for **reuse from any future platform** `startKoin` (see **Phase G**).
- **`app`**: **`startKoin`** + **`databaseModule`** (`Room.databaseBuilder`, DataStore singletons) + **`appCoreArchiModules()`** = `databaseModule` + **`sharedKoinArchiModules`**; feature modules unchanged.

**Validation (Phase E closure — Android):** run **`./gradlew testDebugUnitTest`** and **`./gradlew assembleRelease`**; both should stay green (same bar as §1.1).

**ScreenModel + Koin (Android):** shared **`ScreenModel`**s are **not** constructed with a bare `factory { ScreenModel(get(), …) }` — they need a **`CoroutineScope`**. Use a **`fun interface` factory** registered as **`factory`** in the feature `di` module; the AndroidX **`ViewModel`** takes that factory and calls **`create(viewModelScope)`** (see **`.cursor/rules/viewmodel-patterns.mdc`** — “Shared ScreenModel + Koin”). On iOS (**Phase G**), the same **`ScreenModel`** receives a scope from SwiftUI / Kotlin entry; **Koin wiring for that path** lands with the iOS project.

### Phase F — Shared presentation (all features) + Android app shape

**Goal:** Every user-facing feature follows the **Search Friend** reference: **`XxxScreenModel`**, **`XxxUiState`**, **`XxxUiModel`** in **`:shared:presentation`**, thin **`XxxViewModel`** on Android, and Koin **`fun interface` factories** where a **`CoroutineScope`** is required (see **`.cursor/rules/viewmodel-patterns.mdc`**). **Android** keeps **Compose**, Activities, navigators, and Android-specific I/O in **`feature:*`** / **`coreui`** — that code can evolve **while** **Phase G** adds iOS.

**Last step of this phase (optional timing vs Phase G):** consolidate the **Android deployable surface** into **one application module** (or one agreed tree) — exact Gradle layout **TBD** (e.g. nested **`:androidApp:feature:searchfriend`**-style modules **or** a single **`:app`** with feature source sets / packages). **Decision:** this refactor **does not block** starting **Phase G**; schedule it when it reduces Android maintenance cost.

**Steps (incremental — prefer one feature or one structural PR at a time):**

1. **Per feature** (dashboard, my friends, my profile, post transaction, billing, welcome, …): move coordinator logic to **`commonMain`** in **`:shared:presentation`**; keep Compose, Activities, and navigators on Android; align Koin with **Phase E** (feature `di` modules, **`viewModelOf`** where applicable).
2. **Ports:** keep navigation and one-shot errors as **small interfaces** (e.g. in **`coreui`**) or shared presentation, same spirit as the Search Friend spike.
3. **Final step — Android module shape:** perform the **single-module (or single-tree) Android app** refactor when useful; update **`settings.gradle.kts`**, **`app`** dependencies, and **`startKoin`** module lists as needed. **Validate** **`./gradlew testDebugUnitTest`** and **`./gradlew assembleRelease`** after each meaningful structural change. **Not** required before **Phase G** kickoff.

**Status — in progress.** **Dashboard**, **Post Transaction** (`com.shodo.android.presentation.posttransaction`), **Billing** (`BillingScreenModel`, **`BillingUiState`** in **`com.shodo.android.presentation.billing`**), **Search Friend**, **My Friends**, **My Profile**. Remaining: welcome/placeholder flows if any; then **final Android app module shape** (§7 Phase F last step).

**Phase G can start in parallel** once shared presentation (and any feature **`ScreenModel`** you want on iOS first) is stable enough; remaining Phase F tidy-up (welcome flows, app module shape) continues on the Android side as needed.

**Deferred to Phase G:** SwiftUI app shell, iOS **`startKoin`**, and **`iosMain` / `actual`** bindings for HTTP, persistence, and other platform edges — added **per module** as integration proceeds.

### Phase G — iOS

1. Add Apple targets to relevant shared modules (when not already present); prefer **incremental** rollout (`:shared:presentation` → **`:shared:api`** → **`:shared:data`** → **`:shared:database`**, etc.) with **CI** covering iOS compilation.
2. SwiftUI screens consume the **same `ScreenModel` + `UiState`**; platform code provides **`expect`/`actual`** capabilities (e.g. local image capture per project guide).
3. **Koin on iOS:** from the iOS Kotlin entry (or equivalent), call **`startKoin { modules(sharedKoinArchiModules + iosDatabaseModule + …) }`** — reuse **`sharedKoinArchiModules`** from **`:shared:di`**; add **`iosDatabaseModule`** (and other **`actual`** bindings) for Room / paths / HTTP engines that differ from Android.

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
| **iOS** | **Phase G** — deferred; structure stays **easy to add** |
| **Use cases** | **Optional** — no layer for one-liners; add only when reuse, policy, or real orchestration justify it (**§7 Phase C**, **§8**) |
| **Phase D — data layer (Android)** | **Done** — **`:shared:api`**, **`:shared:data`**, **`:shared:database`**, **`:shared:tracking`** (**§7 Phase D**) |
| **Phase E — DI (Android)** | **Done** — **`:shared:di`** + **`app`** bootstrap (**§7 Phase E**) |
| **Phase F — presentation + Android app shape** | **In progress** — feature **`ScreenModel`**s done for **Search Friend**, **Dashboard**, **My Friends**, **My Profile**, **Post Transaction**, **Billing**; **last step:** one Android deployable module (layout **TBD**, **not** a gate for Phase G) |
| **Phase G — iOS** | **Next / parallel** — Apple targets + **`iosMain`** on shared modules incrementally; SwiftUI, **`startKoin`** on iOS (**§7 Phase G**); Android Compose in **`feature:*`** continues in parallel |
