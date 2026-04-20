package com.shodo.android.presentation.myfriends

import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.presentation.PresentationError
import com.shodo.android.presentation.presentationIoDispatcher
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * My Friends list **shared** screen logic (KMP `commonMain`).
 *
 * On Android, Koin registers [MyFriendListScreenModelFactory] so the AndroidX [ViewModel] passes
 * [androidx.lifecycle.ViewModel.viewModelScope] at [MyFriendListScreenModelFactory.create].
 */
class MyFriendListScreenModel(
    private val userRepository: UserRepository,
    private val coroutineScope: CoroutineScope,
) {

    private val _uiState: MutableStateFlow<MyFriendListUiState> = MutableStateFlow(MyFriendListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<PresentationError>()
    val error = _error.asSharedFlow()

    private var friendsJob: Job? = null

    fun fetchMyFriends() {
        friendsJob?.cancel()
        friendsJob = coroutineScope.launch {
            _uiState.update { MyFriendListUiState.Loading }
            try {
                userRepository.getSubscribedUsers().collect { friends ->
                    if (friends.isNotEmpty()) {
                        val uiFriends = withContext(Dispatchers.Default) {
                            friends.map { it.mapToMyFriendUiModel() }.toPersistentList()
                        }
                        _uiState.update { MyFriendListUiState.Data(friends = uiFriends) }
                    } else {
                        _uiState.update { MyFriendListUiState.Empty }
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
