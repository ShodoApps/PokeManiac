/**
 * Search Friend **presentation models** (UiModels): data shaped for the UI, mapped from domain [User] / cards.
 * Lives in `:shared:presentation` for cross-platform screens (see `docs/kmp-migration-plan.md`).
 */
package com.shodo.android.presentation.searchfriend

import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

/** Subscription control state for a row in the search results list. */
enum class SubscriptionState {
    Subscribed,
    NotSubscribed,
    UpdatingSubscribe
}

/** One user row in Search Friend results ([SearchFriendUiState.Data]). */
data class SearchFriendUiModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val subscriptionState: SubscriptionState,
    val pokemonCards: PersistentList<SearchFriendPokemonCardUiModel>
)

/** Simplified card line item shown under a user row. */
data class SearchFriendPokemonCardUiModel(
    val pokemonId: Int,
    val totalVotes: Int,
    val hasMyVote: Boolean,
    val name: String,
    val imageUrl: String
)

/** Maps a domain user to UI; [ImageSource] is reduced to a display URL string for list rows. */
fun User.mapToSearchFriendUiModel(): SearchFriendUiModel = SearchFriendUiModel(
    id = id,
    name = name,
    imageUrl = imageUrl,
    description = description,
    subscriptionState = if (isSubscribed) SubscriptionState.Subscribed else SubscriptionState.NotSubscribed,
    pokemonCards = pokemonCards.map { it.mapToSearchFriendPokemonCardUiModel() }.toPersistentList()
)

/** Rebuilds a domain [User] for subscribe/unsubscribe calls (uses [ImageSource.UrlSource] for card images). */
fun SearchFriendUiModel.mapToUser(): User = User(
    id = id,
    name = name,
    imageUrl = imageUrl,
    description = description,
    isSubscribed = subscriptionState == SubscriptionState.Subscribed,
    pokemonCards = pokemonCards.map { it.mapToUserPokemonCard() }
)

private fun UserPokemonCard.mapToSearchFriendPokemonCardUiModel(): SearchFriendPokemonCardUiModel =
    SearchFriendPokemonCardUiModel(
        pokemonId = pokemonId,
        totalVotes = totalVotes,
        hasMyVote = hasMyVote,
        name = name,
        imageUrl = (imageSource as? ImageSource.UrlSource)?.imageUrl ?: ""
    )

private fun SearchFriendPokemonCardUiModel.mapToUserPokemonCard(): UserPokemonCard = UserPokemonCard(
    pokemonId = pokemonId,
    totalVotes = totalVotes,
    hasMyVote = hasMyVote,
    name = name,
    imageSource = ImageSource.UrlSource(imageUrl)
)
