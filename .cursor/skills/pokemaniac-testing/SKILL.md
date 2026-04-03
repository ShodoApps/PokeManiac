---
name: pokemaniac-testing
description: Write tests for PokeManiac ViewModels, repositories, and composables. Covers unit tests with Mockito, Turbine flow testing, Koin test setup, and Compose preview testing. Use when adding tests for new features.
---

# PokeManiac Testing Patterns

## Testing Setup

### Dependencies (already in build.gradle.kts)

```gradle
testImplementation(libs.junit)
testImplementation(libs.mockito.kotlin)
testImplementation(libs.coroutines.test)
testImplementation(libs.turbine)
testImplementation(libs.koin.test)

androidTestImplementation(libs.espresso.core)
androidTestImplementation(libs.androidx.test.runner)
androidTestImplementation(libs.androidx.compose.ui.test)
```

---

## ViewModel Testing

### Test Template

**File:** `feature/myfeature/src/test/java/.../MyFeatureViewModelTest.kt`

```kotlin
package com.shodo.android.myfeature

import com.shodo.android.domain.entities.Item
import com.shodo.android.domain.repositories.MyRepository
import com.shodo.android.myfeature.navigator.MyFeatureNavigator
import com.shodo.android.tracking.TrackingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test

class MyFeatureViewModelTest {

    private val myRepository = mockk<MyRepository>()
    private val navigator = mockk<MyFeatureNavigator>(relaxed = true)
    private val tracking = mockk<TrackingRepository>(relaxed = true)

    private lateinit var viewModel: MyFeatureViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = MyFeatureViewModel(myRepository, navigator, tracking)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ============ STATE TESTS ============

    @Test
    fun `start emits Loading then Data state`() = runTest {
        // Given
        val mockItems = listOf(
            Item(id = "1", name = "Item 1"),
            Item(id = "2", name = "Item 2")
        )
        coEvery { myRepository.getAll() } returns flow { emit(mockItems) }

        // When
        viewModel.start()

        // Then
        viewModel.uiState.test {
            assertEquals(MyFeatureUiState.Loading, awaitItem())
            
            val dataState = awaitItem() as MyFeatureUiState.Data
            assertEquals(2, dataState.items.size)
            assertEquals("Item 1", dataState.items[0].name)
        }
    }

    @Test
    fun `start with empty data emits EmptyResult`() = runTest {
        // Given
        coEvery { myRepository.getAll() } returns flow { emit(emptyList()) }

        // When
        viewModel.start()

        // Then
        viewModel.uiState.test {
            assertEquals(MyFeatureUiState.Loading, awaitItem())
            assertEquals(MyFeatureUiState.EmptyResult, awaitItem())
        }
    }

    @Test
    fun `start with error emits error and EmptyResult`() = runTest {
        // Given
        val testException = Exception("Network error")
        coEvery { myRepository.getAll() } throws testException

        // When
        viewModel.start()

        // Then
        viewModel.error.test {
            assertEquals(testException, awaitItem())
        }

        viewModel.uiState.test {
            assertEquals(MyFeatureUiState.Loading, awaitItem())
            assertEquals(MyFeatureUiState.EmptyResult, awaitItem())
        }
    }

    // ============ ACTION TESTS ============

    @Test
    fun `onItemClicked navigates to detail and logs event`() {
        // When
        viewModel.onItemClicked("item-123")

        // Then
        coVerify { navigator.navigateToDetail("item-123") }
        coVerify { tracking.logEvent("myfeature_item_clicked", any()) }
    }

    @Test
    fun `onRefresh reloads data`() = runTest {
        // Given
        coEvery { myRepository.getAll() } returns flow { emit(listOf(mockItem)) }

        // When
        viewModel.onRefresh()

        // Then
        coVerify(exactly = 1) { myRepository.getAll() }
    }
}
```

---

## Repository Testing

### Repository Test Template

**File:** `data/src/test/java/.../MyRepositoryTest.kt`

