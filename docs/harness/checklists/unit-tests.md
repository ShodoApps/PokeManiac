# Checklist: Writing Unit Tests

> Tests for Android ViewModels live in `feature/myfeature/src/test/java/`.
> Tests for shared ScreenModels live in `shared/presentation/src/commonTest/kotlin/`.
> Key libraries: JUnit 4 (Android), kotlin.test (KMP), Mockito (Android), manual fakes (KMP), Turbine, `kotlinx-coroutines-test`.

---

## Part 1 — Android ViewModel Tests

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

## Part 2 — Shared ScreenModel Tests (pure KMP — no Android setup)

ScreenModels in `:shared:presentation` are plain Kotlin. Their tests run on the JVM without any Android machinery — no `Dispatchers.setMain()`, no Activity, no instrumentation. This is one of the main practical benefits of moving logic to shared KMP.

### Add to `shared/presentation/build.gradle.kts`

```kotlin
sourceSets {
    commonTest.dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.turbine)
    }
}
```

All three libraries are already in `libs.versions.toml`.

### Test file location

```
shared/presentation/src/commonTest/kotlin/com/shodo/android/presentation/
└── searchfriend/
    └── SearchFriendScreenModelTest.kt
```

### Key differences from Android ViewModel tests

| | Android `*ViewModelTest` | Shared `*ScreenModelTest` |
|---|---|---|
| Annotation | `@Before` (JUnit4) | `@BeforeTest` (kotlin.test) |
| Dispatcher setup | `Dispatchers.setMain(UnconfinedTestDispatcher())` | Not needed — pass `TestScope` directly |
| Run test | `runTest { }` | `testScope.runTest { }` |
| Mocking | Mockito `@Mock` | Manual fakes — Mockito is JVM-only |
| Error type | `UiError` | `PresentationError` |
| Test runner | Android JUnit | JVM (fast, no emulator) |

### Test pattern (reference: `SearchFriendScreenModel`)

```kotlin
class SearchFriendScreenModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val userRepository = FakeUserRepository()
    private val trackingRepository = FakeTrackingRepository()

    private lateinit var screenModel: SearchFriendScreenModel

    @BeforeTest
    fun setUp() {
        screenModel = SearchFriendScreenModel(
            userRepository = userRepository,
            trackingRepository = trackingRepository,
            coroutineScope = testScope
        )
    }

    @Test
    fun `searchFriend emits Loading then Data when results found`() = testScope.runTest {
        // Given
        userRepository.searchResults = listOf(mockUser)

        screenModel.uiState.test {
            assertEquals(SearchFriendUiState.EmptySearch, awaitItem()) // initial value

            // When
            screenModel.searchFriend("ash")

            // Then
            assertEquals(SearchFriendUiState.Loading, awaitItem())
            assertTrue(awaitItem() is SearchFriendUiState.Data)
        }
    }

    @Test
    fun `searchFriend emits PresentationError when repository throws`() = testScope.runTest {
        // Given
        userRepository.shouldThrow = RuntimeException("Network error")

        // When / Then — subscribe before triggering (SharedFlow replay=0)
        screenModel.error.test {
            screenModel.searchFriend("ash")
            assertEquals("Network error", awaitItem().message)
        }
    }
}
```

### Manual fakes — place in `commonTest`

Mockito requires JVM. In `commonTest`, implement interfaces by hand. Keep fakes minimal — only the behaviour under test:

```kotlin
class FakeUserRepository : UserRepository {
    var searchResults: List<User> = emptyList()
    var shouldThrow: Exception? = null

    override fun searchUsers(name: String): Flow<List<User>> {
        shouldThrow?.let { throw it }
        return flow { emit(searchResults) }
    }
    // stub other methods with TODO() or empty implementations
}

class FakeTrackingRepository : TrackingRepository {
    override suspend fun sendEventScreen(event: TrackingEventScreen) = Unit
    override suspend fun sendEventClick(event: TrackingEventClick) = Unit
}
```

### Run the tests

```bash
./gradlew :shared:presentation:testDebugUnitTest   # runs commonTest on JVM via androidTarget
```

---

## Coverage policy

- [ ] Every `*Repository` interface has a corresponding `*RepositoryImplTest.kt`
- [ ] Every `*ViewModel` has a corresponding `*ViewModelTest.kt` in `src/test/`
- [ ] Every `*ScreenModel` has a corresponding `*ScreenModelTest.kt` in `commonTest/`
- [ ] Every test class covers: at least one happy path + one error case + one empty/edge case
