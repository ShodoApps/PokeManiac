package com.shodo.android.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.presentation.welcome.WelcomeScreenModel
import com.shodo.android.welcome.di.WelcomeScreenModelFactory

/**
 * AndroidX shell; logic in [WelcomeScreenModel] (`:shared:presentation`).
 */
class WelcomeViewModel(
    screenModelFactory: WelcomeScreenModelFactory
) : ViewModel() {

    private val screenModel: WelcomeScreenModel by lazy {
        screenModelFactory.create(viewModelScope)
    }

    val uiState get() = screenModel.uiState
    val uiEvent get() = screenModel.uiEvent

    fun onSignUpClicked() = screenModel.onSignUpClicked()

    fun onSignInClicked() = screenModel.onSignInClicked()
}
