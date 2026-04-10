package com.shodo.android.dashboard

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.coreui.UiError
import com.shodo.android.coreui.navigator.MyFriendsNavigator
import com.shodo.android.coreui.navigator.MyProfileNavigator
import com.shodo.android.coreui.navigator.PostTransactionNavigator
import com.shodo.android.coreui.navigator.SearchFriendNavigator
import com.shodo.android.dashboard.DashboardUiState.EmptyResult
import com.shodo.android.dashboard.DashboardUiState.Loading
import com.shodo.android.dashboard.uimodel.NewActivityUI
import com.shodo.android.dashboard.uimodel.mapToUI
import com.shodo.android.domain.repositories.news.NewsFeedRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
sealed class DashboardUiState {
    data class Data(val news: PersistentList<NewActivityUI>) : DashboardUiState()
    data object EmptyResult : DashboardUiState()
    data object Loading : DashboardUiState()
}

class DashboardViewModel(
    private val newsFeedRepository: NewsFeedRepository,
    private val searchFriendNavigator: SearchFriendNavigator,
    private val myFriendsNavigator: MyFriendsNavigator,
    private val myProfileNavigator: MyProfileNavigator,
    private val postTransactionNavigator: PostTransactionNavigator
) : ViewModel() {

    private val _error = MutableSharedFlow<UiError>()
    val error = _error.asSharedFlow()

    private val _uiState: MutableStateFlow<DashboardUiState> = MutableStateFlow(Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var newsFeedJob: Job? = null

    fun start() {
        newsFeedJob?.cancel()
        newsFeedJob = viewModelScope.launch {
            _uiState.update { Loading }
            fetchNewActivities()
        }
    }

    fun refreshNewsFeed() {
        newsFeedJob?.cancel()
        newsFeedJob = viewModelScope.launch {
            _uiState.update { Loading }
            try {
                delay(2000) // Mock Delay to show the loading state
                fetchNewActivities()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.emit(UiError.from(e))
            }
        }
    }

    fun navigateToSearchFriends(context: Context) {
        searchFriendNavigator.navigate(context)
    }
    fun navigateToMyFriends(context: Context) {
        myFriendsNavigator.navigate(context)
    }
    fun navigateToMyProfile(context: Context) {
        myProfileNavigator.navigate(context)
    }
    fun navigateToPostTransaction(context: Context) {
        postTransactionNavigator.navigate(context)
    }

    private suspend fun fetchNewActivities() {
        try {
            newsFeedRepository.getNewActivities().collect { result ->
                if (result.isNotEmpty()) {
                    val uiItems = withContext(Dispatchers.Default) {
                        result.map { it.mapToUI() }.toPersistentList()
                    }
                    _uiState.update { DashboardUiState.Data(uiItems) }
                } else {
                    _uiState.update { EmptyResult }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _error.emit(UiError.from(e))
            _uiState.update { EmptyResult }
        }
    }
}
