package com.shodo.android.searchfriend

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.coreui.UiError
import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.domain.repositories.tracking.TrackingEventClick
import com.shodo.android.domain.repositories.tracking.TrackingEventScreen
import com.shodo.android.domain.repositories.tracking.TrackingRepository
import com.shodo.android.searchfriend.SearchFriendUiState.Data
import com.shodo.android.searchfriend.SearchFriendUiState.EmptyResult
import com.shodo.android.searchfriend.SearchFriendUiState.EmptySearch
import com.shodo.android.searchfriend.SearchFriendUiState.Loading
import com.shodo.android.searchfriend.uimodel.SearchFriendUI
import com.shodo.android.searchfriend.uimodel.SubscriptionState.NotSubscribed
import com.shodo.android.searchfriend.uimodel.SubscriptionState.Subscribed
import com.shodo.android.searchfriend.uimodel.SubscriptionState.UpdatingSubscribe
import com.shodo.android.searchfriend.uimodel.mapToSearchFriendUI
import com.shodo.android.searchfriend.uimodel.mapToUser
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Immutable
sealed class SearchFriendUiState {
    data object EmptySearch : SearchFriendUiState()
    data object Loading : SearchFriendUiState()
    data class Data(val people: PersistentList<SearchFriendUI>) : SearchFriendUiState()
    data class EmptyResult(val query: String) : SearchFriendUiState()
}

class SearchFriendViewModel(
    private val userRepository: UserRepository,
    private val trackingRepository: TrackingRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SearchFriendUiState> = MutableStateFlow(EmptySearch)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<UiError>()
    val error = _error.asSharedFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                trackingRepository.sendEventScreen(TrackingEventScreen(TRACKING_SEARCH_SCREEN))
            }
        }
    }

    fun searchFriend(friendName: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (friendName.isEmpty() || friendName.isBlank()) {
                _uiState.update { EmptySearch }
            } else {
                _uiState.update { Loading }
                try {
                    userRepository.searchUsers(friendName).collectLatest { friends ->
                        val uiPeople = withContext(Dispatchers.Default) {
                            friends.map { it.mapToSearchFriendUI() }.toPersistentList()
                        }
                        if (uiPeople.isNotEmpty()) {
                            _uiState.update { Data(people = uiPeople) }
                        } else {
                            _uiState.update { EmptyResult(friendName) }
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _error.emit(UiError.from(e))
                }
            }
        }
    }

    fun subscribeFriend(friendId: String) = updateFriendSubscription(friendId = friendId, subscribe = true)
    fun unsubscribeFriend(friendId: String) = updateFriendSubscription(friendId = friendId, subscribe = false)

    private fun updateFriendSubscription(friendId: String, subscribe: Boolean) {
        val data = _uiState.value as? Data ?: return
        val friend = data.people.firstOrNull { it.id == friendId } ?: return
        viewModelScope.launch {
            // 1. Optimistically set the friend's subscription to UpdatingSubscribe
            _uiState.update {
                Data(people = data.people.map { f ->
                    if (f.id == friendId) f.copy(subscriptionState = UpdatingSubscribe) else f
                }.toPersistentList())
            }
            try {
                // 2. Perform suspend operations OUTSIDE update{}, on IO dispatcher
                withContext(Dispatchers.IO) {
                    if (subscribe) {
                        trackingRepository.sendEventClick(TrackingEventClick(TRACKING_SUBSCRIBE_CLICK))
                        userRepository.subscribeUser(friend.mapToUser())
                    } else {
                        trackingRepository.sendEventClick(TrackingEventClick(TRACKING_UNSUBSCRIBE_CLICK))
                        userRepository.unsubscribeUser(friendId)
                    }
                }
                // 3. Update state with final subscription state
                _uiState.update { currentState ->
                    (currentState as? Data)?.copy(
                        people = currentState.people.map { f ->
                            if (f.id == friendId) f.copy(subscriptionState = if (subscribe) Subscribed else NotSubscribed)
                            else f
                        }.toPersistentList()
                    ) ?: currentState
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Revert to previous subscription state on failure
                _uiState.update { currentState ->
                    (currentState as? Data)?.copy(
                        people = currentState.people.map { f ->
                            if (f.id == friendId) f.copy(subscriptionState = if (subscribe) NotSubscribed else Subscribed)
                            else f
                        }.toPersistentList()
                    ) ?: currentState
                }
                _error.emit(UiError.from(e))
            }
        }
    }

    companion object {
        private const val TRACKING_SEARCH_SCREEN = "TRACKING_SEARCH_SCREEN"
        private const val TRACKING_SUBSCRIBE_CLICK = "TRACKING_SUBSCRIBE_CLICK"
        private const val TRACKING_UNSUBSCRIBE_CLICK = "TRACKING_UNSUBSCRIBE_CLICK"
    }
}
