# Claude Code Harness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create 4 scoped `CLAUDE.md` files that replace the Cursor harness (`.cursor/rules/` + `.cursor/skills/`), folding all checklists into the nearest relevant file and dropping templated skill walkthroughs.

**Architecture:** Root `CLAUDE.md` is always loaded (project overview, architecture, DI, KMP status). Three subdirectory `CLAUDE.md` files scope detail to where it's needed: `feature/` for Compose/ViewModel/checklists, `shared/` for KMP/ScreenModel/data layer, `iosApp/` for SwiftUI/SKIE patterns. No `.claude/skills/` directory — Claude Code reads existing implementations directly.

**Tech Stack:** Markdown only. No code changes. No commits (user commits manually).

**Source files to read for reference:**
- `.cursor/rules/pokemaniac-guide.mdc`
- `.cursor/rules/pokemaniac-architecture.mdc`
- `.cursor/rules/viewmodel-patterns.mdc`
- `.cursor/rules/compose-patterns.mdc`
- `.cursor/rules/kotlin-android-standards.mdc`
- `.cursor/rules/development-checklists.mdc`
- `docs/kmp-migration-plan.md`
- `iosApp/PokeManiac/KotlinViewAdapter.swift`
- `iosApp/PokeManiac/SkieFlowCollect.swift`
- `iosApp/PokeManiac/WelcomeScreen.swift`
- `iosApp/PokeManiac/SearchFriendScreen.swift`

---

## File Map

| File | Created/Modified | Purpose |
|---|---|---|
| `CLAUDE.md` | Create | Always-loaded project overview |
| `feature/CLAUDE.md` | Create | Compose, ViewModel, feature checklists |
| `shared/CLAUDE.md` | Create | KMP, ScreenModel, data layer checklist |
| `iosApp/CLAUDE.md` | Create | SwiftUI, SKIE, iOS patterns |

---

## Task 1: Root CLAUDE.md

**Files:**
- Create: `CLAUDE.md`

- [ ] **Step 1: Write `CLAUDE.md`**

```markdown
# PokeManiac — Claude Code Guide

## Engineering Philosophy

Two principles override every other rule in this codebase.

### 1. Pragmatism over perfection

Apply a pattern **only when it solves a real, visible problem**. Every abstraction has a complexity cost that must be justified by a concrete benefit.

- If an optimization makes the code harder to read for no visible gain → don't apply it
- If a pattern adds 3 layers of indirection to a 10-line screen → don't apply it
- When in doubt between two solutions, prefer the simpler one

### 2. Code must be readable by a senior Android developer who doesn't know this codebase

They know Kotlin, Jetpack Compose, coroutines, MVVM — but not our project-specific patterns. Avoid clever tricks that require deep context to understand. Complexity that would prompt a code review question is a signal to simplify.

---

## Quick Navigation

| I'm doing… | Read… |
|---|---|
| Adding a new feature module | `feature/CLAUDE.md` → Adding a New Feature checklist |
| Modifying the data layer | `shared/CLAUDE.md` → Adding API Endpoint checklist |
| Writing tests | `feature/CLAUDE.md` → Adding Unit Tests checklist |
| Working on UI / Compose | `feature/CLAUDE.md` |
| Working on shared KMP modules | `shared/CLAUDE.md` |
| Working on the iOS app | `iosApp/CLAUDE.md` |

---

## Module Architecture

```
Presentation Layer
├── app                    Main entry point; wires all feature Koin modules
├── feature:welcome
├── feature:dashboard
├── feature:searchfriend
├── feature:myfriends
├── feature:myprofile
├── feature:posttransaction
├── feature:billing
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

## Naming Conventions

| Type | Pattern | Example |
|---|---|---|
| AndroidX ViewModel | `*ViewModel` | `SearchFriendViewModel` |
| Shared ScreenModel | `*ScreenModel` | `SearchFriendScreenModel` |
| ScreenModel factory | `*ScreenModelFactory` (fun interface) | `SearchFriendScreenModelFactory` |
| UI State (sealed class) | `*UiState` | `SearchFriendUiState` |
| UI Model (presentation data) | `*UiModel` (shared) / legacy `*UI` / `*Ui` (feature) | `SearchFriendUiModel` |
| Repository interface | `*Repository` | `FriendsRepository` |
| Repository implementation | `*RepositoryImpl` | `FriendsRepositoryImpl` |
| Screen composable (container) | `*Screen` | `SearchFriendScreen` |
| View composable (stateless) | `*View` / `*Content` | `SearchFriendView` |
| Activity | `*Activity` | `SearchFriendActivity` |
| Navigator interface | `*Navigator` | `SearchFriendNavigator` |
| Navigator implementation | `*NavigatorImpl` | `SearchFriendNavigatorImpl` |
| Koin module (feature) | `*Module` (camelCase val) | `searchFriendModule` |
| Request interface | `*Request` | `FriendsRequest` |
| DataStore interface | `*DataStore` | `FriendsDataStore` |
| Room entity | `*Base` | `FriendBase` |
| DAO | `*Dao` | `FriendsDao` |

---

## State Management Pattern

Every screen follows this flow:

```
User Action → ViewModel.onAction() → Repository.operation()
    → _uiState.update { newState }   ← single source of truth
    → Screen collects uiState
    → View renders
```

UI State rules:
- **Sealed class** — never boolean flags (`isLoading`, `hasError`)
- **Immutable** — `data class` with `val`, `PersistentList` for all collection fields
- **Complete** — all data needed to render is in the state; nothing derived elsewhere
- **Single source** — one `StateFlow<*UiState>` per screen

