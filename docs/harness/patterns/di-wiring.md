# DI Wiring — Koin

> Read when setting up Koin modules for a new feature or shared module.
> See `docs/harness/checklists/new-feature.md` for where to register modules in `PokeManiacApplication.kt`.

---

## Shared modules (`shared:di` — `commonMain`)

File: `SharedKoinArchiModules.kt`

```kotlin
val apiModule = module {
    factory<MyRequest> { MyRequestImpl(get()) }
}
val dataModule = module {
    factory<MyRepository> { MyRepositoryImpl(get(), get()) }
}
fun sharedKoinArchiModules() = listOf(apiModule, dataModule)
```

## Android bootstrap (`app/di/DatabaseModule.kt`)

```kotlin
val databaseModule = module {
    single { Room.databaseBuilder(androidApplication(), PokeManiacDatabase::class.java, "db").build() }
    single<MyDataStore> { MyDataStoreImpl(get<PokeManiacDatabase>().myDao()) }
}
fun appCoreArchiModules() = listOf(databaseModule) + sharedKoinArchiModules()
```

## Feature module (`di/MyFeatureModule.kt`)

ScreenModelFactory pattern — the factory closes over `get()` for repositories, scope is provided at creation time by the ViewModel:

```kotlin
fun interface MyFeatureScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): MyFeatureScreenModel
}

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

## Registering a feature module in app

In `PokeManiacApplication.kt`:

```kotlin
startKoin {
    androidContext(this@PokeManiacApplication)
    modules(
        appCoreArchiModules() +
        listOf(
            searchFriendModule,
            myFriendsModule,
            myFeatureModule   // ← add here
        )
    )
}
```
