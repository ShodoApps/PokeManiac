package com.shodo.android.myfriends.myfrienddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.myfriends.myfrienddetail.MyFriendDetailUiState.Data
import com.shodo.android.myfriends.myfrienddetail.MyFriendDetailUiState.Loading
import com.shodo.android.myfriends.uimodel.MyFriendUI
import com.shodo.android.myfriends.uimodel.mapToUI
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class MyFriendDetailUiState {
    data class Data(val friend: MyFriendUI) : MyFriendDetailUiState()
    data object Loading : MyFriendDetailUiState()
}

class MyFriendDetailViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _error = MutableSharedFlow<Exception>()
    val error = _error.asSharedFlow()

    private val _unsubscribed = MutableSharedFlow<Boolean>()
    val unsubscribed = _unsubscribed.asSharedFlow()

    private val _uiState: MutableStateFlow<MyFriendDetailUiState> = MutableStateFlow(Loading)
    val uiState: StateFlow<MyFriendDetailUiState> = _uiState.asStateFlow()

    private var detailJob: Job? = null

    fun start(id: String) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            _uiState.update { Loading }
            try {
                userRepository.getSubscribedUser(id).collect { user ->
                    user?.let {
                        _uiState.update { Data(user.mapToUI()) }
                    } ?: _unsubscribed.emit(true)
                }
            } catch (e: Exception) {
                _error.emit(e)
            }
        }
    }

    fun unsubscribeFriend(friendId: String) {
        viewModelScope.launch {
            try {
                userRepository.unsubscribeUser(friendId)
            } catch (e: Exception) {
                _error.emit(e)
            }
        }
    }
}