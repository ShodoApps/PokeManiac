/**
 * My Profile **presentation models** (UiModels). Mapped from domain [NewActivity] list.
 * `ImageSource.FileSource` only — local capture URIs as opaque strings (see project KMP guide).
 */
package com.shodo.android.presentation.myprofile

import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

data class MyProfileUiModel(
    val name: String?,
    val imageUrl: String?,
    val pokemonCards: PersistentList<MyProfilePokemonCardUiModel>,
)

data class MyProfilePokemonCardUiModel(
    val id: String,
    val name: String,
    /** Local file / content URI string for Coil on Android. */
    val imageUri: String,
    val totalVotes: Int,
)

fun List<NewActivity>.mapToMyProfileUiModel(): MyProfileUiModel = MyProfileUiModel(
    name = null,
    imageUrl = null,
    pokemonCards = sortedByDescending { it.date }
        .mapNotNull { it.pokemonCard.mapToMyProfilePokemonCardUiModel() }
        .toPersistentList(),
)

private fun UserPokemonCard.mapToMyProfilePokemonCardUiModel(): MyProfilePokemonCardUiModel? {
    val fileSource = imageSource as? ImageSource.FileSource ?: return null
    return MyProfilePokemonCardUiModel(
        id = name + fileSource.fileUri,
        totalVotes = totalVotes,
        name = name,
        imageUri = fileSource.fileUri,
    )
}
