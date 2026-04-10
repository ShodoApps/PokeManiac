package com.shodo.android.myfriends.uimodel

import androidx.compose.runtime.Immutable
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Immutable
data class MyFriendUI(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val pokemonCards: PersistentList<MyFriendPokemonCardUI>
)

@Immutable
data class MyFriendPokemonCardUI(
    val id: String,
    val pokemonId: Int,
    val totalVotes: Int,
    val hasMyVote: Boolean,
    val name: String,
    val imageUrl: String
)

fun User.mapToUI() = MyFriendUI(
    id = id,
    name = name,
    imageUrl = imageUrl,
    description = description,
    pokemonCards = pokemonCards.map { it.mapToUI() }.toPersistentList()
)

private fun UserPokemonCard.mapToUI() = MyFriendPokemonCardUI(
    id = name + (imageSource as ImageSource.UrlSource).imageUrl,
    pokemonId = pokemonId,
    totalVotes = totalVotes,
    hasMyVote = hasMyVote,
    name = name,
    imageUrl = (imageSource as ImageSource.UrlSource).imageUrl
)
