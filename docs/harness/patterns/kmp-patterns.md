# KMP Patterns

> Always-loaded rules are in `shared/CLAUDE.md`. This file has detailed patterns with code examples.
> Read it when working in any `shared/*` module.

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
