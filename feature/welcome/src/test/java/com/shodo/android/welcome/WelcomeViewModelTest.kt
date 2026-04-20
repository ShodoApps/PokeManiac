package com.shodo.android.welcome

import app.cash.turbine.test
import com.shodo.android.presentation.welcome.WelcomeUiEvent
import com.shodo.android.presentation.welcome.WelcomeUiState
import com.shodo.android.welcome.di.WelcomeScreenModelFactory
import com.shodo.android.presentation.welcome.WelcomeScreenModel
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
class WelcomeViewModelTest {

    private lateinit var viewModel: WelcomeViewModel

    private val screenModelFactory = WelcomeScreenModelFactory { scope ->
        WelcomeScreenModel(coroutineScope = scope)
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = WelcomeViewModel(screenModelFactory)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState is Idle`() = runTest {
        // Given — fresh ViewModel (see setUp)
        viewModel.uiState.test {
            // When / Then — first emission
            assertEquals(WelcomeUiState.Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSignUpClicked emits ShowMessage`() = runTest {
        // Given
        viewModel.uiEvent.test {
            // When
            viewModel.onSignUpClicked()

            // Then
            val event = awaitItem()
            assertTrue(event is WelcomeUiEvent.ShowMessage)
            assertEquals("Nothing implemented yet", (event as WelcomeUiEvent.ShowMessage).message.message)
        }
    }

    @Test
    fun `onSignInClicked emits NavigateToDashboard`() = runTest {
        // Given
        viewModel.uiEvent.test {
            // When
            viewModel.onSignInClicked()

            // Then
            assertEquals(WelcomeUiEvent.NavigateToDashboard, awaitItem())
        }
    }
}
