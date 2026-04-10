package com.shodo.android.myprofile

import app.cash.turbine.test
import com.shodo.android.coreui.navigator.BillingNavigator
import com.shodo.android.coreui.navigator.PostTransactionNavigator
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import com.shodo.android.domain.repositories.myprofile.MyProfileRepository
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
class MyProfileViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    @Mock private lateinit var myProfileRepository: MyProfileRepository
    @Mock private lateinit var postTransactionNavigator: PostTransactionNavigator
    @Mock private lateinit var billingNavigator: BillingNavigator

    private lateinit var viewModel: MyProfileViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        viewModel = MyProfileViewModel(myProfileRepository, postTransactionNavigator, billingNavigator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ===== start() =====

    @Test
    fun `start emits Loading then Data with mapped FileSource cards`() = runTest {
        // Given — activity with FileSource card (kept by the mapper)
        `when`(myProfileRepository.getMyActivities()).thenReturn(
            flow { emit(listOf(activityWithFileSource("content://media/pikachu.jpg", "Pikachu", LocalDateTime.of(2024, 6, 10, 12, 0)))) }
        )

        viewModel.uiState.test {
            // When
            viewModel.start()

            // Then
            assertEquals(MyProfileUiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is MyProfileUiState.Data)
            assertEquals(1, state.profile.pokemonCards.size)
            assertEquals("Pikachu", state.profile.pokemonCards[0].name)
        }
    }

    @Test
    fun `start emits Data with empty cards when all activities have UrlSource cards`() = runTest {
        // Given — UrlSource cards are filtered out by the mapper (only FileSource is kept)
        `when`(myProfileRepository.getMyActivities()).thenReturn(
            flow { emit(listOf(activityWithUrlSource("Charmander"))) }
        )

        viewModel.uiState.test {
            // When
            viewModel.start()

            // Then — Data is still emitted, but with 0 cards
            assertEquals(MyProfileUiState.Loading, awaitItem())
            val state = awaitItem()
            assertTrue(state is MyProfileUiState.Data)
            assertEquals(0, state.profile.pokemonCards.size)
        }
    }

    @Test
    fun `start emits cards sorted by most recent date first`() = runTest {
        // Given — two activities with different dates (older Pikachu, newer Charizard)
        val older = activityWithFileSource("content://media/pikachu.jpg", "Pikachu", LocalDateTime.of(2024, 1, 1, 10, 0))
        val newer = activityWithFileSource("content://media/charizard.jpg", "Charizard", LocalDateTime.of(2024, 12, 31, 23, 59))

        `when`(myProfileRepository.getMyActivities()).thenReturn(
            flow { emit(listOf(older, newer)) }
        )

        viewModel.uiState.test {
            // When
            viewModel.start()

            // Then — most recent (Charizard) appears first
            skipItems(1) // Loading
            val state = awaitItem() as MyProfileUiState.Data
            assertEquals("Charizard", state.profile.pokemonCards[0].name)
            assertEquals("Pikachu", state.profile.pokemonCards[1].name)
        }
    }

    @Test
    fun `start emits error when repository throws`() = runTest {
        // Given
        `when`(myProfileRepository.getMyActivities()).thenThrow(RuntimeException("Storage error"))

        // Subscriber must be set up BEFORE triggering (SharedFlow replay=0 drops events before subscription)
        viewModel.error.test {
            // When
            viewModel.start()

            // Then
            assertEquals("Storage error", awaitItem().message)
        }
    }

    // ===== Helpers =====

    private fun activityWithFileSource(fileUri: String, cardName: String, date: LocalDateTime) = NewActivity(
        userName = "Ash",
        userImageUrl = null,
        date = date,
        pokemonCard = UserPokemonCard(
            pokemonId = 25,
            name = cardName,
            imageSource = ImageSource.FileSource(fileUri),
            totalVotes = 5,
            hasMyVote = true
        ),
        activityType = NewActivityType.Purchase,
        price = 30
    )

    private fun activityWithUrlSource(cardName: String) = NewActivity(
        userName = "Ash",
        userImageUrl = null,
        date = LocalDateTime.of(2024, 6, 11, 14, 0),
        pokemonCard = UserPokemonCard(
            pokemonId = 4,
            name = cardName,
            imageSource = ImageSource.UrlSource("https://image.url/$cardName.jpg"),
            totalVotes = 2,
            hasMyVote = false
        ),
        activityType = NewActivityType.Sale,
        price = 20
    )
}
