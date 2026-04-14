---
name: pokemaniac-data-layer
description: Add repositories, data stores, API services, and database operations to PokeManiac. Covers repository interfaces, implementations, DAOs, API services, and data mapping patterns. Use when extending data access functionality.
---

# PokeManiac Data Layer Implementation

## Architecture Overview

```
Domain Layer (Interfaces)
├── UserRepository (interface)
└── NewsFeedRepository (interface)
        ↓ implemented by
Data Layer (Implementations)
├── UserRepositoryImpl
├── NewsFeedRepositoryImpl
        ↓ consumes
        ├── FriendsRequest (interface)
        │   ├── FriendsRequestImpl (API calls)
        │
        └── FriendsDataStore (interface)
            └── FriendsDataStoreImpl (Room persistence)
```

---

## Step 1: Define Repository Interface (Domain Layer)

**File:** `domain/src/main/java/com/shodo/android/domain/repositories/MyRepository.kt`

```kotlin
package com.shodo.android.domain.repositories

import com.shodo.android.domain.entities.MyEntity
import kotlinx.coroutines.flow.Flow

interface MyRepository {
    fun getAll(): Flow<List<MyEntity>>
    fun getById(id: String): Flow<MyEntity?>
    suspend fun search(query: String): Flow<List<MyEntity>>
    suspend fun create(entity: MyEntity): Result<MyEntity>
    suspend fun update(entity: MyEntity): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}
```

---

## Step 2: Create Request Interface (Data Layer)

**File:** `shared/data/src/commonMain/kotlin/com/shodo/android/data/myentity/MyRequest.kt` (or remote impl under `shared/api/` — see `docs/kmp-migration-plan.md`)

Request interfaces define how to fetch remote data:

```kotlin
package com.shodo.android.data.myentity

import com.shodo.android.domain.entities.MyEntity
import kotlinx.coroutines.flow.Flow

interface MyRequest {
    suspend fun search(query: String): List<MyEntity>
    suspend fun fetchAll(): List<MyEntity>
}
```

---

## Step 3: Create DataStore Interface (Data Layer)

**File:** `shared/data/src/commonMain/kotlin/com/shodo/android/data/myentity/MyDataStore.kt`

DataStore interfaces define how to access local persistence:

```kotlin
package com.shodo.android.data.myentity

import com.shodo.android.domain.entities.MyEntity
import kotlinx.coroutines.flow.Flow

interface MyDataStore {
    fun getAll(): Flow<List<MyEntity>>
    fun getById(id: String): Flow<MyEntity?>
    suspend fun insert(entity: MyEntity)
    suspend fun update(entity: MyEntity)
    suspend fun delete(id: String)
}
```

---

## Step 4: Implement Repository (Data Layer)

**File:** `shared/data/src/commonMain/kotlin/com/shodo/android/data/myentity/MyRepositoryImpl.kt`

Repository combines data from API and local storage:

```kotlin
package com.shodo.android.data.myentity

import com.shodo.android.domain.entities.MyEntity
import com.shodo.android.domain.repositories.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class MyRepositoryImpl(
    private val request: MyRequest,
    private val dataStore: MyDataStore
) : MyRepository {

    override fun getAll(): Flow<List<MyEntity>> {
        return dataStore.getAll()
    }

    override fun getById(id: String): Flow<MyEntity?> {
        return dataStore.getById(id)
    }

    override suspend fun search(query: String): Flow<List<MyEntity>> {
        return combine(
            dataStore.getAll(),
            flow { emit(request.search(query)) }
        ) { local, remote ->
            // Merge local and remote, prefer remote
            remote.ifEmpty { local }.filter { 
                it.name.contains(query, ignoreCase = true)
            }
        }
    }

    override suspend fun create(entity: MyEntity): Result<MyEntity> {
        return try {
            val created = request.create(entity)  // Remote first
            dataStore.insert(created)             // Then local
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(entity: MyEntity): Result<Unit> {
        return try {
            request.update(entity)
            dataStore.update(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: String): Result<Unit> {
        return try {
            request.delete(id)
            dataStore.delete(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## Step 5: Create API Service & Request Implementation

**File:** `api/src/main/java/com/shodo/android/api/core/remote/MyApiService.kt`

```kotlin
package com.shodo.android.api.core.remote

import com.shodo.android.api.core.remote.dto.MyEntityDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Query

interface MyApiService {
    @GET("entities")
    suspend fun getAll(): List<MyEntityDto>

    @GET("entities")
    suspend fun search(@Query("q") query: String): List<MyEntityDto>

    @POST("entities")
    suspend fun create(@Body entity: MyEntityDto): MyEntityDto

    @DELETE("entities/{id}")
    suspend fun delete(@Query("id") id: String)
}
```

**File:** `api/src/main/java/com/shodo/android/api/myentity/MyRequestImpl.kt`

```kotlin
package com.shodo.android.api.myentity

import com.shodo.android.api.core.remote.MyApiService
import com.shodo.android.api.core.remote.dto.MyEntityDto
import com.shodo.android.data.myentity.MyRequest
import com.shodo.android.domain.entities.MyEntity

