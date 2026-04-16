package com.shodo.android.presentation.myfriends

import kotlinx.collections.immutable.PersistentList

/**
 * Screen state for My Friends list, emitted from [MyFriendListScreenModel].
 * Logically immutable snapshots; for Compose skipping, see Android feature views / `compose-patterns.mdc`.
 */
sealed class MyFriendListUiState {
    data object Loading : MyFriendListUiState()
    data class Data(val friends: PersistentList<MyFriendUiModel>) : MyFriendListUiState()
    data object Empty : MyFriendListUiState()
}
