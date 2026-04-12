package com.shodo.android.domain.repositories.entities

data class User(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val isSubscribed: Boolean,
    val pokemonCards: List<UserPokemonCard>
)

data class UserPokemonCard(
    val pokemonId: Int,
    val name: String,
    val imageSource: ImageSource,
    val totalVotes: Int,
    val hasMyVote: Boolean
)

sealed class ImageSource {
    data class UrlSource(val imageUrl: String) : ImageSource()
    data class FileSource(val fileUri: String) : ImageSource()
}