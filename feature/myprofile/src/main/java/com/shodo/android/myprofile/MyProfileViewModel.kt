package com.shodo.android.myprofile

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.coreui.UiError
import com.shodo.android.coreui.navigator.BillingNavigator
import com.shodo.android.coreui.navigator.PostTransactionNavigator
import com.shodo.android.domain.repositories.myprofile.MyProfileRepository
import com.shodo.android.myprofile.MyProfileUiState.Loading
import com.shodo.android.myprofile.uimodel.MyProfileUI
import com.shodo.android.myprofile.uimodel.mapToMyProfileUI
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
sealed class MyProfileUiState {
    data object Loading: MyProfileUiState()
    data class Data(val profile: MyProfileUI): MyProfileUiState()
}

class MyProfileViewModel(
    private val myProfileRepository: MyProfileRepository,
    private val postTransactionNavigator: PostTransactionNavigator,
    private val billingNavigator: BillingNavigator
): ViewModel() {

    private val _uiState: MutableStateFlow<MyProfileUiState> = MutableStateFlow(Loading)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<UiError>()
    val error = _error.asSharedFlow()

    private var activitiesJob: Job? = null

    fun start() {
        activitiesJob?.cancel()
        activitiesJob = viewModelScope.launch {
            _uiState.update { Loading }
            try {
                myProfileRepository.getMyActivities().collect { myActivities ->
                    val profile = withContext(Dispatchers.Default) {
                        myActivities.mapToMyProfileUI()
                    }
                    _uiState.update { MyProfileUiState.Data(profile = profile) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.emit(UiError.from(e))
            }
        }
    }

    fun navigateToPostTransaction(context: Context) {
        postTransactionNavigator.navigate(context)
    }
    fun navigateToBilling(context: Context) {
        billingNavigator.navigate(context)
    }
}
