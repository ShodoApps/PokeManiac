package com.shodo.android.presentation.myfriends

/**
 * Screen state for My Friend detail, emitted from [MyFriendDetailScreenModel].
 * Logically immutable snapshots; for Compose skipping, see Android feature views / `compose-patterns.mdc`.
 */
sealed class MyFriendDetailUiState {
    data class Data(val friend: MyFriendUiModel) : MyFriendDetailUiState()
    data object Loading : MyFriendDetailUiState()
}
