package com.shodo.android.api.request

import com.shodo.android.api.core.remote.SuperHerosApiService
import com.shodo.android.api.core.remote.model.SuperheroDTO
import com.shodo.android.data.myfriends.FriendsRequest
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.entities.UserPokemonCard

class FriendsRequestImpl(private val apiService: SuperHerosApiService) : FriendsRequest {
    override suspend fun searchUsers(friendName: String): List<User> {
        val response = apiService.searchCharacter(friendName)
        if (!response.isSuccessful) throw Exception("HTTP ${response.code()}: ${response.message()}")
        return response.body()?.results?.map { superheroDTO -> superheroDTO.mapToFriend() } ?: emptyList()
    }
}

// Should be in an adequate Mapper
private fun SuperheroDTO.mapToFriend() = User(
    id = this.id,
    name = this.name,
    imageUrl = this.image.url,
    description = this.work.occupation,
    isSubscribed = false,
    pokemonCards = fillPokemonCards(this.id)
)

// TOTALLY FAKE
private fun fillPokemonCards(friendId: String): List<UserPokemonCard> {
    return when (friendId) {
        "69" -> listOf(
            UserPokemonCard(
                pokemonId = 2,
                totalVotes = 19,
                hasMyVote = false,
                name = "Ivysaur",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png")
            ),
            UserPokemonCard(
                pokemonId = 18,
                totalVotes = 3,
                hasMyVote = false,
                name = "Pidgeot",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/18.png")
            ),
            UserPokemonCard(
                pokemonId = 100,
                totalVotes = 0,
                hasMyVote = false,
                name = "Voltorb",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/100.png")
            ),
            UserPokemonCard(
                pokemonId = 123,
                totalVotes = 20,
                hasMyVote = false,
                name = "Scyther",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/123.png")
            )
        ) // Batman 69
        "70" -> listOf(
            UserPokemonCard(
                pokemonId = 7,
                totalVotes = 4,
                hasMyVote = false,
                name = "Squirtle",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png")
            ),
            UserPokemonCard(
                pokemonId = 78,
                totalVotes = 5,
                hasMyVote = false,
                name = "Rapidash",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/78.png")
            )
        ) // Batman 70
        "71" -> listOf(
            UserPokemonCard(
                pokemonId = 42,
                totalVotes = 1,
                hasMyVote = false,
                name = "Golbat",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/42.png")
            ),
            UserPokemonCard(
                pokemonId = 65,
                totalVotes = 34,
                hasMyVote = false,
                name = "Alakazam",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/65.png")
            ),
            UserPokemonCard(
                pokemonId = 143,
                totalVotes = 3,
                hasMyVote = false,
                name = "Snorlax",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/143.png")
            )
        ) // Batman 71
        "195" -> listOf(
            UserPokemonCard(
                pokemonId = 9,
                totalVotes = 12,
                hasMyVote = false,
                name = "Shellder",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/9.png")
            ),
            UserPokemonCard(
                pokemonId = 18,
                totalVotes = 3,
                hasMyVote = false,
                name = "Pidgeot",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/18.png")
            )
        ) // Superman 195
        "644" -> listOf(
            UserPokemonCard(
                pokemonId = 150,
                totalVotes = 193,
                hasMyVote = false,
                name = "Mewtwo",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/150.png")
            )
        ) // Superman 644
        "720" -> listOf(
            UserPokemonCard(
                pokemonId = 2,
                totalVotes = 19,
                hasMyVote = false,
                name = "Ivysaur",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png")
            ),
            UserPokemonCard(
                pokemonId = 17,
                totalVotes = 3,
                hasMyVote = false,
                name = "Pidgeotto",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/17.png")
            ),
            UserPokemonCard(
                pokemonId = 54,
                totalVotes = 30,
                hasMyVote = false,
                name = "Psyduck",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/54.png")
            ),
            UserPokemonCard(
                pokemonId = 12,
                totalVotes = 20,
                hasMyVote = false,
                name = "Butterfree",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/12.png")
            ),
            UserPokemonCard(
                pokemonId = 98,
                totalVotes = 109,
                hasMyVote = false,
                name = "Krabby",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/98.png")
            )
        ) // Wonder woman 720
        else -> emptyList()
    }
}