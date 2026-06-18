package com.shodo.android.shared.api.request

import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import com.shodo.android.domain.repositories.friends.FriendsRequest
import com.shodo.android.shared.api.core.remote.SuperHerosApiService
import com.shodo.android.shared.api.core.remote.model.SuperheroDTO

/**
 * Same layering as before: [FriendsRequest] implementation delegates to [SuperHerosApiService], then maps DTO → domain.
 *
 * **Portrait URLs:** the API points [User.imageUrl] at `superherodb.com`, which is often behind **Cloudflare** bot/challenge
 * protection. In-app image loaders (Coil + OkHttp) are not a full browser and may get blocked or receive HTML instead of
 * JPEG — that is an infrastructure/CDN constraint, not a Compose or Coil configuration bug. Mitigations are outside this
 * mapper: e.g. your own image proxy/CDN, a backend that resolves portraits, or a host that allows mobile app traffic.
 *
 * Does not catch [kotlinx.coroutines.CancellationException]; it propagates so upstream coroutines (e.g. ViewModel / Flow) cancel correctly.
 * Never wrap this call in `catch (Exception)` without rethrowing cancellation first.
 */
class FriendsRequestImpl(
    private val apiService: SuperHerosApiService
) : FriendsRequest {

    override suspend fun searchUsers(friendName: String): List<User> {
        val dto = apiService.searchCharacter(friendName)
        if (dto.response.equals("error", ignoreCase = true)) {
            throw Exception(dto.error ?: "SuperHero API error")
        }
        return dto.results.map { it.mapToFriend() }
    }
}

private fun SuperheroDTO.mapToFriend() = User(
    id = id,
    name = name,
    imageUrl = image.url.normalizeSuperHeroImageUrl(),
    description = work.occupation,
    isSubscribed = false,
    pokemonCards = fillPokemonCards(id)
)

/**
 * SuperHero DB returns protocol-relative image URLs (`//www.superherodb.com/...`).
 * Normalize to `https://…` so clients have an absolute URL (whether they can fetch it past Cloudflare is separate).
 */
private fun String.normalizeSuperHeroImageUrl(): String {
    val t = trim()
    return if (t.startsWith("//")) "https:$t" else t
}

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
        )
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
        )
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
        )
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
        )
        "644" -> listOf(
            UserPokemonCard(
                pokemonId = 150,
                totalVotes = 193,
                hasMyVote = false,
                name = "Mewtwo",
                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/150.png")
            )
        )
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
        )
        else -> emptyList()
    }
}