---

## Screen / View Composition

```
Activity
  └── Screen (container composable)
        ├── collectAsStateWithLifecycle()    ← StateFlow → State
        ├── observeWithLifecycle()           ← SharedFlow errors → snackbar
        ├── OnLifecycleEventEffect(ON_START) ← trigger viewModel.start()
        └── delegates rendering to →
              View (stateless composable)
              ├── pure UI — no side effects, no state collection
              └── all data and callbacks via parameters
```

---

## Koin DI Wiring

**`shared:di` — `commonMain`** (`SharedKoinArchiModules.kt`):
```kotlin
val apiModule = module {
    factory<MyRequest> { MyRequestImpl(get()) }
}
val dataModule = module {
    factory<MyRepository> { MyRepositoryImpl(get(), get()) }
}
fun sharedKoinArchiModules() = listOf(apiModule, dataModule)
```

**`app/di/DatabaseModule.kt`** (Android edge):
```kotlin
val databaseModule = module {
    single { Room.databaseBuilder(androidApplication(), PokeManiacDatabase::class.java, "db").build() }
    single<MyDataStore> { MyDataStoreImpl(get<PokeManiacDatabase>().myDao()) }
}
fun appCoreArchiModules() = listOf(databaseModule) + sharedKoinArchiModules()
```

**Feature `di/*Module.kt`** (ScreenModel factory pattern — see `feature/CLAUDE.md`):
```kotlin
val myFeatureModule = module {
    factory<MyFeatureScreenModelFactory> {
        MyFeatureScreenModelFactory { scope -> MyFeatureScreenModel(get(), scope) }
    }
    viewModelOf(::MyFeatureViewModel)
    single<MyFeatureNavigator> { MyFeatureNavigatorImpl(get()) }
}
```

---

## Key Libraries

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material3 |
| State | Coroutines + StateFlow / SharedFlow |
| ViewModel | `androidx.lifecycle` |
| DI | Koin 3 (`koin-core` in commonMain, `koin-android` in app) |
| Navigation | Compose Navigation (type-safe `@Serializable` routes) |
| Network | Ktor (shared) |
| Serialization | Moshi + kotlinx-serialization |
| Persistence | Room (KMP, commonMain + androidTarget) |
| Image Loading | Coil |
| iOS interop | SKIE |
| Testing | JUnit, Mockito, Turbine |

---

## Tracking & Analytics

```kotlin
class MyViewModel(private val tracking: TrackingRepository) : ViewModel() {
    fun onItemClicked(id: String) {
        tracking.logEvent("item_clicked", mapOf("id" to id))
    }
}
```

Import `TrackingRepository` from `:shared:domain` only — never from `:shared:tracking` implementation types.

---

## i18n

Strings in `res/values/strings.xml` (English default) and `res/values-fr/strings.xml` (French).
Access in Compose: `stringResource(R.string.my_key)`.

## Dark Mode

Automatic via Material3. Always wrap screens in `PokeManiacTheme { }`.

---

## KMP Migration Status

- ✅ **Phase A–C** — `:shared:domain` is KMP `commonMain` + Apple targets
- ✅ **Phase D** — `:shared:api`, `:shared:data`, `:shared:database`, `:shared:tracking` (commonMain + androidTarget)
- ✅ **Phase E** — `:shared:di` commonMain Koin + `app` bootstrap
- ✅ **Phase F** — all features have shared `ScreenModel` in `:shared:presentation`
- ⏳ **Phase G** — iOS SwiftUI app, Apple targets on remaining shared modules (in progress)

Authoritative plan: `docs/kmp-migration-plan.md`

---

## Troubleshooting

**ViewModel not receiving data**
1. Check `OnLifecycleEventEffect(ON_START)` is present in Screen
2. Verify Flow is emitting in repository
3. Confirm `collectAsStateWithLifecycle()` is used (not `collectAsState()`)

**Koin injection fails**
1. Check module is registered in `PokeManiacApplication.kt`
2. Verify interface/implementation are declared in the right module
3. Check constructor parameter types match Koin bindings

**Compose preview not showing**
1. `@Preview` annotation present before `@Composable`
2. Wrapped in `PokeManiacTheme { }`
3. Imports are correct

**Gradle module not found**
1. Module included in `settings.gradle.kts`
2. `implementation(project(":feature:myfeature"))` in `app/build.gradle.kts`
3. Run Gradle sync

---

## ✅ Checklist: Before Pushing to Remote

### Build
- [ ] `./gradlew testDebugUnitTest` passes
- [ ] `./gradlew assembleRelease` passes
- [ ] No compilation errors or unresolved lint warnings

### Code Quality
- [ ] No hardcoded strings (use `stringResource`)
- [ ] No debug code (`Log.d`, `println`)
- [ ] No TODOs or FIXMEs (or tracked as issues)
- [ ] No dead code or unused imports

### Git
- [ ] Commits are atomic and descriptive
- [ ] No credentials or sensitive files committed
- [ ] Branch is up-to-date with main

---

## ✅ Checklist: After Feature Completion

- [ ] Removed debug code and unused imports
- [ ] `docs/kmp-migration-plan.md` updated if architecture decisions were made
- [ ] README updated if new setup steps are required
- [ ] Feature is reachable from other features via its navigator
- [ ] Performance impact documented if non-trivial
```

- [ ] **Step 2: Verify**

Confirm `CLAUDE.md` exists at the project root and contains these sections:
- `## Engineering Philosophy`
- `## Module Architecture`
- `## Golden Rule: Dependency Direction`
- `## Naming Conventions`
- `## Koin DI Wiring`
- `## KMP Migration Status`
- `## ✅ Checklist: Before Pushing to Remote`
- `## ✅ Checklist: After Feature Completion`

