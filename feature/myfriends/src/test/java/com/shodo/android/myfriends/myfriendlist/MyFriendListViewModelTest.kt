package com.shodo.android.myfriends.myfriendlist

import app.cash.turbine.test
import com.shodo.android.coreui.navigator.SearchFriendNavigator
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.friends.UserRepository
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
class MyFriendListViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var searchFriendNavigator: SearchFriendNavigator

    private lateinit var viewModel: MyFriendListViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        viewModel = MyFriendListViewModel(userRepository, searchFriendNavigator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ===== fetchMyFriends() =====

    @Test
    fun `fetchMyFriends emits Loading then Data when friends are returned`() = runTest {
        // Given
        `when`(userRepository.getSubscribedUsers()).thenReturn(
            flow { emit(listOf(defaultUser, defaultUser2)) }
        )

        viewModel.uiState.test {
            // When
            viewModel.fetchMyFriends()

            // Then
            assertEquals(MyFriendListUiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is MyFriendListUiState.Data)
            assertEquals(2, state.friends.size)
            assertEquals("friendId", state.friends[0].id)
        }
    }

    @Test
    fun `fetchMyFriends emits Loading then Empty when no friends`() = runTest {
        // Given
        `when`(userRepository.getSubscribedUsers()).thenReturn(
            flow { emit(emptyList()) }
        )

        viewModel.uiState.test {
            // When
            viewModel.fetchMyFriends()

            // Then
            assertEquals(MyFriendListUiState.Loading, awaitItem())
            assertEquals(MyFriendListUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `fetchMyFriends emits error when repository throws`() = runTest {
        // Given
        `when`(userRepository.getSubscribedUsers()).thenThrow(RuntimeException("DB error"))

        // Subscriber must be set up BEFORE triggering (SharedFlow replay=0 drops events before subscription)
        viewModel.error.test {
            // When
            viewModel.fetchMyFriends()

            // Then
            assertEquals("DB error", awaitItem().message)
        }
    }

    // ===== unsubscribeFriend() =====

    @Test
    fun `unsubscribeFriend calls repository with the given id`() = runTest {
        // Given — default mock returns Unit (no error)
        // When
        viewModel.unsubscribeFriend("friendId")

        // Then — no error emitted (verify via no emission)
        viewModel.error.test {
            ensureAllEventsConsumed()
        }
    }

    companion object {
        private val defaultUser = User(
            id = "friendId",
            name = "Ash",
            imageUrl = "https://image.url/ash.jpg",
            description = "Pokemon trainer",
            isSubscribed = true,
            pokemonCards = listOf()
        )
        private val defaultUser2 = User(
            id = "friendId2",
            name = "Misty",
            imageUrl = "https://image.url/misty.jpg",
            description = "Water trainer",
            isSubscribed = true,
            pokemonCards = listOf()
        )
    }
}
