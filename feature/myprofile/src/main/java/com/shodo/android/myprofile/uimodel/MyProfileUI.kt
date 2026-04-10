package com.shodo.android.myprofile.uimodel

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

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

fun List<NewActivity>.mapToMyProfileUI(): MyProfileUI = MyProfileUI(
    name = null,
    imageUrl = null,
    pokemonCards = sortedByDescending { it.date }
        .mapNotNull { it.pokemonCard.mapToMyProfileUI() }
        .toPersistentList()
)

private fun UserPokemonCard.mapToMyProfileUI(): MyProfilePokemonCardUI? {
    val fileSource = imageSource as? ImageSource.FileSource ?: return null
    return MyProfilePokemonCardUI(
        id = name + fileSource.fileUri,
        totalVotes = totalVotes,
        name = name,
        imageUri = fileSource.fileUri
    )
}