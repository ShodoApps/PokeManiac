package com.shodo.android.presentation.searchfriend

import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.domain.repositories.tracking.TrackingEventClick
import com.shodo.android.domain.repositories.tracking.TrackingEventScreen
import com.shodo.android.domain.repositories.tracking.TrackingRepository
import com.shodo.android.presentation.PresentationError
import com.shodo.android.presentation.presentationIoDispatcher
import com.shodo.android.presentation.searchfriend.SearchFriendUiState.Data
import com.shodo.android.presentation.searchfriend.SearchFriendUiState.EmptyResult
import com.shodo.android.presentation.searchfriend.SearchFriendUiState.EmptySearch
import com.shodo.android.presentation.searchfriend.SearchFriendUiState.Loading
import com.shodo.android.presentation.searchfriend.SubscriptionState.NotSubscribed
import com.shodo.android.presentation.searchfriend.SubscriptionState.Subscribed
import com.shodo.android.presentation.searchfriend.SubscriptionState.UpdatingSubscribe
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
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

/**
 * Search Friend **shared** screen logic (KMP `commonMain`).
 *
 * Named **ScreenModel** (not `ViewModel`) so the Android module can keep **`SearchFriendViewModel`**
 * as the AndroidX lifecycle type without duplicate class names. Same role as the “shared ViewModel”
 * described in `docs/kmp-migration-plan.md`: state, events, and flows with no Android dependency.
 *
 * On Android, Koin registers **`SearchFriendScreenModelFactory`** in the feature `di` module so the wrapper passes
 * **`viewModelScope`** at `create(...)` (see **`.cursor/rules/viewmodel-patterns.mdc`** — Shared ScreenModel + Koin).
 *
 * The platform supplies a [CoroutineScope] (on Android: `ViewModel.viewModelScope` via that factory).
 *
 * Exposes [uiState] for rendering and [error] for one-shot user messages ([PresentationError]).
 */
class SearchFriendScreenModel(
    private val userRepository: UserRepository,
    private val trackingRepository: TrackingRepository,
    private val coroutineScope: CoroutineScope
) {

    private val _uiState: MutableStateFlow<SearchFriendUiState> = MutableStateFlow(EmptySearch)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<PresentationError>()
    val error = _error.asSharedFlow()

    private var searchJob: Job? = null

    init {
        coroutineScope.launch {
            withContext(presentationIoDispatcher) {
                trackingRepository.sendEventScreen(TrackingEventScreen(TRACKING_SEARCH_SCREEN))
            }
        }
    }

    fun searchFriend(friendName: String) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            if (friendName.isEmpty() || friendName.isBlank()) {
                _uiState.update { EmptySearch }
            } else {
                _uiState.update { Loading }
                try {
                    userRepository.searchUsers(friendName).collectLatest { friends ->
                        val uiPeople = withContext(Dispatchers.Default) {
                            friends.map { it.mapToSearchFriendUiModel() }.toPersistentList()
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
                    _uiState.update { EmptyResult(friendName) }
                    _error.emit(PresentationError.from(e))
                }
            }
        }
    }

    fun subscribeFriend(friendId: String) = updateFriendSubscription(friendId = friendId, subscribe = true)

    fun unsubscribeFriend(friendId: String) = updateFriendSubscription(friendId = friendId, subscribe = false)

    private fun updateFriendSubscription(friendId: String, subscribe: Boolean) {
        val data = _uiState.value as? Data ?: return
        val friend = data.people.firstOrNull { it.id == friendId } ?: return
        coroutineScope.launch {
            _uiState.update {
                Data(
                    people = data.people.map { f ->
                        if (f.id == friendId) f.copy(subscriptionState = UpdatingSubscribe) else f
                    }.toPersistentList()
                )
            }
            try {
                withContext(presentationIoDispatcher) {
                    if (subscribe) {
                        trackingRepository.sendEventClick(TrackingEventClick(TRACKING_SUBSCRIBE_CLICK))
                        userRepository.subscribeUser(friend.mapToUser())
                    } else {
                        trackingRepository.sendEventClick(TrackingEventClick(TRACKING_UNSUBSCRIBE_CLICK))
                        userRepository.unsubscribeUser(friendId)
                    }
                }
                _uiState.update { currentState ->
                    (currentState as? Data)?.copy(
                        people = currentState.people.map { f ->
                            if (f.id == friendId) {
                                f.copy(subscriptionState = if (subscribe) Subscribed else NotSubscribed)
                            } else {
                                f
                            }
                        }.toPersistentList()
                    ) ?: currentState
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    (currentState as? Data)?.copy(
                        people = currentState.people.map { f ->
                            if (f.id == friendId) {
                                f.copy(subscriptionState = if (subscribe) NotSubscribed else Subscribed)
                            } else {
                                f
                            }
                        }.toPersistentList()
                    ) ?: currentState
                }
                _error.emit(PresentationError.from(e))
            }
        }
    }

    private companion object {
        private const val TRACKING_SEARCH_SCREEN = "TRACKING_SEARCH_SCREEN"
        private const val TRACKING_SUBSCRIBE_CLICK = "TRACKING_SUBSCRIBE_CLICK"
        private const val TRACKING_UNSUBSCRIBE_CLICK = "TRACKING_UNSUBSCRIBE_CLICK"
    }
}
