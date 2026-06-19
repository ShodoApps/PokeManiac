# Checklist: Adding a New Feature Module

> Read `docs/harness/patterns/feature-patterns.md` for code examples of every pattern below.
> Read `docs/harness/patterns/di-wiring.md` for Koin module setup.

---

## Setup
- [ ] Feature name decided (snake_case: `mynewfeature`)
- [ ] Created `feature/mynewfeature/` with subdirs: `ui/`, `uimodel/`, `di/`, `navigator/`
- [ ] Created `src/test/java/` for tests

## Core files
- [ ] `MyNewFeatureActivity.kt` — `setContent { [PROJECT_NAME]Theme { MyNewFeatureScreen() } }`
- [ ] `MyNewFeatureScreen.kt` — `collectAsStateWithLifecycle`, `observeWithLifecycle`, `OnLifecycleEventEffect`
- [ ] `MyNewFeatureViewModel.kt` — thin wrapper; `lazy { factory.create(viewModelScope) }`
- [ ] `ui/MyNewFeatureView.kt` — stateless; `when (uiState) { ... }`
- [ ] `ui/MyNewFeaturePreview.kt` — one preview per UiState branch
- [ ] `uimodel/MyNewFeatureUI.kt` — `@Immutable data class` + domain→UI extension functions

## ScreenModel (in `:shared:presentation`)
- [ ] `MyNewFeatureUiState` sealed class (plain Kotlin, no `@Immutable` in commonMain)
- [ ] `MyNewFeatureScreenModel` with `StateFlow<MyNewFeatureUiState>` + `SharedFlow<UiError>`
- [ ] `fun interface MyNewFeatureScreenModelFactory`

## Navigation
- [ ] Navigator interface created in `coreui/navigator/`
- [ ] `MyNewFeatureNavigatorImpl.kt` in feature module

## DI
- [ ] `di/MyNewFeatureModule.kt` — `factory<MyNewFeatureScreenModelFactory>`, `viewModelOf(::MyNewFeatureViewModel)`, `single<Navigator>`
- [ ] Module added to `[PROJECT_NAME]Application.kt` `startKoin { modules(..., myNewFeatureModule) }`

## Gradle
- [ ] `feature/mynewfeature/build.gradle.kts` — deps: `:shared:domain`, `:shared:presentation`, `coreui`, `:shared:tracking`, Compose, Koin
- [ ] Added to `settings.gradle.kts`: `include(":feature:mynewfeature")`
- [ ] Added to `app/build.gradle.kts`: `implementation(project(":feature:mynewfeature"))`

## Imports verification
- [ ] ❌ NO imports from `:shared:data`, `:shared:api`, `:shared:database`
- [ ] ✅ Only `domain`, `coreui`, `presentation`, `tracking` (interface only)

## Tests
- [ ] `MyNewFeatureViewModelTest.kt` — at least one happy path + one error case

## Mobile best practices

- [ ] If the feature adds a **new library**: check if it requires ProGuard/R8 rules — add them to `proguard-rules.pro` before testing `./gradlew assembleRelease`
- [ ] If the feature requests an **Android runtime permission**: implement three states — request → show rationale if denied once → direct to Settings (`Settings.ACTION_APPLICATION_DETAILS_SETTINGS`) if permanently denied
- [ ] If the feature requests an **iOS runtime permission**: declare the corresponding `NS*UsageDescription` in `Info.plist` before requesting (missing key = crash on device); permission is one-shot — after denial, direct user to `UIApplication.openSettingsURLString`
- [ ] For photo selection on iOS: prefer `PHPickerViewController` (no permission required) over `UIImagePickerController` (see `docs/harness/patterns/ios-patterns.md`)
