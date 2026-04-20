package com.shodo.android.presentation.welcome

sealed class WelcomeUiState {
    data object Idle : WelcomeUiState()
}
