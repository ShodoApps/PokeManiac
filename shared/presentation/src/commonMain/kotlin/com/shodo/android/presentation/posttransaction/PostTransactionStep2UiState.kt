package com.shodo.android.presentation.posttransaction

sealed class PostTransactionStep2UiState {
    data object Filling : PostTransactionStep2UiState()
    data object Loading : PostTransactionStep2UiState()
}
