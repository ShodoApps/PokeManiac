package com.shodo.android.searchfriend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.domain.repositories.tracking.TrackingRepository
import com.shodo.android.presentation.searchfriend.SearchFriendScreenModel

/**
 * AndroidX [ViewModel] for Search Friend (Koin + Compose).
 *
 * Holds a [SearchFriendScreenModel] and forwards its API. The **ScreenModel** lives in
 * `:shared:presentation` (KMP); this class only ties it to Android lifecycle via [viewModelScope].
 *
 * This split is a common **migration pattern**: AndroidX `ViewModel` survives process death and
 * provides a structured scope; shared code stays Android-free. Later you may replace this wrapper
 * with another scope owner (e.g. Compose-first or multiplatform lifecycle) if you drop AndroidX
 * `ViewModel` project-wide.
 *
 * - [uiState] — [com.shodo.android.presentation.searchfriend.SearchFriendUiState]
 * - [error] — [com.shodo.android.presentation.PresentationError]
 */
class SearchFriendViewModel(
    userRepository: UserRepository,
    trackingRepository: TrackingRepository,
) : ViewModel() {

    private val screenModel = SearchFriendScreenModel(
        userRepository = userRepository,
        trackingRepository = trackingRepository,
        coroutineScope = viewModelScope,
    )

    val uiState get() = screenModel.uiState
    val error get() = screenModel.error

    fun searchFriend(friendName: String) = screenModel.searchFriend(friendName)

    fun subscribeFriend(friendId: String) = screenModel.subscribeFriend(friendId)

    fun unsubscribeFriend(friendId: String) = screenModel.unsubscribeFriend(friendId)
}
