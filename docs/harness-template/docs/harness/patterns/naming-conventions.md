# Naming Conventions & Module Architecture

> Read when naming a new file, class, interface, or Koin module, or when you need to understand which module does what.
> See root `CLAUDE.md` for dependency direction rules.

---

## Module Architecture

```
Presentation Layer
├── app                    Main entry point; wires all feature Koin modules
├── feature:[feature1]     (replace with your actual feature names)
├── feature:[feature2]
├── feature:[feature3]
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

## Naming Conventions

| Type | Pattern | Example |
|---|---|---|
| AndroidX ViewModel | `*ViewModel` | `MyFeatureViewModel` |
| Shared ScreenModel | `*ScreenModel` | `MyFeatureScreenModel` |
| ScreenModel factory | `*ScreenModelFactory` (fun interface) | `MyFeatureScreenModelFactory` |
| UI State (sealed class) | `*UiState` | `MyFeatureUiState` |
| UI Model (presentation data) | `*UiModel` (shared) / legacy `*UI` / `*Ui` (feature) | `MyFeatureUiModel` |
| Repository interface | `*Repository` | `FriendsRepository` |
| Repository implementation | `*RepositoryImpl` | `FriendsRepositoryImpl` |
| Screen composable (container) | `*Screen` | `MyFeatureScreen` |
| View composable (stateless) | `*View` / `*Content` | `MyFeatureView` |
| Activity | `*Activity` | `MyFeatureActivity` |
| Navigator interface | `*Navigator` | `MyFeatureNavigator` |
| Navigator implementation | `*NavigatorImpl` | `MyFeatureNavigatorImpl` |
| Koin module (feature) | `*Module` (camelCase val) | `myFeatureModule` |
| Request interface | `*Request` | `FriendsRequest` |
| DataStore interface | `*DataStore` | `FriendsDataStore` |
| Room entity | `*Base` | `FriendBase` |
| DAO | `*Dao` | `FriendsDao` |
