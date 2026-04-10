package com.shodo.android.billing

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class BillingViewModelTest {

    private lateinit var viewModel: BillingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = BillingViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.uiState.test {
            assertEquals(BillingUiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `start does not emit error`() = runTest {
        // When
        viewModel.start()

        // Then — no error emitted
        viewModel.error.test {
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `start keeps state as Loading`() = runTest {
        viewModel.uiState.test {
            // Initial Loading already consumed
            assertEquals(BillingUiState.Loading, awaitItem())

            // When — start() sets Loading on an already-Loading StateFlow
            // StateFlow suppresses duplicate values, so no new emission is expected
            viewModel.start()

            ensureAllEventsConsumed()
        }
    }
}
