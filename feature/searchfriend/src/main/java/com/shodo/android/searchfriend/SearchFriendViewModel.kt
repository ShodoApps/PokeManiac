package com.shodo.android.searchfriend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.presentation.searchfriend.SearchFriendScreenModel
import com.shodo.android.searchfriend.di.SearchFriendScreenModelFactory

/**
 * AndroidX [ViewModel] for Search Friend (Koin + Compose).
 *
 * Resolves [SearchFriendScreenModel] via [SearchFriendScreenModelFactory] with [viewModelScope] so the shared coordinator
 * is lifecycle-safe (Phase E — Koin + explicit [CoroutineScope]).
 *
 * - [uiState] — [com.shodo.android.presentation.searchfriend.SearchFriendUiState]
 * - [error] — [com.shodo.android.presentation.PresentationError]
 */
class SearchFriendViewModel(
    private val screenModelFactory: SearchFriendScreenModelFactory,
) : ViewModel() {

    private val screenModel: SearchFriendScreenModel by lazy {
        screenModelFactory.create(viewModelScope)
    }

    val uiState get() = screenModel.uiState
    val error get() = screenModel.error

    fun searchFriend(friendName: String) = screenModel.searchFriend(friendName)

    fun subscribeFriend(friendId: String) = screenModel.subscribeFriend(friendId)

    fun unsubscribeFriend(friendId: String) = screenModel.unsubscribeFriend(friendId)
}
