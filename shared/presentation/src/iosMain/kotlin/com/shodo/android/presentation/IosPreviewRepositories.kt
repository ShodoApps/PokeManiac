package com.shodo.android.presentation

import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import com.shodo.android.domain.repositories.news.NewsFeedRepository
import com.shodo.android.presentation.dashboard.DashboardScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month

/**
 * Dashboard-only iOS preview: [NewsFeedRepository] still depends on Room-backed stores on Android.
 * When `:shared:database` gains iOS, replace with real [com.shodo.android.data.newsfeed.NewsFeedRepositoryImpl] wiring.
 */
fun createDashboardScreenModelForIos(coroutineScope: CoroutineScope): DashboardScreenModel =
    DashboardScreenModel(
        newsFeedRepository = IosPreviewNewsFeedRepository(),
        coroutineScope = coroutineScope,
    )

private class IosPreviewNewsFeedRepository : NewsFeedRepository {
    override fun getNewActivities(): Flow<List<NewActivity>> = flowOf(iosPreviewActivities())

    override suspend fun saveNewActivity(newActivity: NewActivity) {}
}

private fun iosPreviewActivities(): List<NewActivity> {
    val card =
        UserPokemonCard(
            pokemonId = 25,
            name = "Pikachu",
            imageSource =
                ImageSource.UrlSource(
                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
                ),
            totalVotes = 42,
            hasMyVote = true,
        )
    return listOf(
        NewActivity(
            userName = "Ash",
            userImageUrl = null,
            date = LocalDateTime(2024, Month.JANUARY, 10, 14, 0),
            pokemonCard = card,
            activityType = NewActivityType.Purchase,
            price = 120,
        ),
    )
}
