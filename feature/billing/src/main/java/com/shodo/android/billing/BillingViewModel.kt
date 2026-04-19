package com.shodo.android.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.billing.di.BillingScreenModelFactory
import com.shodo.android.presentation.billing.BillingScreenModel

/**
 * AndroidX shell; logic in [BillingScreenModel] (`:shared:presentation`).
 */
class BillingViewModel(
    screenModelFactory: BillingScreenModelFactory
) : ViewModel() {

    private val screenModel: BillingScreenModel by lazy {
        screenModelFactory.create(viewModelScope)
    }

    val uiState get() = screenModel.uiState
    val error get() = screenModel.error

    fun start() = screenModel.start()
}
