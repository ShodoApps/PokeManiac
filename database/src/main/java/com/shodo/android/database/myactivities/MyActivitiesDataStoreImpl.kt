package com.shodo.android.database.myactivities

import androidx.core.net.toUri
import com.shodo.android.data.myprofile.MyActivitiesDataStore
import com.shodo.android.database.PokeManiacDatabase
import com.shodo.android.database.myactivities.MyActivityBase.NewActivityTypeBase
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MyActivitiesDataStoreImpl(private val database: PokeManiacDatabase) : MyActivitiesDataStore {
    override suspend fun saveNewActivity(newActivity: NewActivity) {
        database.localMyActivitiesDao().insert(newActivity.mapToBase())
    }

    override fun getAllActivities(): Flow<List<NewActivity>> {
        return database.localMyActivitiesDao().getAllActivities().map { activities -> activities.map { it.mapToModel() } }
    }
}

private fun NewActivity.mapToBase() = MyActivityBase(
    date = LocalDateTime.now().toString(),
    price = price,
    type = when (activityType) {
        NewActivityType.Purchase -> NewActivityTypeBase.Purchase
        NewActivityType.Sale -> NewActivityTypeBase.Sale
    },
    pokemonCard = MyActivityBase.MyPokemonCardBase(
        id = pokemonCard.pokemonId,
        name = pokemonCard.name,
        fileUri = (pokemonCard.imageSource as ImageSource.FileSource).fileUri.toString(),
        hasMyVote = pokemonCard.hasMyVote,
        totalVotes = pokemonCard.totalVotes
    )
)

private fun MyActivityBase.mapToModel() = NewActivity(
    userName = "Super Dresseur",
    userImageUrl = null,
    date = LocalDateTime.parse(date),
    price = price,
    activityType = when (type) {
        NewActivityTypeBase.Purchase -> NewActivityType.Purchase
        NewActivityTypeBase.Sale -> NewActivityType.Sale
    },
    pokemonCard = UserPokemonCard(
        pokemonId = pokemonCard.id,
        imageSource = ImageSource.FileSource(pokemonCard.fileUri.toUri()),
        totalVotes = pokemonCard.totalVotes,
        hasMyVote = pokemonCard.hasMyVote,
        name = pokemonCard.name
    )
)
