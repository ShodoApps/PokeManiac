package com.shodo.android.billing

import app.cash.turbine.test
import com.shodo.android.billing.di.BillingScreenModelFactory
import com.shodo.android.presentation.billing.BillingScreenModel
import com.shodo.android.presentation.billing.BillingUiState
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

    private val screenModelFactory = BillingScreenModelFactory { scope ->
        BillingScreenModel(coroutineScope = scope)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = BillingViewModel(screenModelFactory)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Given — fresh ViewModel (see setUp)
        viewModel.uiState.test {
            // When / Then — first emission
            assertEquals(BillingUiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `start does not emit error`() = runTest {
        // Given — no error path in billing flow today
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
            // Given — initial Loading
            assertEquals(BillingUiState.Loading, awaitItem())

            // When — start() sets Loading on an already-Loading StateFlow
            // StateFlow suppresses duplicate values, so no new emission is expected
            viewModel.start()

            // Then
            ensureAllEventsConsumed()
        }
    }
}
