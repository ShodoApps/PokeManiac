# Checklist: Adding API Endpoint & Data Operations

> Read `docs/harness/patterns/kmp-patterns.md` for type pipeline and ScreenModel patterns.
> Read `docs/harness/patterns/di-wiring.md` for Koin registration.

---

## Domain layer (`shared/domain/src/commonMain/`)
- [ ] Repository interface: `domain/repositories/My*Repository.kt` — `Flow<>` for reads, `suspend fun` for writes
- [ ] Domain entity: `domain/entities/MyEntity.kt`

## Data layer (`shared/data/src/commonMain/`)
- [ ] DataStore interface: `data/myentity/MyDataStore.kt`
- [ ] Repository implementation: `data/myentity/MyRepositoryImpl.kt` — constructor-injects `MyRequest` + `MyDataStore`
- [ ] Maps DTOs → domain in repository impl; never expose DTOs via repository interface

## API layer (`shared/api/src/commonMain/` or `androidMain/`)
- [ ] DTO: `api/myentity/dto/MyEntityDto.kt` with `@Serializable`
- [ ] Request implementation: `api/myentity/MyRequestImpl.kt` — maps DTOs → domain entities

## Database layer (`shared/database/src/commonMain/`)
- [ ] Room entity: `database/myentity/MyEntityBase.kt` (`@Entity`, `@PrimaryKey`)
- [ ] DAO: `database/myentity/MyEntityDao.kt` (`Flow` for reads, `suspend` for writes)
- [ ] DataStore implementation: `database/myentity/MyDataStoreImpl.kt` — maps `MyEntityBase` ↔ domain
- [ ] DAO added to `@Database(entities = [...])` in `[PROJECT_NAME]Database.kt`

## Koin registration
- [ ] `SharedKoinArchiModules.kt` (`shared/di`): `dataModule` + `apiModule` — add `factory<MyRepository>`, `factory<MyRequest>`
- [ ] `app/di/DatabaseModule.kt`: `databaseModule` — add `single<MyDataStore> { MyDataStoreImpl(get<[PROJECT_NAME]Database>().myDao()) }`

## Gradle validation
- [ ] `./gradlew testDebugUnitTest` passes
- [ ] `./gradlew assembleRelease` passes

## Tests
- [ ] `MyRepositoryImplTest.kt` — mock `MyRequest` + `MyDataStore` with Mockito
- [ ] Test: `getAll()` returns flow from dataStore
- [ ] Test: `search()` combines local + remote
- [ ] Test: write operations delegate to both request and dataStore
- [ ] Follow **Given / When / Then** comments
