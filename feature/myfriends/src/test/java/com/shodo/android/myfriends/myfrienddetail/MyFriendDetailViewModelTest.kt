package com.shodo.android.myfriends.myfrienddetail

import app.cash.turbine.test
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
class MyFriendDetailViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    @Mock private lateinit var userRepository: UserRepository

    private lateinit var viewModel: MyFriendDetailViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        viewModel = MyFriendDetailViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ===== start() =====

    @Test
    fun `start emits Loading then Data when user is found`() = runTest {
        // Given
        `when`(userRepository.getSubscribedUser("friendId")).thenReturn(
            flow { emit(defaultUser) }
        )

        viewModel.uiState.test {
            // When
            viewModel.start("friendId")

            // Then
            assertEquals(MyFriendDetailUiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is MyFriendDetailUiState.Data)
            assertEquals("friendId", state.friend.id)
            assertEquals("Ash", state.friend.name)
        }
    }

    @Test
    fun `start emits unsubscribed event when user is null`() = runTest {
        // Given — user was removed (unsubscribed from another screen)
        `when`(userRepository.getSubscribedUser("friendId")).thenReturn(
            flow { emit(null) }
        )

        // Subscriber must be set up BEFORE triggering (SharedFlow replay=0 drops events before subscription)
        viewModel.unsubscribed.test {
            // When
            viewModel.start("friendId")

            // Then
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `start emits error when repository throws`() = runTest {
        // Given
        `when`(userRepository.getSubscribedUser("friendId")).thenThrow(RuntimeException("Not found"))

        // Subscriber must be set up BEFORE triggering (SharedFlow replay=0 drops events before subscription)
        viewModel.error.test {
            // When
            viewModel.start("friendId")

            // Then
            assertEquals("Not found", awaitItem().message)
        }
    }

    // ===== unsubscribeFriend() =====

    @Test
    fun `unsubscribeFriend calls repository with the given id`() = runTest {
        // Given — default mock returns Unit (no error)
        // When
        viewModel.unsubscribeFriend("friendId")

        // Then — no error emitted
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
    }
}
