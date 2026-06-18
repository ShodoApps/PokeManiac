# Naming Conventions & Module Architecture

> Read when naming a new file, class, interface, or Koin module, or when you need to understand which module does what.
> See root `CLAUDE.md` for dependency direction rules.

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
