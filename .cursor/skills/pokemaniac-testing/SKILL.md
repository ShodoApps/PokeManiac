---
name: pokemaniac-testing
description: Write tests for PokeManiac ViewModels, repositories, and composables. Covers unit tests with Mockito, Turbine flow testing, Koin test setup, Compose preview testing, and Given/When/Then comments in tests. Use when adding or refactoring tests (including shared ScreenModel + factory pattern).
---

# PokeManiac Testing Patterns

## Testing Setup

### Dependencies (add to the feature module's build.gradle.kts)

```gradle
// Testing
testImplementation(libs.kotlinx.coroutines.test)
testImplementation(libs.mockito.core)
testImplementation(libs.kotlin.test)
testImplementation(libs.turbine)
```

---

## ViewModel Testing

### Test Template

**File:** `feature/myfeature/src/test/java/.../MyFeatureViewModelTest.kt`

```kotlin
package com.shodo.android.myfeature

import app.cash.turbine.test
import com.shodo.android.domain.repositories.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class MyFeatureViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    @Mock private lateinit var myRepository: MyRepository

    private lateinit var viewModel: MyFeatureViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        viewModel = MyFeatureViewModel(myRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `start emits Loading then Data when repository returns items`() = runTest {
        // Given
        `when`(myRepository.getAll()).thenReturn(
            flow { emit(listOf(defaultItem)) }
        )

        viewModel.uiState.test {
            // When
            viewModel.start()

            // Then
            assertEquals(MyFeatureUiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is MyFeatureUiState.Data)
            assertEquals(1, state.items.size)
        }
    }

    @Test
    fun `start emits Loading then EmptyResult when repository returns empty list`() = runTest {
        // Given
        `when`(myRepository.getAll()).thenReturn(flow { emit(emptyList()) })

        viewModel.uiState.test {
            // When
            viewModel.start()

            // Then
            assertEquals(MyFeatureUiState.Loading, awaitItem())
            assertEquals(MyFeatureUiState.EmptyResult, awaitItem())
        }
    }

    @Test
    fun `start emits UiError when repository throws`() = runTest {
        // Given
        `when`(myRepository.getAll()).thenThrow(RuntimeException("Network error"))

        // When
        viewModel.start()

        // Then — error flow emits UiError (not raw Exception)
        viewModel.error.test {
            assertEquals("Network error", awaitItem().message)
        }
    }
}
```

### Key rules

- **Given / When / Then** — keep `// Given`, `// When`, `// Then` comments in each test method (and short `// Given — …` notes where context helps). **Do not drop them** when refactoring (e.g. moving logic to `:shared:presentation`, introducing `XxxScreenModelFactory`, switching errors to `PresentationError`).
- Use `UnconfinedTestDispatcher` — coroutines execute eagerly, `delay()` is skipped automatically
- Use `Dispatchers.setMain` / `resetMain` in `@Before` / `@After`
- Use `@Mock` + `MockitoAnnotations.openMocks(this)` for all dependencies
- Error flows: feature may expose `UiError` **or** shared `PresentationError` from `:shared:presentation` — assert on **`error.message`**, not the exception type
- Use **Turbine** (`viewModel.uiState.test {}`) for Flow assertions
- Use `skipItems(n)` to skip known intermediate states
- Use `persistentListOf()` (not `listOf()`) in fixtures for `PersistentList<>` fields

### Shared `ScreenModel` + `XxxScreenModelFactory` in tests

When the AndroidX `ViewModel` wraps a shared **`ScreenModel`**, tests still use **Given / When / Then**. Build the factory manually (no Koin):

```kotlin
val factory = MyFeatureScreenModelFactory { scope ->
    MyFeatureScreenModel(mockRepository, scope)
}
viewModel = MyFeatureViewModel(factory, /* other Android deps */)
```

See **`SearchFriendViewModelTest`**, **`DashboardViewModelTest`**, **`MyFriendListViewModelTest`**, **`MyProfileViewModelTest`**.

### Critical: subscribe to SharedFlow BEFORE triggering the action

`error` is a `SharedFlow` with `replay = 0`. If the action is triggered BEFORE subscribing, the event is lost.

**Always put the trigger INSIDE the `.test {}` block:**

```kotlin
// ✅ Correct — subscription is active when emit happens
viewModel.error.test {
    viewModel.start()
    assertEquals("Network error", awaitItem().message)
}

// ❌ Wrong — error already emitted before test{} subscribes
viewModel.start()
viewModel.error.test {
    awaitItem() // TurbineTimeoutCancellationException!
}
```