class MyRequestImpl(
    private val apiService: MyApiService
) : MyRequest {

    override suspend fun search(query: String): List<MyEntity> {
        return apiService.search(query).map { it.mapToDomain() }
    }

    override suspend fun fetchAll(): List<MyEntity> {
        return apiService.getAll().map { it.mapToDomain() }
    }

    private fun MyEntityDto.mapToDomain() = MyEntity(
        id = this.id,
        name = this.name,
        description = this.description
    )
}
```

---

## Step 6: Create DAO & Room Entity

**File:** `database/src/main/java/com/shodo/android/database/myentity/MyEntityBase.kt`

Room entity (with naming convention `*Base`):

```kotlin
package com.shodo.android.database.myentity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_entities")
data class MyEntityBase(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String
)
```

**File:** `database/src/main/java/com/shodo/android/database/myentity/MyEntityDao.kt`

```kotlin
package com.shodo.android.database.myentity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MyEntityDao {
    
    @Query("SELECT * FROM my_entities")
    fun getAll(): Flow<List<MyEntityBase>>

    @Query("SELECT * FROM my_entities WHERE id = :id")
    fun getById(id: String): Flow<MyEntityBase?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MyEntityBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<MyEntityBase>)

    @Update
    suspend fun update(entity: MyEntityBase)

    @Query("DELETE FROM my_entities WHERE id = :id")
    suspend fun delete(id: String)
}
```

---

## Step 7: Create DataStore Implementation

**File:** `database/src/main/java/com/shodo/android/database/myentity/MyDataStoreImpl.kt`

```kotlin
package com.shodo.android.database.myentity

import com.shodo.android.data.myentity.MyDataStore
import com.shodo.android.domain.entities.MyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MyDataStoreImpl(
    private val dao: MyEntityDao
) : MyDataStore {

    override fun getAll(): Flow<List<MyEntity>> {
        return dao.getAll().map { entities ->
            entities.map { it.mapToDomain() }
        }
    }

    override fun getById(id: String): Flow<MyEntity?> {
        return dao.getById(id).map { it?.mapToDomain() }
    }

    override suspend fun insert(entity: MyEntity) {
        dao.insert(entity.mapToDatabase())
    }

    override suspend fun update(entity: MyEntity) {
        dao.update(entity.mapToDatabase())
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }

    private fun MyEntityBase.mapToDomain() = MyEntity(
        id = this.id,
        name = this.name,
        description = this.description
    )

    private fun MyEntity.mapToDatabase() = MyEntityBase(
        id = this.id,
        name = this.name,
        description = this.description
    )
}
```

---

## Step 8: Register in Room Database

**File:** `database/src/main/java/com/shodo/android/database/PokeManiacDatabase.kt`

```kotlin
package com.shodo.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shodo.android.database.myentity.MyEntityDao
import com.shodo.android.database.myentity.MyEntityBase

@Database(
    entities = [
        MyEntityBase::class,
        // ... other entities
    ],
    version = 1
)
abstract class PokeManiacDatabase : RoomDatabase() {
    abstract fun myEntityDao(): MyEntityDao
    // ... other DAOs
}
```

---

## Step 9: Register Koin Bindings

**In `dependencyinjection/src/main/java/.../CleanArchiModules.kt`:**

```kotlin
val dataModule = module {
    factory<MyRepository> { MyRepositoryImpl(get(), get()) }
}

val apiModule = module {
    factory<MyRequest> { MyRequestImpl(get()) }
}

val databaseModule = module {
    single<MyDataStore> { MyDataStoreImpl(get<PokeManiacDatabase>().myEntityDao()) }
}
```

---

## Data Flow Example

```
ViewModel calls:
repository.search("pokemon")
    ↓
MyRepositoryImpl.search("pokemon")
    ↓ combines:
    ├─ dataStore.getAll()           [Local Room data]
    └─ request.search("pokemon")    [Remote API]
        ↓
    MyRequestImpl.search(query)
        ↓
    apiService.search(query)        [Retrofit call]
        ↓
    MyApiService interface          [HTTP call]
```

---

## Testing Data Layer

**Repository Test:**
```kotlin
class MyRepositoryTest {
    private val request = mockk<MyRequest>()
    private val dataStore = mockk<MyDataStore>()
    private lateinit var repository: MyRepository

    @Before
    fun setUp() {
        repository = MyRepositoryImpl(request, dataStore)
    }

    @Test
    fun `search combines local and remote data`() = runTest {
        coEvery { request.search("test") } returns listOf(mockEntity1)
        every { dataStore.getAll() } returns flow { emit(listOf(mockEntity2)) }

        repository.search("test").test {
            val result = awaitItem()
            assertEquals(2, result.size)
        }
    }
}
```

---

## Important Patterns

✅ **DO:**
- Use Flow for reads (reactive)
- Use suspend for writes (one-time operations)
- Map entities at layer boundaries
- Implement Result for error handling
- Combine local and remote sources in repository

❌ **DON'T:**
- Expose Room entities in domain layer
- Skip entity mapping
- Mix domain and database models
- Block on database calls
- Ignore error handling in repository
