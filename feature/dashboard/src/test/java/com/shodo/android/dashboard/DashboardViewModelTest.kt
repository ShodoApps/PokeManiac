package com.shodo.android.dashboard

import app.cash.turbine.test
import com.shodo.android.coreui.navigator.MyFriendsNavigator
import com.shodo.android.coreui.navigator.MyProfileNavigator
import com.shodo.android.coreui.navigator.PostTransactionNavigator
import com.shodo.android.coreui.navigator.SearchFriendNavigator
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import com.shodo.android.domain.repositories.news.NewsFeedRepository
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
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class DashboardViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    @Mock private lateinit var newsFeedRepository: NewsFeedRepository
    @Mock private lateinit var searchFriendNavigator: SearchFriendNavigator
    @Mock private lateinit var myFriendsNavigator: MyFriendsNavigator
    @Mock private lateinit var myProfileNavigator: MyProfileNavigator
    @Mock private lateinit var postTransactionNavigator: PostTransactionNavigator

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        viewModel = DashboardViewModel(
            newsFeedRepository,
            searchFriendNavigator,
            myFriendsNavigator,
            myProfileNavigator,
            postTransactionNavigator
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ===== start() =====

    @Test
    fun `start emits Loading then Data when repository returns activities`() = runTest {
        // Given
        `when`(newsFeedRepository.getNewActivities()).thenReturn(
            flow { emit(listOf(defaultNewActivity)) }
        )

        viewModel.uiState.test {
            // When
            viewModel.start()

            // Then
            assertEquals(DashboardUiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is DashboardUiState.Data)
            assertEquals(1, state.news.size)
        }
    }

    @Test
    fun `start emits Loading then EmptyResult when repository returns empty list`() = runTest {
        // Given
        `when`(newsFeedRepository.getNewActivities()).thenReturn(
            flow { emit(emptyList()) }
        )

        viewModel.uiState.test {
            // When
            viewModel.start()

            // Then
            assertEquals(DashboardUiState.Loading, awaitItem())
            assertEquals(DashboardUiState.EmptyResult, awaitItem())
        }
    }

    @Test
    fun `start emits error and EmptyResult when repository throws`() = runTest {
        // Given
        `when`(newsFeedRepository.getNewActivities()).thenThrow(RuntimeException("Network error"))

        // Subscriber must be set up BEFORE triggering (SharedFlow replay=0 drops events before subscription)
        viewModel.error.test {
            // When
            viewModel.start()

            // Then
            assertEquals("Network error", awaitItem().message)
        }
        assertTrue(viewModel.uiState.value is DashboardUiState.EmptyResult)
    }

    // ===== refreshNewsFeed() =====

    @Test
    fun `refreshNewsFeed emits Loading then Data after delay`() = runTest {
        // Given
        `when`(newsFeedRepository.getNewActivities()).thenReturn(
            flow { emit(listOf(defaultNewActivity)) }
        )

        viewModel.uiState.test {
            // When
            viewModel.refreshNewsFeed()

            // Then — Loading first, then Data (delay is auto-skipped with UnconfinedTestDispatcher)
            assertEquals(DashboardUiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is DashboardUiState.Data)
        }
    }

    @Test
    fun `refreshNewsFeed emits error when repository throws`() = runTest {
        // Given
        `when`(newsFeedRepository.getNewActivities()).thenThrow(RuntimeException("Timeout"))

        // Subscriber must be set up BEFORE triggering (SharedFlow replay=0 drops events before subscription)
        viewModel.error.test {
            // When
            viewModel.refreshNewsFeed()

            // Then
            assertEquals("Timeout", awaitItem().message)
        }
    }

    companion object {
        private val defaultNewActivity = NewActivity(
            userName = "Ash",
            userImageUrl = "https://image.url/ash.jpg",
            date = LocalDateTime.of(2024, 1, 15, 10, 30),
            pokemonCard = UserPokemonCard(
                pokemonId = 25,
                name = "Pikachu",
                imageSource = ImageSource.UrlSource("https://image.url/pikachu.jpg"),
                totalVotes = 10,
                hasMyVote = false
            ),
            activityType = NewActivityType.Purchase,
            price = 50
        )
    }
}
