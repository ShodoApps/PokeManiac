package com.shodo.android.presentation.myfriends

import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.presentation.PresentationError
import com.shodo.android.presentation.presentationIoDispatcher
import com.shodo.android.presentation.myfriends.MyFriendDetailUiState.Data
import com.shodo.android.presentation.myfriends.MyFriendDetailUiState.Loading
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * My Friend detail **shared** screen logic (KMP `commonMain`).
 *
 * On Android, Koin registers [MyFriendDetailScreenModelFactory] so the AndroidX [ViewModel] passes
 * [androidx.lifecycle.ViewModel.viewModelScope] at [MyFriendDetailScreenModelFactory.create].
 */
class MyFriendDetailScreenModel(
    private val userRepository: UserRepository,
    private val coroutineScope: CoroutineScope,
) {

    private val _error = MutableSharedFlow<PresentationError>()
    val error = _error.asSharedFlow()

    private val _unsubscribed = MutableSharedFlow<Boolean>()
    val unsubscribed = _unsubscribed.asSharedFlow()

    private val _uiState: MutableStateFlow<MyFriendDetailUiState> = MutableStateFlow(Loading)
    val uiState: StateFlow<MyFriendDetailUiState> = _uiState.asStateFlow()

    private var detailJob: Job? = null

    fun start(id: String) {
        detailJob?.cancel()
        detailJob = coroutineScope.launch {
            _uiState.update { Loading }
            try {
                userRepository.getSubscribedUser(id).collect { user ->
                    if (user != null) {
                        val uiModel = user.mapToMyFriendUiModel()
                        _uiState.update { Data(uiModel) }
                    } else {
                        _unsubscribed.emit(true)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.emit(PresentationError.from(e))
            }
        }
    }

    fun unsubscribeFriend(friendId: String) {
        coroutineScope.launch {
            try {
                withContext(presentationIoDispatcher) { userRepository.unsubscribeUser(friendId) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.emit(PresentationError.from(e))
            }
        }
    }
}
