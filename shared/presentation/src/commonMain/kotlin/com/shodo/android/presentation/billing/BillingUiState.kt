package com.shodo.android.presentation.billing

sealed class BillingUiState {
    data class Data(val state: Int) : BillingUiState()
    data object Error : BillingUiState()
    data object Loading : BillingUiState()
}
