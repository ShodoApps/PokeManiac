package com.shodo.android.presentation.welcome

import com.shodo.android.presentation.PresentationError

sealed class WelcomeUiEvent {
    data class ShowMessage(val message: PresentationError) : WelcomeUiEvent()
    data object NavigateToDashboard : WelcomeUiEvent()
}
