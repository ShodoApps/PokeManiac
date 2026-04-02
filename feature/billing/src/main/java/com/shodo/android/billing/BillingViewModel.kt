package com.shodo.android.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.billing.BillingUiState.Loading
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed class BillingUiState {
    data class Data(val state: Int) : BillingUiState()
    data object Error : BillingUiState()
    data object Loading : BillingUiState()
}

class BillingViewModel() : ViewModel() {

    private val _error = MutableSharedFlow<Exception>()
    val error = _error.asSharedFlow()

    private val _uiState: MutableStateFlow<BillingUiState> = MutableStateFlow(Loading)
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    fun start() {
        viewModelScope.launch {
            _uiState.update { Loading }
        }
    }
}