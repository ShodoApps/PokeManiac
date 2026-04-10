package com.shodo.android.posttransaction.step2

import android.net.Uri
import app.cash.turbine.test
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.news.NewsFeedRepository
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

    // Uri is an Android type — Mockito.mock() creates a safe JVM proxy without invoking Android stubs
    private val mockUri: Uri = mock(Uri::class.java)

    // Inline fake used for the error path: Kotlin objects are cleaner than Mockito for suspend funs
    private val failingRepository = object : NewsFeedRepository {
        override fun getNewActivities(): Flow<List<NewActivity>> = flow { emit(emptyList()) }
        override suspend fun saveNewActivity(newActivity: NewActivity) {
            throw RuntimeException("Save failed")
        }
    }

    // Inline fake for the success path: does nothing (saveNewActivity returns Unit)
    private val successRepository = object : NewsFeedRepository {
        override fun getNewActivities(): Flow<List<NewActivity>> = flow { emit(emptyList()) }
        override suspend fun saveNewActivity(newActivity: NewActivity) = Unit
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

    // ===== saveActivity() — success path =====

    @Test
    fun `saveActivity emits success=true on repository success`() = runTest {
        val viewModel = PostTransactionStep2ViewModel(successRepository)

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
        val viewModel = PostTransactionStep2ViewModel(successRepository)

        viewModel.uiState.test {
            assertEquals(PostTransactionStep2UiState.Filling, awaitItem()) // initial

            // When
            viewModel.saveActivity("Pikachu", 25, NewActivityType.Purchase, 50, mockUri)

            // Then — Loading is emitted during the save
            assertEquals(PostTransactionStep2UiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== saveActivity() — error path =====

    @Test
    fun `saveActivity emits UiError and reverts to Filling on repository failure`() = runTest {
        val viewModel = PostTransactionStep2ViewModel(failingRepository)

        viewModel.error.test {
            // When
            viewModel.saveActivity("Pikachu", 25, NewActivityType.Purchase, 50, mockUri)

            // Then — error emitted
            assertEquals("Save failed", awaitItem().message)
        }

        // State reverted to Filling
        assertEquals(PostTransactionStep2UiState.Filling, viewModel.uiState.value)
    }

    @Test
    fun `saveActivity does not emit success on repository failure`() = runTest {
        val viewModel = PostTransactionStep2ViewModel(failingRepository)

        // When
        viewModel.saveActivity("Pikachu", 25, NewActivityType.Purchase, 50, mockUri)

        // Then — no success event
        viewModel.success.test {
            ensureAllEventsConsumed()
        }
    }
}