---

## Task 2: feature/CLAUDE.md

**Files:**
- Create: `feature/CLAUDE.md`

- [ ] **Step 1: Write `feature/CLAUDE.md`**

```markdown
# PokeManiac — Feature Module Guide

> Loaded automatically when working in `feature/*` or `coreui`. See root `CLAUDE.md` for project-wide architecture rules.

---

## Screen vs View Pattern

### Screen (container composable) — `*Screen.kt`

Responsibilities: collect state, observe lifecycle, handle errors, inject ViewModel.

```kotlin
@Composable
fun MyFeatureScreen(
    modifier: Modifier = Modifier,
    viewModel: MyFeatureViewModel = koinViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Error flow — lifecycle-aware, pauses in background
    viewModel.error.observeWithLifecycle(lifecycleOwner) { error ->
        snackbarHostState.showSnackbar(error.message)
    }

    // State — lifecycle-aware, pauses in background
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Reload on ON_START (use ON_RESUME if data can change from another Activity, e.g. camera flow)
    OnLifecycleEventEffect(Lifecycle.Event.ON_START, lifecycleOwner) { viewModel.start() }

    MyFeatureView(
        modifier = modifier,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction
    )
}
```

### View (stateless composable) — `ui/*View.kt`

Responsibilities: pure UI rendering only. No state collection, no side effects.

```kotlin
@Composable
fun MyFeatureView(
    modifier: Modifier = Modifier,
    uiState: MyFeatureUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (action: UserAction) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState) {
                MyFeatureUiState.Loading -> GenericLoader()
                is MyFeatureUiState.Data -> MyFeatureContent(uiState.items, onAction)
                MyFeatureUiState.EmptyResult -> GenericEmptyScreen()
            }
        }
    }
}
```

### Feature file structure

```
feature/myfeature/src/main/java/com/shodo/android/myfeature/
├── MyFeatureActivity.kt          Entry point — setContent { PokeManiacTheme { MyFeatureScreen() } }
├── MyFeatureScreen.kt            Container composable
├── MyFeatureViewModel.kt         Thin AndroidX ViewModel; delegates to ScreenModel
├── di/
│   └── MyFeatureModule.kt        Koin bindings
├── navigator/
│   └── MyFeatureNavigatorImpl.kt Cross-feature navigation
├── ui/
│   ├── MyFeatureView.kt          Stateless view
│   ├── MyFeatureContent.kt       Content sections
│   └── MyFeaturePreview.kt       @Preview composables for all UiState branches
└── uimodel/
    └── MyFeatureUI.kt            Android-side UI data models + domain→UI mappers
```

---

## ViewModel Patterns

### State and error flows

```kotlin
@Immutable   // Always annotate sealed UiState — Compose cannot infer stability
sealed class MyFeatureUiState {
    data object Loading : MyFeatureUiState()
    data class Data(val items: PersistentList<ItemUI>) : MyFeatureUiState()
    data object EmptyResult : MyFeatureUiState()
}

class MyFeatureViewModel(
    private val screenModelFactory: MyFeatureScreenModelFactory
) : ViewModel() {
    private val screenModel by lazy { screenModelFactory.create(viewModelScope) }

    val uiState get() = screenModel.uiState   // StateFlow<MyFeatureUiState>
    val error get() = screenModel.error       // SharedFlow<UiError>

    fun start() = screenModel.start()
    fun onAction(action: UserAction) = screenModel.onAction(action)
}
```

### ScreenModel factory (Koin wiring)

```kotlin
// fun interface in di/MyFeatureModule.kt
fun interface MyFeatureScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): MyFeatureScreenModel
}

// Koin factory — closes over get() for repositories; scope applied at create(...)
val myFeatureModule = module {
    factory<MyFeatureScreenModelFactory> {
        MyFeatureScreenModelFactory { scope ->
            MyFeatureScreenModel(repository = get(), coroutineScope = scope)
        }
    }
    viewModelOf(::MyFeatureViewModel)
    single<MyFeatureNavigator> { MyFeatureNavigatorImpl(get()) }
}
```

Reference implementation: `SearchFriendScreenModelFactory` + `SearchFriendModule`.

### UI models — Android-side annotations

UI model `data class` types used directly by `@Composable` functions must be annotated so Compose can skip recomposition:

```kotlin
@Immutable  // all val fields are primitives, String, enum, or PersistentList
data class ItemUI(val id: String, val name: String, val count: Int)

@Stable     // when a field has correct equals() but is not @Immutable (e.g. Uri)
data class CardUI(val id: String, val imageUri: Uri)
```

**Do NOT add `@Immutable`/`@Stable` in `:shared:presentation` `commonMain`** — those types are plain Kotlin; annotations stay on the Android side.

### Error flow — always use `UiError`

```kotlin
// SharedFlow<UiError>, not SharedFlow<Exception>
private val _error = MutableSharedFlow<UiError>()
val error = _error.asSharedFlow()

// In catch blocks:
catch (e: CancellationException) { throw e }   // always rethrow
catch (e: Exception) { _error.emit(UiError.from(e)) }
```

`UiError.from(e)` returns `UiError(message = e.message ?: "Something went wrong")` — never null. In Screen: `snackbarHostState.showSnackbar(error.message)`, not `error.toString()`.

### Lifecycle helper

Use `OnLifecycleEventEffect` from `coreui.extensions` — not raw `DisposableEffect + LifecycleEventObserver`:

```kotlin
// ON_START: most screens (reload when first shown)
OnLifecycleEventEffect(Lifecycle.Event.ON_START, lifecycleOwner) { viewModel.start() }

// ON_RESUME: when data can change from another Activity (e.g. camera → post transaction)
OnLifecycleEventEffect(Lifecycle.Event.ON_RESUME, lifecycleOwner) { viewModel.start() }
```

---

## Coroutine Rules

### Always rethrow CancellationException

```kotlin
viewModelScope.launch {
    try {
        val data = repository.fetchData()
        _uiState.update { Data(data) }
    } catch (e: CancellationException) {
        throw e   // never swallow — breaks structured concurrency
    } catch (e: Exception) {
        _error.emit(UiError.from(e))
    }
}
```

### Dispatcher rules

| Dispatcher | Use for |
|---|---|
| `Dispatchers.Main` (default for `viewModelScope`) | `_uiState.update {}`, navigation calls |
| `Dispatchers.IO` | network calls, DB reads/writes, file I/O |
| `Dispatchers.Default` | CPU-bound work: sorting, mapping large lists |

```kotlin
viewModelScope.launch {
    _uiState.update { Loading }                              // Main ✅
    val raw = withContext(Dispatchers.IO) { repo.fetch() }   // IO ✅
    val ui = withContext(Dispatchers.Default) {              // Default ✅
        raw.map { it.mapToUI() }.toPersistentList()
    }
    _uiState.update { Data(ui) }                            // Main ✅
}
```

### Cancel overlapping jobs

```kotlin
private var loadJob: Job? = null

fun start() {
    loadJob?.cancel()
    loadJob = viewModelScope.launch { ... }
}
```

---

## Recomposition Optimization

### Tier 1 — Always apply (negligible complexity cost)

- `@Immutable` on all `sealed class UiState` and UI model `data class`
- `PersistentList<>` for all collection fields (never plain `List<>`)
- `observeWithLifecycle()` for all `SharedFlow` (correctness + lifecycle fix)
- `key = { it.id }` on all lazy `items { }` blocks
- String `key` on all single `item { }` blocks: `item(key = "header") { ... }`
- `remember(param) { }` for any value derived inline from a parameter (`sumOf`, `filter`, `count`)

### Tier 2 — Only on screens with LazyList / LazyGrid / animations

Tier 2 screens in this project: **Dashboard, SearchFriend, MyFriendList, MyFriendDetail, MyProfile**.

- Navigation lambdas capturing `context`/`viewModel`: `remember(viewModel, context) { ... }`
- Leaf composables (cards, list items) receive only the fields they actually display — not the full model object
- Per-item lambdas inside lazy item lambdas: `remember(item.id, callback) { { callback(item.id) } }`

```kotlin
// ✅ Tier 2 — leaf composable receives only used fields
@Composable
fun MyFriendCard(
    id: String,
    name: String,
    imageUrl: String,
    onFriendPressed: () -> Unit
) { ... }

// At the call site inside LazyColumn:
items(items = friends, key = { it.id }) { friend ->
    val onPressed = remember(friend.id, onFriendClicked) { { onFriendClicked(friend.id) } }
    MyFriendCard(id = friend.id, name = friend.name, imageUrl = friend.imageUrl, onFriendPressed = onPressed)
}
```

### Never nest a lazy layout inside a scrollable container

```kotlin
// ❌ BAD — LazyVerticalGrid has infinite height
Column(modifier = Modifier.verticalScroll(...)) {
    LazyVerticalGrid(...) { ... }
}

// ✅ GOOD — header as full-span item inside the grid
LazyVerticalGrid(columns = GridCells.Adaptive(minSize = cellSize)) {
    item(key = "header", span = { GridItemSpan(maxLineSpan) }) { HeaderSection() }
    items(cards, key = { it.id }) { card -> CardItem(card) }
}
```

---

## State Collection

```kotlin
// ✅ StateFlow — always collectAsStateWithLifecycle (pauses in background)
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// ✅ SharedFlow — always observeWithLifecycle (lifecycle-aware, from coreui)
viewModel.error.observeWithLifecycle(lifecycleOwner) { error ->
    snackbarHostState.showSnackbar(error.message)
}

// ✅ rememberUpdatedState for callbacks in LaunchedEffect(Unit)
val currentOnBack by rememberUpdatedState(onBack)
LaunchedEffect(Unit) {
    viewModel.navigateBack.collectLatest { currentOnBack() }
}
```

---

## Type-Safe Navigation

Routes carry **IDs only** — never display data (`name`, `imageUrl`, etc.):

```kotlin
// ✅ GOOD — only the ID travels via navigation
@Serializable
data class DetailRoute(val id: String)

// ❌ BAD — name duplicates data in UiState, two sources of truth
@Serializable
data class DetailRoute(val id: String, val name: String)
```

While loading, the title is empty — acceptable. The View derives everything from `uiState`.

---

## Previews

Include one `@Preview` per `UiState` branch, always wrapped in `PokeManiacTheme`:

```kotlin
@Preview(name = "Loading")
@Composable
fun MyFeatureViewLoadingPreview() {
    PokeManiacTheme { MyFeatureView(uiState = MyFeatureUiState.Loading, snackbarHostState = remember { SnackbarHostState() }) }
}

@Preview(name = "Data")
@Composable
fun MyFeatureViewDataPreview() {
    PokeManiacTheme {
        MyFeatureView(
            uiState = MyFeatureUiState.Data(items = persistentListOf(ItemUI("1", "Item 1"))),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(name = "Empty")
@Composable
fun MyFeatureViewEmptyPreview() {
    PokeManiacTheme { MyFeatureView(uiState = MyFeatureUiState.EmptyResult, snackbarHostState = remember { SnackbarHostState() }) }
}
```

---

## ✅ Checklist: Adding a New Feature Module

### Setup
- [ ] Feature name decided (snake_case: `mynewfeature`)
- [ ] Created `feature/mynewfeature/` with subdirs: `ui/`, `uimodel/`, `di/`, `navigator/`
- [ ] Created `src/test/java/` for tests

### Core files
- [ ] `MyNewFeatureActivity.kt` — `setContent { PokeManiacTheme { MyNewFeatureScreen() } }`
- [ ] `MyNewFeatureScreen.kt` — `collectAsStateWithLifecycle`, `observeWithLifecycle`, `OnLifecycleEventEffect`
- [ ] `MyNewFeatureViewModel.kt` — thin wrapper; `lazy { factory.create(viewModelScope) }`
- [ ] `ui/MyNewFeatureView.kt` — stateless; `when (uiState) { ... }`
- [ ] `ui/MyNewFeaturePreview.kt` — one preview per UiState branch
- [ ] `uimodel/MyNewFeatureUI.kt` — `@Immutable data class` + domain→UI extension functions

### ScreenModel (in `:shared:presentation`)
- [ ] `MyNewFeatureUiState` sealed class (plain Kotlin, no `@Immutable` in commonMain)
- [ ] `MyNewFeatureScreenModel` with `StateFlow<MyNewFeatureUiState>` + `SharedFlow<UiError>`
- [ ] `fun interface MyNewFeatureScreenModelFactory`

### Navigation
- [ ] Navigator interface created in `coreui/navigator/`
- [ ] `MyNewFeatureNavigatorImpl.kt` in feature module

### DI
- [ ] `di/MyNewFeatureModule.kt` — `factory<MyNewFeatureScreenModelFactory>`, `viewModelOf(::MyNewFeatureViewModel)`, `single<Navigator>`
- [ ] Module added to `PokeManiacApplication.kt` `startKoin { modules(..., myNewFeatureModule) }`

### Gradle
- [ ] `feature/mynewfeature/build.gradle.kts` — deps: `:shared:domain`, `:shared:presentation`, `coreui`, `:shared:tracking`, Compose, Koin
- [ ] Added to `settings.gradle.kts`: `include(":feature:mynewfeature")`
- [ ] Added to `app/build.gradle.kts`: `implementation(project(":feature:mynewfeature"))`

### Imports verification
- [ ] ❌ NO imports from `:shared:data`, `:shared:api`, `:shared:database`
- [ ] ✅ Only `domain`, `coreui`, `presentation`, `tracking` (interface only)

### Tests
- [ ] `MyNewFeatureViewModelTest.kt` — at least one happy path + one error case

---

## ✅ Checklist: Adding Unit Tests

- [ ] Test file: `feature/myfeature/src/test/java/.../MyFeatureViewModelTest.kt`
- [ ] `Dispatchers.setMain(UnconfinedTestDispatcher())` in `@Before`
- [ ] `Dispatchers.resetMain()` in `@After`
- [ ] Mock all dependencies with `@Mock` + `MockitoAnnotations.openMocks(this)`
- [ ] Build ScreenModelFactory manually (no Koin): `MyFeatureScreenModelFactory { scope -> MyFeatureScreenModel(mockRepo, scope) }`
- [ ] Happy path test: Loading → Data state transition
- [ ] Empty state test: Loading → EmptyResult
- [ ] Error test: subscribe to `error` **inside** `error.test { viewModel.start(); ... }` — SharedFlow replay=0, events lost if you subscribe after emit
- [ ] Use **Turbine** for all Flow assertions: `viewModel.uiState.test { awaitItem() }`
- [ ] Use `persistentListOf()` in fixtures, not `listOf()`, for `PersistentList<>` fields
- [ ] For Android types in JVM tests (`Uri`): `Mockito.mock(Uri::class.java)` — never `Uri.parse(...)` (crashes JVM)
- [ ] Assert `error.message` for `UiError`, not the exception type
- [ ] Follow **Given / When / Then** comment structure in every test method

```kotlin
@Test
fun `start emits Loading then Data`() = runTest {
    // Given
    `when`(mockRepo.getAll()).thenReturn(flow { emit(listOf(mockItem)) })

    viewModel.uiState.test {
        // When
        viewModel.start()

        // Then
        assertEquals(MyFeatureUiState.Loading, awaitItem())
        assertTrue(awaitItem() is MyFeatureUiState.Data)
    }
}
```

---

## ✅ Checklist: Architecture Compliance

- [ ] Features import from `domain`, `coreui`, `presentation`, `tracking` only
- [ ] ❌ No imports from `:shared:data`, `:shared:api`, `:shared:database`
- [ ] UI state is a sealed class — no boolean flags
- [ ] `StateFlow` private mutable, public immutable
- [ ] `SharedFlow` for one-shot events (errors, navigation)
- [ ] Screen: `collectAsStateWithLifecycle`, `observeWithLifecycle`, `OnLifecycleEventEffect`
- [ ] View: no state collection, no side effects, pure UI
- [ ] `@Immutable` on `sealed class UiState` and UI model `data class` (Android side)
- [ ] `PersistentList<>` for all collection fields in UI models
- [ ] All `SharedFlow` collected via `observeWithLifecycle()` from coreui

---

## ✅ Checklist: Code Review

- [ ] No circular dependencies between modules
- [ ] Dependency direction respected: Presentation → Domain → Data
- [ ] No direct imports between feature modules (only via navigator interfaces)
- [ ] `UiError` used (not raw `Exception`); `error.message` in snackbar (not `.toString()`)
- [ ] `OnLifecycleEventEffect` used (not raw `DisposableEffect + LifecycleEventObserver`)
- [ ] `CancellationException` always rethrown in catch blocks
- [ ] Recomposition Tier 1 applied everywhere
- [ ] Recomposition Tier 2 applied on Dashboard, SearchFriend, MyFriendList, MyFriendDetail, MyProfile
- [ ] No lazy layout nested inside a scrollable container
- [ ] Navigation routes carry IDs only — no display data
- [ ] Preview composables for all UiState branches
- [ ] Tests follow Given / When / Then; SharedFlow subscribed before trigger
```

- [ ] **Step 2: Verify**

Confirm `feature/CLAUDE.md` exists and contains these sections:
- `## Screen vs View Pattern`
- `## ViewModel Patterns`
- `## Coroutine Rules`
- `## Recomposition Optimization`
- `## ✅ Checklist: Adding a New Feature Module`
- `## ✅ Checklist: Adding Unit Tests`
- `## ✅ Checklist: Architecture Compliance`
- `## ✅ Checklist: Code Review`

---

## Task 3: shared/CLAUDE.md

**Files:**
- Create: `shared/CLAUDE.md`

- [ ] **Step 1: Write `shared/CLAUDE.md`**

```markdown
# PokeManiac — Shared KMP Modules Guide

> Loaded automatically when working in `shared/*`. See root `CLAUDE.md` for project-wide rules.

---

## Module Roles

| Module | Layer | Responsibility |
|---|---|---|
| `:shared:domain` | Domain | Repository interfaces, entities, domain errors. `commonMain` + Apple targets. |
| `:shared:presentation` | Presentation | `*ScreenModel`, `*UiState`, `*UiModel`. `commonMain` only. |
| `:shared:data` | Data | Repository implementations, DataStore **interfaces**. `commonMain` + `androidTarget`. |
| `:shared:api` | Data | Ktor clients, DTOs, `*RequestImpl`. `commonMain` + `androidTarget`. |
| `:shared:database` | Data | Room entities, DAOs, DataStore **implementations**. `commonMain` + `androidTarget`. |
| `:shared:tracking` | Cross-cutting | `TrackingRepository` impl + Koin `trackingModule`. |
| `:shared:di` | DI | Koin `apiModule` + `dataModule` in `commonMain`. No `Context`. |

---

## Type Pipeline: DTO → Domain → UiModel

| Layer | Type | Rule |
|---|---|---|
| Data / API | `*Dto`, Room entity | Storage / transport shape only; never cross repository boundary |
| Repository impl | (internal) | Maps Dto → domain; repository **interface** exposes domain types only |
| Domain | `*Model` or named entity (`User`, `NewActivity`) | Business meaning; shared `commonMain` |
| Presentation | `*UiModel` | What the screen needs; built from domain in `ScreenModel` |
| Presentation | `*UiState` (sealed) | `Loading`, `Empty`, `Error`, `Data(items: List<*UiModel>)` |

DTOs **never** cross the repository interface boundary.

---

## ScreenModel vs ViewModel

| | `*ScreenModel` | `*ViewModel` (Android) |
|---|---|---|
| Location | `:shared:presentation` `commonMain` | `feature:*` Android module |
| Extends | Nothing (plain Kotlin) | `androidx.lifecycle.ViewModel` |
| Scope | `CoroutineScope` passed in | `viewModelScope` |
| Responsibility | State + flows + user actions | Thin shell: supplies scope, forwards calls |
| iOS use | Direct (with iOS-provided scope) | Not used on iOS |

### Standard wiring (ScreenModelFactory pattern)

Reference implementation: `SearchFriendScreenModelFactory` + `SearchFriendViewModel`.

**1. `fun interface` factory in `feature/.../di/`:**
```kotlin
fun interface MyFeatureScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): MyFeatureScreenModel
}
```

**2. Koin `factory` in `di/MyFeatureModule.kt`:**
```kotlin
factory<MyFeatureScreenModelFactory> {
    MyFeatureScreenModelFactory { scope ->
        MyFeatureScreenModel(repository = get(), coroutineScope = scope)
    }
}
```

**3. Thin Android `ViewModel`:**
```kotlin
class MyFeatureViewModel(
    private val factory: MyFeatureScreenModelFactory
) : ViewModel() {
    private val screenModel by lazy { factory.create(viewModelScope) }
    val uiState get() = screenModel.uiState
    val error get() = screenModel.error
    fun start() = screenModel.start()
}
```

**4. Unit tests — build factory manually, no Koin:**
```kotlin
val factory = MyFeatureScreenModelFactory { scope -> MyFeatureScreenModel(mockRepository, scope) }
val viewModel = MyFeatureViewModel(factory)
```

**5. iOS** — construct `ScreenModel` directly with a scope from `createPresentationCoroutineScope()` via `KotlinViewAdapter`. No `ViewModel`.

---

## UiState / UiModel in commonMain

During Phase F/G, `*UiState` and `*UiModel` live in `commonMain` as **plain Kotlin** — no `androidx.compose.runtime` dependency in `:shared:presentation`.

- ❌ Do **not** add `@Immutable` or `@Stable` in `commonMain`
- ✅ On Android (`feature:*`, `coreui`), keep `@Immutable`/`@Stable` on types that Composables consume directly

---

## KMP Dependency Rules

```
:shared:presentation  → :shared:domain  (only)
:shared:data          → :shared:domain  (implements interfaces)
:shared:api           → :shared:domain  (maps DTOs to domain)
:shared:database      → :shared:data    (implements DataStore interfaces)
:shared:di            → :shared:data, :shared:api  (wires implementations)
feature:*             → :shared:domain, :shared:presentation, coreui, :shared:tracking
```

**:shared:presentation never depends on :shared:data / :shared:api / :shared:database.**

---

## expect/actual: Platform-Specific Capabilities

Use `expect/actual` for capabilities, not for domain types. The `FileSource` entity stays unchanged; only the capture mechanism is platform-specific.

```kotlin
// In a shared KMP module
expect class LocalImageCapture {
    fun createImageFile(): String  // returns platform-specific localIdentifier
}

// androidMain — existing code, unchanged
actual class LocalImageCapture(private val context: Context) {
    actual fun createImageFile(): String {
        val file = File.createTempFile("PokeManiac_", ".jpg", context.filesDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file).toString()
    }
}

// iosMain — new implementation
actual class LocalImageCapture {
    actual fun createImageFile(): String {
        // PHPicker / UIImagePickerController flow
        return phAssetLocalIdentifier
    }
}
```

**Do NOT change the Android `actual` implementation** when adding the iOS `actual`. The domain entity `FileSource(localIdentifier)` and Room persistence stay unchanged.

---

## ✅ Checklist: Adding API Endpoint & Data Operations

### Domain layer (`shared/domain/src/commonMain/`)
- [ ] Repository interface: `domain/repositories/My*Repository.kt` — `Flow<>` for reads, `suspend fun` for writes
- [ ] Domain entity: `domain/entities/MyEntity.kt`

### Data layer (`shared/data/src/commonMain/`)
- [ ] DataStore interface: `data/myentity/MyDataStore.kt`
- [ ] Repository implementation: `data/myentity/MyRepositoryImpl.kt` — constructor-injects `MyRequest` + `MyDataStore`
- [ ] Maps DTOs → domain in repository impl; never expose DTOs via repository interface

### API layer (`shared/api/src/commonMain/` or `androidMain/`)
- [ ] DTO: `api/myentity/dto/MyEntityDto.kt` with `@Serializable`
- [ ] Request implementation: `api/myentity/MyRequestImpl.kt` — maps DTOs → domain entities

### Database layer (`shared/database/src/commonMain/`)
- [ ] Room entity: `database/myentity/MyEntityBase.kt` (`@Entity`, `@PrimaryKey`)
- [ ] DAO: `database/myentity/MyEntityDao.kt` (`Flow` for reads, `suspend` for writes)
- [ ] DataStore implementation: `database/myentity/MyDataStoreImpl.kt` — maps `MyEntityBase` ↔ domain
- [ ] DAO added to `@Database(entities = [...])` in `PokeManiacDatabase.kt`

### Koin registration
- [ ] `SharedKoinArchiModules.kt` (`shared/di`): `dataModule` + `apiModule` — add `factory<MyRepository>`, `factory<MyRequest>`
- [ ] `app/di/DatabaseModule.kt`: `databaseModule` — add `single<MyDataStore> { MyDataStoreImpl(get<PokeManiacDatabase>().myDao()) }`

### Gradle validation
- [ ] `./gradlew testDebugUnitTest` passes
- [ ] `./gradlew assembleRelease` passes

### Tests
- [ ] `MyRepositoryImplTest.kt` — mock `MyRequest` + `MyDataStore` with Mockito
- [ ] Test: `getAll()` returns flow from dataStore
- [ ] Test: `search()` combines local + remote
- [ ] Test: write operations delegate to both request and dataStore
- [ ] Follow **Given / When / Then** comments
```

- [ ] **Step 2: Verify**

Confirm `shared/CLAUDE.md` exists and contains:
- `## Module Roles`
- `## ScreenModel vs ViewModel`
- `## UiState / UiModel in commonMain`
- `## KMP Dependency Rules`
- `## ✅ Checklist: Adding API Endpoint & Data Operations`

---

## Task 4: iosApp/CLAUDE.md

**Files:**
- Create: `iosApp/CLAUDE.md`

- [ ] **Step 1: Write `iosApp/CLAUDE.md`**

```markdown
# PokeManiac — iOS App Guide

> Loaded automatically when working in `iosApp/`. See root `CLAUDE.md` for project-wide architecture. See `shared/CLAUDE.md` for ScreenModel/UiState patterns.

---

## Architecture

The iOS app consumes shared `ScreenModel` + `UiState` from `:shared:presentation` via SKIE (Kotlin/Swift interop). It mirrors the Android Screen/View split in SwiftUI.

```
SwiftUI Screen (struct)
  ├── @StateObject KotlinViewAdapter<ScreenModel, UiState>
  ├── .task { for await event in collectAsAsyncStream(adapter.viewModel.uiEvent) { ... } }
  └── delegates to →
        SwiftUI View (struct)
        ├── pure rendering — no state ownership
        └── all data via parameters
```

---

## KotlinViewAdapter

`KotlinViewAdapter` bridges a shared `ScreenModel` (with `StateFlow`) to SwiftUI's `ObservableObject` / `@Published`.

```swift
@StateObject private var adapter: KotlinViewAdapter<MyScreenModel, MyUiState>

init(...) {
    _adapter = StateObject(
        wrappedValue: KotlinViewAdapter(
            viewModel: { scope in
                // Simple ScreenModel: construct directly
                MyScreenModel(coroutineScope: scope)
                // ScreenModel needing Koin deps: use iOS composition helper
                // MyIosCompositionKt.createMyScreenModelForIos(coroutineScope: scope)
            },
            state: { $0.uiState }
        )
    )
}
```

`KotlinViewAdapter` creates a `CoroutineScope` via `createPresentationCoroutineScope()`, passes it to the ScreenModel, and cancels it on `deinit`. Do not manage scope manually.

---

## Collecting SharedFlow Events (SKIE)

Use `collectAsAsyncStream` from `SkieFlowCollect.swift` to consume `SharedFlow` in `.task`:

```swift
.task {
    for await event in collectAsAsyncStream(adapter.viewModel.uiEvent) {
        switch onEnum(of: event) {
        case .navigateToDashboard:
            onNavigateToDashboard()
        case .showMessage(let data):
            alertText = data.message.message
            showAlert = true
        }
    }
}
```

For error flows (`SharedFlow<UiError>`):
```swift
.task {
    for await err in collectAsAsyncStream(adapter.viewModel.error) {
        errorMessage = err.message
        showError = true
    }
}
```

`.task` is cancelled automatically when the view disappears — no manual cancellation needed.

---

## Screen Pattern (reference: WelcomeScreen, SearchFriendScreen)

```swift
struct MyScreen: View {
    @StateObject private var adapter: KotlinViewAdapter<MyScreenModel, MyUiState>
    @State private var errorMessage: String?
    @State private var showError = false

    let onBack: () -> Void

    init(onBack: @escaping () -> Void) {
        self.onBack = onBack
        _adapter = StateObject(
            wrappedValue: KotlinViewAdapter(
                viewModel: { scope in MyScreenModel(coroutineScope: scope) },
                state: { $0.uiState }
            )
        )
    }

    var body: some View {
        MyView(
            uiState: adapter.state,
            onAction: { adapter.viewModel.onAction() }
        )
        .task {
            for await err in collectAsAsyncStream(adapter.viewModel.error) {
                errorMessage = err.message
                showError = true
            }
        }
        .alert("Error", isPresented: $showError, presenting: errorMessage) { _ in
            Button("OK", role: .cancel) {}
        } message: { msg in Text(msg) }
    }
}
```

---

## View Pattern

SwiftUI Views are stateless structs — no `@StateObject`, no flow collection:

```swift
struct MyView: View {
    let uiState: MyUiState
    let onAction: () -> Void

    var body: some View {
        // render uiState
    }
}

#Preview {
    MyView(uiState: ..., onAction: {})
}
```

---

## ScreenModels Needing Koin Dependencies

For ScreenModels that require repositories injected via Koin (e.g. `SearchFriendScreenModel`), create an iOS composition helper in `:shared:presentation` `iosMain`:

```kotlin
// iosMain
fun createSearchFriendScreenModelForIos(coroutineScope: CoroutineScope): SearchFriendScreenModel {
    return SearchFriendScreenModel(
        repository = KoinComponent().get(),
        coroutineScope = coroutineScope
    )
}
```

Then call from Swift: `SearchFriendIosCompositionKt.createSearchFriendScreenModelForIos(coroutineScope: scope)`.

---

## Existing Reference Screens

| Screen | File | Pattern used |
|---|---|---|
| Welcome | `WelcomeScreen.swift` + `WelcomeView.swift` | KotlinViewAdapter + uiEvent SharedFlow |
| Search Friend | `SearchFriendScreen.swift` + `SearchFriendView.swift` | KotlinViewAdapter + iOS composition helper + error SharedFlow |
| Dashboard | `DashboardScreen.swift` + `DashboardView.swift` | KotlinViewAdapter + StateFlow only |
```

- [ ] **Step 2: Verify**

Confirm `iosApp/CLAUDE.md` exists and contains:
- `## KotlinViewAdapter`
- `## Collecting SharedFlow Events (SKIE)`
- `## Screen Pattern`
- `## View Pattern`

---

## Self-Review Notes

**Spec coverage check:**
- Root `CLAUDE.md` ✅ — engineering philosophy, module architecture, naming, DI, KMP status, checklists
- `feature/CLAUDE.md` ✅ — Screen/View, ViewModel, ScreenModelFactory, coroutines, recomposition, navigation, previews, 4 checklists
- `shared/CLAUDE.md` ✅ — module roles, type pipeline, ScreenModel vs ViewModel, commonMain rules, dependency rules, expect/actual, data layer checklist
- `iosApp/CLAUDE.md` ✅ — KotlinViewAdapter, SKIE flow collection, Screen/View pattern, iOS composition helper, reference screens

**Placeholder scan:** No TBDs, TODOs, or "similar to above" patterns. All code blocks are complete.

**Type consistency:** `MyFeatureScreenModelFactory`, `MyFeatureScreenModel`, `MyFeatureViewModel`, `MyFeatureUiState` naming used consistently across Tasks 2 and 3. `UiError` used consistently in Task 2.
