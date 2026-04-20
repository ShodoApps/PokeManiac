package com.shodo.android.data.newsfeed

import com.shodo.android.domain.datastore.FriendsDataStore
import com.shodo.android.domain.datastore.MyActivitiesDataStore
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import com.shodo.android.domain.repositories.news.NewsFeedRepository
import kotlinx.datetime.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

// This one is mocked, sorry !
class NewsFeedRepositoryImpl(
    private val friendsDataStore: FriendsDataStore,
    private val myActivitiesDataStore: MyActivitiesDataStore
) : NewsFeedRepository {
    override fun getNewActivities(): Flow<List<NewActivity>> {
        val getMyActivities = myActivitiesDataStore.getAllActivities()
        val getBatman69Activities = friendsDataStore.getFriendById("69")
        val getBatman70Activities = friendsDataStore.getFriendById("70")
        val getSuperman720Activities = friendsDataStore.getFriendById("720")
        return getBatman69Activities.combine(getBatman70Activities) { batman69, batman70 ->
            mutableListOf<NewActivity>().apply {
                batman69?.let {
                    batman69.pokemonCards.getOrNull(2)?.let { card ->
                        add(
                            NewActivity(
                                userName = batman69.name,
                                userImageUrl = batman69.imageUrl,
                                date = LocalDateTime(2025, 2, 20, 12, 34, 50),
                                pokemonCard = card,
                                activityType = NewActivityType.Purchase,
                                price = 59
                            )
                        )
                    }
                    batman69.pokemonCards.getOrNull(3)?.let { card ->
                        add(
                            NewActivity(
                                userName = batman69.name,
                                userImageUrl = batman69.imageUrl,
                                date = LocalDateTime(2025, 3, 10, 13, 32, 29),
                                pokemonCard = card,
                                activityType = NewActivityType.Purchase,
                                price = 190
                            )
                        )
                    }
                    add(
                        NewActivity(
                            userName = batman69.name,
                            userImageUrl = batman69.imageUrl,
                            date = LocalDateTime(2025, 3, 10, 2, 4, 30),
                            pokemonCard = UserPokemonCard(
                                pokemonId = 42,
                                totalVotes = 1,
                                hasMyVote = false,
                                name = "Golbat",
                                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/42.png")
                            ),
                            activityType = NewActivityType.Sale,
                            price = 2338
                        )
                    )
                }

                batman70?.let {
                    batman70.pokemonCards.getOrNull(0)?.let { card ->
                        add(
                            NewActivity(
                                userName = batman70.name,
                                userImageUrl = batman70.imageUrl,
                                date = LocalDateTime(2025, 1, 12, 3, 34, 2),
                                pokemonCard = card,
                                activityType = NewActivityType.Purchase,
                                price = 59
                            )
                        )
                    }
                    add(
                        NewActivity(
                            userName = batman70.name,
                            userImageUrl = batman70.imageUrl,
                            date = LocalDateTime(2025, 3, 10, 2, 4, 12),
                            pokemonCard = UserPokemonCard(
                                pokemonId = 150,
                                totalVotes = 193,
                                hasMyVote = false,
                                name = "Mewtwo",
                                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/150.png")
                            ),
                            activityType = NewActivityType.Sale,
                            price = 10000
                        )
                    )
                }
            }
        }.combine(getSuperman720Activities) { batmansActivities, superman720 ->
            mutableListOf<NewActivity>().apply {
                addAll(batmansActivities)
                superman720?.let {
                    superman720.pokemonCards.getOrNull(0)?.let { card ->
                        add(
                            NewActivity(
                                userName = superman720.name,
                                userImageUrl = superman720.imageUrl,
                                date = LocalDateTime(2024, 12, 30, 13, 50, 24),
                                pokemonCard = card,
                                activityType = NewActivityType.Purchase,
                                price = 59
                            )
                        )
                    }
                    add(
                        NewActivity(
                            userName = superman720.name,
                            userImageUrl = superman720.imageUrl,
                            date = LocalDateTime(2025, 2, 14, 19, 4, 45),
                            pokemonCard = UserPokemonCard(
                                pokemonId = 12,
                                totalVotes = 20,
                                hasMyVote = false,
                                name = "Butterfree",
                                imageSource = ImageSource.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/12.png")
                            ),
                            activityType = NewActivityType.Sale,
                            price = 80
                        )
                    )
                }
            }
        }.combine(getMyActivities) { friendsActivities, myActivities ->
            mutableListOf<NewActivity>().apply {
                addAll(myActivities)
                addAll(friendsActivities)
            }.sortedByDescending { it.date }.toList()
        }.flowOn(Dispatchers.Default)
    }

    override suspend fun saveNewActivity(newActivity: NewActivity) {
        myActivitiesDataStore.saveNewActivity(newActivity)
    }
}