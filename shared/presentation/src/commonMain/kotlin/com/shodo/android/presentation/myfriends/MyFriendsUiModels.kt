/**
 * My Friends **presentation models** (UiModels): data shaped for the UI, mapped from domain [User].
 * Lives in `:shared:presentation` for cross-platform screens (see `docs/kmp-migration-plan.md`).
 */
package com.shodo.android.presentation.myfriends

import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

/** One friend row in the subscribed friends list or detail header. */
data class MyFriendUiModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val pokemonCards: PersistentList<MyFriendPokemonCardUiModel>
)

/** One Pokémon card tile on the friend detail screen. */
data class MyFriendPokemonCardUiModel(
    val id: String,
    val pokemonId: Int,
    val totalVotes: Int,
    val hasMyVote: Boolean,
    val name: String,
    val imageUrl: String
)

fun User.mapToMyFriendUiModel(): MyFriendUiModel = MyFriendUiModel(
    id = id,
    name = name,
    imageUrl = imageUrl,
    description = description,
    pokemonCards = pokemonCards.map { it.mapToMyFriendPokemonCardUiModel() }.toPersistentList()
)

private fun UserPokemonCard.mapToMyFriendPokemonCardUiModel(): MyFriendPokemonCardUiModel =
    MyFriendPokemonCardUiModel(
        id = name + (imageSource as? ImageSource.UrlSource)?.imageUrl.orEmpty(),
        pokemonId = pokemonId,
        totalVotes = totalVotes,
        hasMyVote = hasMyVote,
        name = name,
        imageUrl = (imageSource as? ImageSource.UrlSource)?.imageUrl.orEmpty()
    )