### StateFlow emits its current value on subscription

When testing `uiState` (a `StateFlow`), Turbine immediately receives the current value. Always consume the initial state with `awaitItem()` before asserting no further changes:

```kotlin
viewModel.uiState.test {
    assertTrue(awaitItem() is MyUiState.EmptySearch) // initial value
    viewModel.someNoOpAction()
    ensureAllEventsConsumed() // now safe
}
```

### Android types in JVM tests

`android.net.Uri` (and other Android framework classes) crash in plain JVM tests.

**Never use `Uri.parse(...)` in companion object or field initializers** — use `Mockito.mock(Uri::class.java)` instead:

```kotlin
// ✅ Safe — Mockito creates a proxy without calling any Android code
private val mockUri: Uri = mock(Uri::class.java)

// ❌ Crashes in JVM tests
private val uri = Uri.parse("content://media/photo.jpg")
```

---

## Repository Testing

### Repository Test Template

**File:** `data/src/test/java/.../MyRepositoryImplTest.kt`

```kotlin
package com.shodo.android.data.myentity

import app.cash.turbine.test
import com.shodo.android.domain.repositories.entities.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

class MyRepositoryImplTest {

    @Mock private lateinit var request: MyRequest
    @Mock private lateinit var dataStore: MyDataStore

    private lateinit var repository: MyRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = MyRepositoryImpl(request, dataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAll returns flow from dataStore`() = runTest {
        // Given
        `when`(dataStore.getAll()).thenReturn(flow { emit(listOf(defaultItem)) })

        // When
        repository.getAll().test {
            // Then
            assertEquals(listOf(defaultItem), awaitItem())
        }
    }

    @Test
    fun `save delegates to dataStore`() = runTest {
        // When
        repository.save(defaultItem)

        // Then
        verify(dataStore).insert(defaultItem)
    }
}
```

---

## Compose Preview Testing

### Preview Composables

**File:** `feature/myfeature/src/main/java/.../ui/MyFeaturePreview.kt`

```kotlin
package com.shodo.android.myfeature.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.myfeature.MyFeatureUiState
import kotlinx.collections.immutable.persistentListOf

@Preview(name = "Loading State")
@Composable
fun MyFeatureViewLoadingPreview() {
    PokeManiacTheme {
        MyFeatureView(
            uiState = MyFeatureUiState.Loading,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(name = "Data State")
@Composable
fun MyFeatureViewDataPreview() {
    PokeManiacTheme {
        MyFeatureView(
            uiState = MyFeatureUiState.Data(
                items = persistentListOf(
                    ItemUI(id = "1", name = "Item 1"),
                    ItemUI(id = "2", name = "Item 2")
                )
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(name = "Empty State")
@Composable
fun MyFeatureViewEmptyPreview() {
    PokeManiacTheme {
        MyFeatureView(
            uiState = MyFeatureUiState.EmptyResult,
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
```

---

## Best Practices

✅ **DO:**
- Use `runTest` for coroutine tests
- Use **Turbine** for Flow assertions
- Use **Mockito** (`mockito-core`) to mock interfaces and classes
- Test state transitions (Loading → Data, Loading → EmptyResult, Loading → error)
- Include Given/When/Then comments
- Assert `error.message` for `UiError` (not the exception type)
- Use `persistentListOf()` in fixtures when the field type is `PersistentList<>`
- Use `Mockito.mock(Uri::class.java)` for Android types needed in JVM tests
- Use `UnconfinedTestDispatcher` — no need for `advanceUntilIdle()` or `advanceTimeBy()`
- Subscribe to SharedFlow **inside** `error.test {}` before calling the trigger

❌ **DON'T:**
- Use `Thread.sleep()` in tests
- Use MockK (`io.mockk`) — the project uses Mockito
- Assert raw `Exception` from error flows — assert `UiError.message` instead
- Use `Uri.parse(...)` in companion objects or field initializers — it crashes on JVM
- Test implementation details (mapper internals, exact tracking event strings)
- Forget to `resetMain()` in `@After`
- Use `listOf()` where `PersistentList<>` is expected in fixture data
- Assert `viewModel.uiState.value` directly after actions that call `withContext(Dispatchers.Default)` — the mapping runs on a real thread; use Turbine's `awaitItem()` instead
