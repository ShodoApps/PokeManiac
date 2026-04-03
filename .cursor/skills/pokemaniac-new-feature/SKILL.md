---
name: pokemaniac-new-feature
description: Add a new feature module to PokeManiac following architecture patterns. Includes Activity, ViewModel, Repository integration, navigation, and DI setup. Use when adding new user-facing features to the app.
---

# Adding a New Feature to PokeManiac

## Quick Checklist

- [ ] Create feature module directory
- [ ] Create Activity, Screen, ViewModel, View files
- [ ] Create UI state sealed class
- [ ] Create Navigator interface (in coreui) and implementation
- [ ] Create DI module with Koin bindings
- [ ] Add feature to root `settings.gradle.kts`
- [ ] Add feature dependency to `app/build.gradle.kts`
- [ ] Add PokeManiacTheme wrapper to Activity
- [ ] Add feature module to `PokeManiacApplication.kt` startKoin

---

## Step 1: Create Feature Module Structure

```
feature/
└── mynewfeature/
    ├── build.gradle.kts
    ├── src/main/java/com/shodo/android/mynewfeature/
    │   ├── MyNewFeatureActivity.kt
    │   ├── MyNewFeatureScreen.kt
    │   ├── MyNewFeatureViewModel.kt
    │   ├── di/
    │   │   └── MyNewFeatureModule.kt
    │   ├── navigator/
    │   │   └── MyNewFeatureNavigatorImpl.kt
    │   ├── ui/
    │   │   ├── MyNewFeatureView.kt
    │   │   ├── MyNewFeatureContent.kt
    │   │   └── MyNewFeaturePreview.kt
    │   └── uimodel/
    │       └── MyNewFeatureUI.kt
    ├── src/main/res/
    └── src/test/java/...
```

---

## Step 2: Define UI State (MyNewFeatureViewModel.kt)

```kotlin
package com.shodo.android.mynewfeature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.shodo.android.domain.repositories.MyRepository
import com.shodo.android.mynewfeature.navigator.MyNewFeatureNavigator
import com.shodo.android.tracking.TrackingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MyNewFeatureUiState {
    data object Loading : MyNewFeatureUiState()
    data class Data(val items: PersistentList<ItemUI>) : MyNewFeatureUiState()
    data object EmptyResult : MyNewFeatureUiState()
}

class MyNewFeatureViewModel(
    private val myRepository: MyRepository,
    private val navigator: MyNewFeatureNavigator,
    private val tracking: TrackingRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<MyNewFeatureUiState> = MutableStateFlow(Loading)
    val uiState: StateFlow<MyNewFeatureUiState> = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<Exception>()
    val error: SharedFlow<Exception> = _error.asSharedFlow()

    fun start() {
        viewModelScope.launch {
            loadData()
        }
    }

    fun onItemClicked(itemId: String) {
        tracking.logEvent("mynewfeature_item_clicked", mapOf("item_id" to itemId))
        navigator.navigateToDetail(itemId)
    }

    private suspend fun loadData() {
        try {
            _uiState.value = Loading
            val items = myRepository.fetchItems()
            _uiState.value = MyNewFeatureUiState.Data(items.mapToUI().toPersistentList())
        } catch (e: Exception) {
            _error.emit(e)
            _uiState.value = MyNewFeatureUiState.EmptyResult
        }
    }

    private fun Item.mapToUI() = ItemUI(
        id = this.id,
        name = this.name
    )
}
```

---

## Step 3: Create Screen Container (MyNewFeatureScreen.kt)

```kotlin
package com.shodo.android.mynewfeature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyNewFeatureScreen(
    modifier: Modifier = Modifier,
    viewModel: MyNewFeatureViewModel = koinViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(error.message.toString())
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_START) {
                viewModel.start()
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    MyNewFeatureView(
        modifier = modifier,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onItemClicked = viewModel::onItemClicked
    )
}
```

---

## Step 4: Create Stateless View (MyNewFeatureView.kt)

