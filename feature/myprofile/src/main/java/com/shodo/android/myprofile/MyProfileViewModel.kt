package com.shodo.android.myprofile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.coreui.navigator.BillingNavigator
import com.shodo.android.coreui.navigator.PostTransactionNavigator
import com.shodo.android.myprofile.di.MyProfileScreenModelFactory
import com.shodo.android.presentation.myprofile.MyProfileScreenModel

/**
 * AndroidX shell; logic in [MyProfileScreenModel] (`:shared:presentation`).
 */
class MyProfileViewModel(
    private val screenModelFactory: MyProfileScreenModelFactory,
    private val postTransactionNavigator: PostTransactionNavigator,
    private val billingNavigator: BillingNavigator
) : ViewModel() {

    private val screenModel: MyProfileScreenModel by lazy {
        screenModelFactory.create(viewModelScope)
    }

    val uiState get() = screenModel.uiState
    val error get() = screenModel.error

    fun start() = screenModel.start()

    fun navigateToPostTransaction(context: Context) {
        postTransactionNavigator.navigate(context)
    }

    fun navigateToBilling(context: Context) {
        billingNavigator.navigate(context)
    }
}