```kotlin
package com.shodo.android.data.myentity

import com.shodo.android.domain.entities.Item
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test

class MyRepositoryTest {

    private val request = mockk<MyRequest>()
    private val dataStore = mockk<MyDataStore>()
    private lateinit var repository: MyRepositoryImpl

    @Before
    fun setUp() {
        repository = MyRepositoryImpl(request, dataStore)
    }

    @Test
    fun `getAll returns data from dataStore`() = runTest {
        // Given
        val mockItems = listOf(Item(id = "1", name = "Item 1"))
        coEvery { dataStore.getAll() } returns flow { emit(mockItems) }

        // When
        repository.getAll().test {
            // Then
            assertEquals(mockItems, awaitItem())
        }
    }

    @Test
    fun `search combines local and remote data`() = runTest {
        // Given
        val localItems = listOf(Item(id = "1", name = "Local Item"))
        val remoteItems = listOf(Item(id = "2", name = "Remote Item"))
        
        coEvery { dataStore.getAll() } returns flow { emit(localItems) }
        coEvery { request.search("test") } returns remoteItems

        // When
        repository.search("test").test {
            // Then - remote takes priority
            val result = awaitItem()
            assertEquals(remoteItems, result)
        }
    }

    @Test
    fun `create persists to both remote and local`() = runTest {
        // Given
        val newItem = Item(id = "new", name = "New Item")
        coEvery { request.create(newItem) } returns newItem
        coEvery { dataStore.insert(newItem) } returns Unit

        // When
        val result = repository.create(newItem)

        // Then
        assert(result.isSuccess)
        coVerify { request.create(newItem) }
        coVerify { dataStore.insert(newItem) }
    }

    @Test
    fun `create returns failure on exception`() = runTest {
        // Given
        val newItem = Item(id = "new", name = "New Item")
        val exception = Exception("Network error")
        coEvery { request.create(newItem) } throws exception

        // When
        val result = repository.create(newItem)

        // Then
        assert(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
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
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.myfeature.MyNewFeatureUiState
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
                    ItemUI(id = "2", name = "Item 2"),
                    ItemUI(id = "3", name = "Item 3")
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

@Preview
@Composable
fun MyFeatureCardPreview() {
    PokeManiacTheme {
        MyFeatureCard(
            item = ItemUI(id = "1", name = "Sample Item"),
            onClick = {}
        )
    }
}
```

---

## Koin Test Setup

### Test Module Setup

```kotlin
@Before
fun setUp() {
    startKoin {
        modules(
            dataModule,
            apiModule,
            databaseModule,
            myFeatureModule
        )
    }
}

@After
fun tearDown() {
    stopKoin()
}
```

### Koin Mocking

```kotlin
@Test
fun `inject mocked repository`() {
    val mockRepository = mockk<MyRepository>()
    
    startKoin {
        modules(
            module {
                factory<MyRepository> { mockRepository }
                viewModelOf(::MyFeatureViewModel)
            }
        )
    }

    val viewModel = koinViewModel<MyFeatureViewModel>()
    assertEquals(mockRepository, viewModel.myRepository)
}
```

---

## Common Test Patterns

### Test with Given/When/Then Comments

```kotlin
@Test
fun `feature displays items when loaded`() = runTest {
    // Given: repository returns items
    coEvery { repository.getAll() } returns flow {
        emit(listOf(mockItem1, mockItem2))
    }

    // When: viewModel starts
    viewModel.start()

    // Then: UI state contains items
    viewModel.uiState.test {
        assert(awaitItem() is MyFeatureUiState.Loading)
        val data = awaitItem() as MyFeatureUiState.Data
        assertEquals(2, data.items.size)
    }
}
```

### Test Error Handling

```kotlin
@Test
fun `error is emitted on repository failure`() = runTest {
    // Given
    val error = Exception("API error")
    coEvery { repository.getAll() } throws error

    // When
    viewModel.start()

    // Then
    viewModel.error.test {
        assertEquals(error, awaitItem())
    }
}
```

### Test Flow Cancellation

```kotlin
@Test
fun `viewModel cleanup on cancel`() = runTest {
    coEvery { repository.getAll() } returns flow {
        delay(10000) // Long running operation
        emit(listOf(mockItem))
    }

    val job = launch {
        viewModel.start()
    }

    advanceTimeBy(100)
    job.cancel()

    // Verify no state was emitted
    viewModel.uiState.test {
        assertEquals(MyFeatureUiState.Loading, awaitItem())
    }
}
```

---

## Instrumentation Testing (UI)

### Espresso Test Example

```kotlin
@RunWith(AndroidJUnit4::class)
class MyFeatureActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            PokeManiacTheme {
                MyFeatureScreen()
            }
        }
    }

    @Test
    fun `displays loading initially`() {
        composeTestRule
            .onNodeWithTag("loader")
            .assertIsDisplayed()
    }

    @Test
    fun `displays items when loaded`() {
        composeTestRule
            .onNodeWithText("Item 1")
            .assertIsDisplayed()
    }
}
```

---

## Best Practices

✅ **DO:**
- Use `runTest` for coroutine tests
- Use `Turbine` for Flow assertions
- Mock external dependencies
- Test state transitions
- Include Given/When/Then comments
- Create Preview composables for all states
- Use `UnconfinedTestDispatcher` for ViewModel tests

❌ **DON'T:**
- Use `Thread.sleep()` in tests
- Test implementation details
- Create overly complex mocks
- Skip error case testing
- Test Compose rendering internals
- Forget to reset Dispatchers
