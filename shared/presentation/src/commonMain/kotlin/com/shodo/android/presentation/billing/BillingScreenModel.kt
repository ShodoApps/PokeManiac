package com.shodo.android.presentation.billing

import com.shodo.android.presentation.PresentationError
import com.shodo.android.presentation.billing.BillingUiState.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BillingScreenModel(
    private val coroutineScope: CoroutineScope,
) {

    private val _error = MutableSharedFlow<PresentationError>()
    val error = _error.asSharedFlow()

    private val _uiState: MutableStateFlow<BillingUiState> = MutableStateFlow(Loading)
    val uiState = _uiState.asStateFlow()

    fun start() {
        coroutineScope.launch {
            _uiState.update { Loading }
        }
    }
}
