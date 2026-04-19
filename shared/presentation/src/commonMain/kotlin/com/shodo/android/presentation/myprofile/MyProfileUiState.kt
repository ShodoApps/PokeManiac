package com.shodo.android.presentation.myprofile

/**
 * Screen state for My Profile, from [MyProfileScreenModel].
 * Logically immutable snapshots; Compose stability → Android feature layer / compose-patterns.
 */
sealed class MyProfileUiState {
    data object Loading : MyProfileUiState()
    data class Data(val profile: MyProfileUiModel) : MyProfileUiState()
}
