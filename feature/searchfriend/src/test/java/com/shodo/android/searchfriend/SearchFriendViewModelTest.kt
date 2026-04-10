package com.shodo.android.searchfriend

import app.cash.turbine.test
import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.domain.repositories.tracking.TrackingRepository
import com.shodo.android.searchfriend.uimodel.SearchFriendUI
import com.shodo.android.searchfriend.uimodel.SubscriptionState.NotSubscribed
import com.shodo.android.searchfriend.uimodel.SubscriptionState.Subscribed
import com.shodo.android.searchfriend.uimodel.SubscriptionState.UpdatingSubscribe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class SearchFriendViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var trackingRepository: TrackingRepository

    private lateinit var viewModel: SearchFriendViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        viewModel = SearchFriendViewModel(userRepository, trackingRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ===== searchFriend =====

    @Test
    fun `searchFriend with results emits Data state with mapped people`() = runTest {
        // Given
        `when`(userRepository.searchUsers("friendName")).thenReturn(
            flow { emit(listOf(defaultUserNotSubscribed, defaultUserNotSubscribed2)) }
        )

        viewModel.uiState.test {
            // When
            viewModel.searchFriend("friendName")
            skipItems(2) // EmptySearch → Loading → Data

            // Then
            val state = awaitItem()
            assertTrue(state is SearchFriendUiState.Data)
            assertEquals(listOf(defaultFriendUINotSubscribed, defaultFriendUINotSubscribed2), state.people)
        }
    }

    @Test
    fun `searchFriend with empty query emits EmptySearch`() = runTest {
        viewModel.uiState.test {
            // When
            viewModel.searchFriend("")

            // Then
            assertTrue(awaitItem() is SearchFriendUiState.EmptySearch)
        }
    }

    @Test
    fun `searchFriend with blank query emits EmptySearch`() = runTest {
        viewModel.uiState.test {
            // When
            viewModel.searchFriend("   ")

            // Then
            assertTrue(awaitItem() is SearchFriendUiState.EmptySearch)
        }
    }

    @Test
    fun `searchFriend with no matching users emits EmptyResult with query`() = runTest {
        // Given
        `when`(userRepository.searchUsers("unknown")).thenReturn(
            flow { emit(emptyList()) }
        )

        viewModel.uiState.test {
            // When
            viewModel.searchFriend("unknown")
            skipItems(2) // EmptySearch → Loading

            // Then
            val state = awaitItem()
            assertTrue(state is SearchFriendUiState.EmptyResult)
            assertEquals("unknown", state.query)
        }
    }

    @Test
    fun `searchFriend error emits UiError`() = runTest {
        // Given
        `when`(userRepository.searchUsers("friendName")).thenThrow(RuntimeException("Network error"))

        // Subscriber must be set up BEFORE triggering (SharedFlow replay=0 drops events before subscription)
        viewModel.error.test {
            // When
            viewModel.searchFriend("friendName")

            // Then
            assertEquals("Network error", awaitItem().message)
        }
    }

    // ===== subscribeFriend =====

    @Test
    fun `subscribeFriend transitions through UpdatingSubscribe then Subscribed`() = runTest {
        // Given
        `when`(userRepository.searchUsers("friendName")).thenReturn(
            flow { emit(listOf(defaultUserNotSubscribed, defaultUserNotSubscribed2)) }
        )

        viewModel.uiState.test {
            viewModel.searchFriend("friendName")
            skipItems(3) // EmptySearch → Loading → Data

            // When
            viewModel.subscribeFriend("friendId")

            // Then — UpdatingSubscribe first
            val updatingState = awaitItem()
            assertTrue(updatingState is SearchFriendUiState.Data)
            assertEquals(UpdatingSubscribe, updatingState.people.first { it.id == "friendId" }.subscriptionState)

            // Then — final Subscribed
            val finalState = awaitItem()
            assertTrue(finalState is SearchFriendUiState.Data)
            assertEquals(listOf(defaultFriendUISubscribed, defaultFriendUINotSubscribed2), finalState.people)
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `unsubscribeFriend transitions through UpdatingSubscribe then NotSubscribed`() = runTest {
        // Given
        `when`(userRepository.searchUsers("friendName")).thenReturn(
            flow { emit(listOf(defaultUserSubscribed, defaultUserNotSubscribed2)) }
        )

        viewModel.uiState.test {
            viewModel.searchFriend("friendName")
            skipItems(3) // EmptySearch → Loading → Data

            // When
            viewModel.unsubscribeFriend("friendId")

            // Then — UpdatingSubscribe first
            val updatingState = awaitItem()
            assertTrue(updatingState is SearchFriendUiState.Data)
            assertEquals(UpdatingSubscribe, updatingState.people.first { it.id == "friendId" }.subscriptionState)

            // Then — final NotSubscribed
            val finalState = awaitItem()
            assertTrue(finalState is SearchFriendUiState.Data)
            assertEquals(listOf(defaultFriendUINotSubscribed, defaultFriendUINotSubscribed2), finalState.people)
        }
    }

    @Test
    fun `subscribeFriend does nothing when state is not Data`() = runTest {
        // StateFlow always replays current value — consume it before checking no further events arrive
        viewModel.uiState.test {
            assertTrue(awaitItem() is SearchFriendUiState.EmptySearch) // initial value

            // When — no-op because state is not Data
            viewModel.subscribeFriend("friendId")

            // Then — no additional state change emitted
            ensureAllEventsConsumed()
        }
    }

    companion object {
        private val defaultUserNotSubscribed = User(
            id = "friendId",
            name = "friendName",
            imageUrl = "friendImageUrl",
            description = "friendDescription",
            isSubscribed = false,
            pokemonCards = listOf()
        )
        private val defaultUserNotSubscribed2 = User(
            id = "friendId2",
            name = "friendName2",
            imageUrl = "friendImageUrl2",
            description = "friendDescription2",
            isSubscribed = false,
            pokemonCards = listOf()
        )
        private val defaultUserSubscribed = User(
            id = "friendId",
            name = "friendName",
            imageUrl = "friendImageUrl",
            description = "friendDescription",
            isSubscribed = true,
            pokemonCards = listOf()
        )

        private val defaultFriendUINotSubscribed = SearchFriendUI(
            id = "friendId",
            name = "friendName",
            imageUrl = "friendImageUrl",
            description = "friendDescription",
            subscriptionState = NotSubscribed,
            pokemonCards = persistentListOf()
        )
        private val defaultFriendUINotSubscribed2 = SearchFriendUI(
            id = "friendId2",
            name = "friendName2",
            imageUrl = "friendImageUrl2",
            description = "friendDescription2",
            subscriptionState = NotSubscribed,
            pokemonCards = persistentListOf()
        )
        private val defaultFriendUISubscribed = SearchFriendUI(
            id = "friendId",
            name = "friendName",
            imageUrl = "friendImageUrl",
            description = "friendDescription",
            subscriptionState = Subscribed,
            pokemonCards = persistentListOf()
        )
    }
}
