package com.shodo.android.myprofile.uimodel

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentList

@Stable
data class MyProfileUI(
    val name: String?,
    val imageUrl: String?,
    val pokemonCards: PersistentList<MyProfilePokemonCardUI>
)

@Stable
data class MyProfilePokemonCardUI(
    val id: String,
    val name: String,
    val imageUri: Uri,
    val totalVotes: Int
)