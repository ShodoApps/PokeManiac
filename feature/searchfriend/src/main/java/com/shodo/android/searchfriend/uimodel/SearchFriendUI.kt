package com.shodo.android.searchfriend.uimodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Immutable
enum class SubscriptionState {
    Subscribed, NotSubscribed, UpdatingSubscribe
}

@Immutable
data class SearchFriendUI(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val subscriptionState: SubscriptionState,
    val pokemonCards: PersistentList<SearchFriendPokemonCardUI>
)

@Immutable
data class SearchFriendPokemonCardUI(
    val pokemonId: Int,
    val totalVotes: Int,
    val hasMyVote: Boolean,
    val name: String,
    val imageUrl: String
)

fun User.mapToSearchFriendUI() = SearchFriendUI(
    id = id,
    name = name,
    imageUrl = imageUrl,
    description = description,
    subscriptionState = if (isSubscribed) SubscriptionState.Subscribed else SubscriptionState.NotSubscribed,
    pokemonCards = pokemonCards.map { it.mapToSearchFriendUI() }.toPersistentList()
)

fun SearchFriendUI.mapToUser() = User(
    id = id,
    name = name,
    imageUrl = imageUrl,
    description = description,
    isSubscribed = subscriptionState == SubscriptionState.Subscribed,
    pokemonCards = pokemonCards.map { it.mapToUserPokemonCard() }
)

private fun UserPokemonCard.mapToSearchFriendUI() = SearchFriendPokemonCardUI(
    pokemonId = pokemonId,
    totalVotes = totalVotes,
    hasMyVote = hasMyVote,
    name = name,
    imageUrl = (imageSource as? ImageSource.UrlSource)?.imageUrl ?: ""
)

private fun SearchFriendPokemonCardUI.mapToUserPokemonCard() = UserPokemonCard(
    pokemonId = pokemonId,
    totalVotes = totalVotes,
    hasMyVote = hasMyVote,
    name = name,
    imageSource = ImageSource.UrlSource(imageUrl)
)