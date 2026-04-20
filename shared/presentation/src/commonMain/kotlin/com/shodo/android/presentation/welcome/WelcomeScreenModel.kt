package com.shodo.android.presentation.welcome

import com.shodo.android.presentation.PresentationError
import com.shodo.android.presentation.welcome.WelcomeUiState.Idle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WelcomeScreenModel(
    private val coroutineScope: CoroutineScope,
) {

    private val _uiState: MutableStateFlow<WelcomeUiState> = MutableStateFlow(Idle)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<WelcomeUiEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    fun onSignUpClicked() {
        coroutineScope.launch {
            _uiEvent.emit(
                WelcomeUiEvent.ShowMessage(PresentationError("Nothing implemented yet"))
            )
        }
    }

    fun onSignInClicked() {
        coroutineScope.launch {
            _uiEvent.emit(WelcomeUiEvent.NavigateToDashboard)
        }
    }
}
