package com.shodo.android.searchfriend.uimodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentList

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