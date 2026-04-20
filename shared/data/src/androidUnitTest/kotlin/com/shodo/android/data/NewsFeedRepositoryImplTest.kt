package com.shodo.android.data

import app.cash.turbine.test
import com.shodo.android.domain.datastore.FriendsDataStore
import com.shodo.android.domain.datastore.MyActivitiesDataStore
import com.shodo.android.data.newsfeed.NewsFeedRepositoryImpl
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.entities.UserPokemonCard
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
import kotlinx.datetime.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class NewsFeedRepositoryImplTest {

    @Mock private lateinit var friendsDataStore: FriendsDataStore
    @Mock private lateinit var myActivitiesDataStore: MyActivitiesDataStore

    private lateinit var repository: NewsFeedRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = NewsFeedRepositoryImpl(friendsDataStore, myActivitiesDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ===== saveNewActivity() =====

    @Test
    fun `saveNewActivity delegates to myActivitiesDataStore`() = runTest {
        // When
        repository.saveNewActivity(defaultActivity)

        // Then
        verify(myActivitiesDataStore).saveNewActivity(defaultActivity)
    }

    // ===== getNewActivities() =====

    @Test
    fun `getNewActivities returns empty list when all users are null and no personal activities`() = runTest {
        // Given — all friend lookups return null users, no personal activities
        stubAllFriendsNull()
        `when`(myActivitiesDataStore.getAllActivities()).thenReturn(flow { emit(emptyList()) })

        // When
        repository.getNewActivities().test {
            // Then
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getNewActivities returns only personal activities when all friends are null`() = runTest {
        // Given
        stubAllFriendsNull()
        val myActivity = activity("Pikachu", LocalDateTime(2025, 3, 1, 10, 0), 25)
        `when`(myActivitiesDataStore.getAllActivities()).thenReturn(flow { emit(listOf(myActivity)) })

        // When
        repository.getNewActivities().test {
            // Then
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals(myActivity, result[0])
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getNewActivities merges personal and friend activities`() = runTest {
        // Given — batman69 has 4 cards (indices 2 and 3 are used by the impl), batman70 null, superman720 null
        `when`(friendsDataStore.getFriendById("69")).thenReturn(flow { emit(userWithCards("Batman69", 4)) })
        `when`(friendsDataStore.getFriendById("70")).thenReturn(flow { emit(null) })
        `when`(friendsDataStore.getFriendById("720")).thenReturn(flow { emit(null) })

        val myActivity = activity("Pikachu", LocalDateTime(2024, 1, 1, 0, 0), 25)
        `when`(myActivitiesDataStore.getAllActivities()).thenReturn(flow { emit(listOf(myActivity)) })

        // When
        repository.getNewActivities().test {
            val result = awaitItem()

            // Then — personal activity + friend cards (index 2, 3) + hardcoded Golbat = 4 total
            assertTrue(result.size > 1)
            assertTrue(result.any { it.userName == "Ash" }) // personal activity
            assertTrue(result.any { it.userName == "Batman69" }) // friend activity
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getNewActivities sorts results by date descending`() = runTest {
        // Given — batman70 has 1 card (index 0), myActivities has an older activity
        stubAllFriendsNull()
        `when`(friendsDataStore.getFriendById("70")).thenReturn(flow { emit(userWithCards("Batman70", 1)) })

        val oldActivity = activity("Pikachu", LocalDateTime(2020, 1, 1, 0, 0), 25)
        val recentActivity = activity("Charizard", LocalDateTime(2025, 6, 1, 0, 0), 6)
        `when`(myActivitiesDataStore.getAllActivities()).thenReturn(
            flow { emit(listOf(oldActivity, recentActivity)) }
        )

        // When
        repository.getNewActivities().test {
            val result = awaitItem()

            // Then — most recent first
            assertTrue(result.size >= 2)
            for (i in 0 until result.size - 1) {
                assertTrue(
                    result[i].date >= result[i + 1].date,
                    "Expected descending order at index $i: ${result[i].date} >= ${result[i + 1].date}"
                )
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== Helpers =====

    private fun stubAllFriendsNull() {
        `when`(friendsDataStore.getFriendById("69")).thenReturn(flow { emit(null) })
        `when`(friendsDataStore.getFriendById("70")).thenReturn(flow { emit(null) })
        `when`(friendsDataStore.getFriendById("720")).thenReturn(flow { emit(null) })
    }

    private fun userWithCards(name: String, cardCount: Int) = User(
        id = name.lowercase(),
        name = name,
        imageUrl = "https://image.url/$name.jpg",
        description = "$name description",
        isSubscribed = true,
        pokemonCards = (1..cardCount).map { i ->
            UserPokemonCard(
                pokemonId = i,
                name = "Card $i",
                imageSource = ImageSource.UrlSource("https://image.url/card$i.jpg"),
                totalVotes = 0,
                hasMyVote = false
            )
        }
    )

    private fun activity(cardName: String, date: LocalDateTime, pokemonId: Int) = NewActivity(
        userName = "Ash",
        userImageUrl = null,
        date = date,
        pokemonCard = UserPokemonCard(
            pokemonId = pokemonId,
            name = cardName,
            imageSource = ImageSource.UrlSource("https://image.url/$cardName.jpg"),
            totalVotes = 0,
            hasMyVote = false
        ),
        activityType = NewActivityType.Purchase,
        price = 10
    )

    companion object {
        private val defaultActivity = NewActivity(
            userName = "Ash",
            userImageUrl = null,
            date = LocalDateTime(2024, 6, 1, 12, 0),
            pokemonCard = UserPokemonCard(
                pokemonId = 25,
                name = "Pikachu",
                imageSource = ImageSource.UrlSource("https://image.url/pikachu.jpg"),
                totalVotes = 0,
                hasMyVote = false
            ),
            activityType = NewActivityType.Purchase,
            price = 30
        )
    }
}
