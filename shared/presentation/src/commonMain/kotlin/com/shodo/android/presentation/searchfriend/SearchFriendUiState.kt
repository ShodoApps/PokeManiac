package com.shodo.android.presentation.searchfriend

import kotlinx.collections.immutable.PersistentList

/**
 * Immutable screen state for Search Friend, emitted as [kotlinx.coroutines.flow.StateFlow] from
 * [SearchFriendScreenModel]. Intended for Jetpack Compose and, later, SwiftUI parity.
 */
sealed class SearchFriendUiState {
    data object EmptySearch : SearchFriendUiState()
    data object Loading : SearchFriendUiState()
    data class Data(val people: PersistentList<SearchFriendUiModel>) : SearchFriendUiState()
    data class EmptyResult(val query: String) : SearchFriendUiState()
}