```kotlin
package com.shodo.android.mynewfeature.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shodo.android.coreui.components.GenericEmptyScreen
import com.shodo.android.coreui.components.GenericLoader
import com.shodo.android.mynewfeature.MyNewFeatureUiState

@Composable
fun MyNewFeatureView(
    modifier: Modifier = Modifier,
    uiState: MyNewFeatureUiState,
    snackbarHostState: SnackbarHostState,
    onItemClicked: (itemId: String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MyNewFeatureTopBar()
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState) {
                MyNewFeatureUiState.Loading -> {
                    GenericLoader()
                }
                is MyNewFeatureUiState.Data -> {
                    MyNewFeatureContent(
                        items = uiState.items,
                        onItemClicked = onItemClicked
                    )
                }
                MyNewFeatureUiState.EmptyResult -> {
                    GenericEmptyScreen()
                }
            }
        }
    }
}

@Composable
fun MyNewFeatureContent(
    items: PersistentList<ItemUI>,
    onItemClicked: (itemId: String) -> Unit
) {
    LazyColumn {
        items(items) { item ->
            MyNewFeatureCard(
                item = item,
                onClick = { onItemClicked(item.id) }
            )
        }
    }
}

@Composable
fun MyNewFeatureTopBar() {
    // App bar implementation
}

@Composable
fun MyNewFeatureCard(
    item: ItemUI,
    onClick: () -> Unit
) {
    // Card implementation
}

@Preview
@Composable
fun MyNewFeatureViewPreview() {
    PokeManiacTheme {
        MyNewFeatureView(
            uiState = MyNewFeatureUiState.Data(
                items = persistentListOf(
                    ItemUI("1", "Item 1"),
                    ItemUI("2", "Item 2")
                )
            ),
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
```

---

## Step 5: Create Activity (MyNewFeatureActivity.kt)

```kotlin
package com.shodo.android.mynewfeature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.shodo.android.coreui.theme.PokeManiacTheme
import org.koin.androidx.compose.koinViewModel

class MyNewFeatureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeManiacTheme {
                MyNewFeatureScreen()
            }
        }
    }
}
```

---

## Step 6: Create Navigator (in coreui & feature)

**In `coreui/src/main/java/com/shodo/android/coreui/navigator/MyNewFeatureNavigator.kt`:**
```kotlin
package com.shodo.android.coreui.navigator

interface MyNewFeatureNavigator {
    fun navigateToDetail(itemId: String)
    fun navigateBack()
}
```

**In `feature/mynewfeature/navigator/MyNewFeatureNavigatorImpl.kt`:**
```kotlin
package com.shodo.android.mynewfeature.navigator

import android.content.Context
import android.content.Intent
import com.shodo.android.coreui.navigator.MyNewFeatureNavigator
import com.shodo.android.mynewfeature.MyNewFeatureActivity

class MyNewFeatureNavigatorImpl(private val context: Context) : MyNewFeatureNavigator {
    override fun navigateToDetail(itemId: String) {
        val intent = Intent(context, MyDetailActivity::class.java).apply {
            putExtra("item_id", itemId)
        }
        context.startActivity(intent)
    }

    override fun navigateBack() {
        // Handle back navigation if needed
    }
}
```

---

## Step 7: Create DI Module (MyNewFeatureModule.kt)

```kotlin
package com.shodo.android.mynewfeature.di

import com.shodo.android.coreui.navigator.MyNewFeatureNavigator
import com.shodo.android.mynewfeature.MyNewFeatureViewModel
import com.shodo.android.mynewfeature.navigator.MyNewFeatureNavigatorImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val myNewFeatureModule = module {
    viewModelOf(::MyNewFeatureViewModel)
    single<MyNewFeatureNavigator> { MyNewFeatureNavigatorImpl(get()) }
}
```

---

## Step 8: Add to Gradle

**In `settings.gradle.kts`:**
```kotlin
include(":feature:mynewfeature")
```

**In `app/build.gradle.kts` dependencies:**
```kotlin
implementation(project(":feature:mynewfeature"))
```

**In `feature/mynewfeature/build.gradle.kts`:**
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.shodo.android.mynewfeature"
    // ... rest of config
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":coreui"))
    implementation(project(":tracking"))
    
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
}
```

---

## Step 9: Register in Application

**In `app/src/main/java/com/shodo/android/pokemaniac/PokeManiacApplication.kt`:**
```kotlin
val modules = listOf(
    cleanArchiModules,
    trackingModule,
    welcomeModule,
    dashboardModule,
    searchfriendModule,
    myfriends,
    myprofileModule,
    posttransactionModule,
    billingModule,
    myNewFeatureModule  // ADD THIS
)

startKoin {
    androidContext(this@PokeManiacApplication)
    modules(modules)
}
```

---

## Important Reminders

✅ **DO:**
- Import from `domain`, `coreui`, `tracking` only
- Use sealed class for UI state
- Keep ViewModel logic clean
- Map entities to UI models in ViewModel
- Include Preview composables
- Use PersistentList for immutable collections

❌ **DON'T:**
- Import from `data`, `api`, `database`
- Use multiple Boolean flags for state
- Expose domain models in UI state
- Forget lifecycle callback in Screen
- Skip Koin binding setup
