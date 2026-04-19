package com.shodo.android.posttransaction.step2

import android.net.Uri
import app.cash.turbine.test
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.news.NewsFeedRepository
import com.shodo.android.posttransaction.di.PostTransactionStep2ScreenModelFactory
import com.shodo.android.presentation.posttransaction.PostTransactionStep2UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class PostTransactionStep2ViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    private val mockUri: Uri = mock(Uri::class.java)

    private val failingRepository = object : NewsFeedRepository {
        override fun getNewActivities(): Flow<List<NewActivity>> = flow { emit(emptyList()) }
        override suspend fun saveNewActivity(newActivity: NewActivity) {
            throw RuntimeException("Save failed")
        }
    }

    private val successRepository = object : NewsFeedRepository {
        override fun getNewActivities(): Flow<List<NewActivity>> = flow { emit(emptyList()) }
        override suspend fun saveNewActivity(newActivity: NewActivity) = Unit
    }

    private fun factoryFor(repo: NewsFeedRepository) = PostTransactionStep2ScreenModelFactory { scope ->
        com.shodo.android.presentation.posttransaction.PostTransactionStep2ScreenModel(repo, scope)
    }

    @Before
    fun setUp() {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveActivity emits success=true on repository success`() = runTest {
        // Given
        val viewModel = PostTransactionStep2ViewModel(factoryFor(successRepository))

        // Subscriber must be set up BEFORE triggering (SharedFlow replay=0 drops events before subscription)
        viewModel.success.test {
            // When
            viewModel.saveActivity("Pikachu", 25, NewActivityType.Purchase, 50, mockUri)

            // Then
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `saveActivity transitions to Loading while saving`() = runTest {
        // Given
        val viewModel = PostTransactionStep2ViewModel(factoryFor(successRepository))

        viewModel.uiState.test {
            // Then — initial
            assertEquals(PostTransactionStep2UiState.Filling, awaitItem())

            // When
            viewModel.saveActivity("Pikachu", 25, NewActivityType.Purchase, 50, mockUri)

            // Then — Loading while saving
            assertEquals(PostTransactionStep2UiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveActivity emits error and reverts to Filling on repository failure`() = runTest {
        // Given
        val viewModel = PostTransactionStep2ViewModel(factoryFor(failingRepository))

        viewModel.error.test {
            // When
            viewModel.saveActivity("Pikachu", 25, NewActivityType.Purchase, 50, mockUri)

            // Then — error emitted
            assertEquals("Save failed", awaitItem().message)
        }

        // Then — state reverted to Filling
        assertEquals(PostTransactionStep2UiState.Filling, viewModel.uiState.value)
    }

    @Test
    fun `saveActivity does not emit success on repository failure`() = runTest {
        // Given
        val viewModel = PostTransactionStep2ViewModel(factoryFor(failingRepository))

        // When
        viewModel.saveActivity("Pikachu", 25, NewActivityType.Purchase, 50, mockUri)

        // Then — no success event
        viewModel.success.test {
            ensureAllEventsConsumed()
        }
    }
}
