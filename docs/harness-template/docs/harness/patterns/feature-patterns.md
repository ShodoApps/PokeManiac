# Feature Patterns

> Always-loaded rules are in `feature/CLAUDE.md`. This file has the detailed patterns with full code examples.
> Read it when implementing or reviewing any feature module code.

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
├── MyFeatureActivity.kt          Entry point — setContent { [PROJECT_NAME]Theme { MyFeatureScreen() } }
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

Reference implementation: `MyFeatureScreenModelFactory` + `MyFeatureModule`.

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

Identify the screens with LazyList/LazyGrid or animations in your project and apply Tier 2 to each.

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

Include one `@Preview` per `UiState` branch, always wrapped in `[PROJECT_NAME]Theme`:

```kotlin
@Preview(name = "Loading")
@Composable
fun MyFeatureViewLoadingPreview() {
    [PROJECT_NAME]Theme { MyFeatureView(uiState = MyFeatureUiState.Loading, snackbarHostState = remember { SnackbarHostState() }) }
}

@Preview(name = "Data")
@Composable
fun MyFeatureViewDataPreview() {
    [PROJECT_NAME]Theme {
        MyFeatureView(
            uiState = MyFeatureUiState.Data(items = persistentListOf(ItemUI("1", "Item 1"))),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(name = "Empty")
@Composable
fun MyFeatureViewEmptyPreview() {
    [PROJECT_NAME]Theme { MyFeatureView(uiState = MyFeatureUiState.EmptyResult, snackbarHostState = remember { SnackbarHostState() }) }
}
```

---

## State Management Flow

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
- **Error events** — one-shot events (errors, navigation) are emitted on a separate `SharedFlow<UiError>`; never encoded as state fields

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
