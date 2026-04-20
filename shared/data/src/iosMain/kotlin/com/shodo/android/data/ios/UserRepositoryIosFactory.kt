package com.shodo.android.data.ios

import com.shodo.android.data.myfriends.UserRepositoryImpl
import com.shodo.android.database.friends.FriendsDataStoreImpl
import com.shodo.android.database.sharedPokeManiacDatabase
import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.shared.api.core.factories.HttpClientFactory
import com.shodo.android.shared.api.core.remote.SuperHerosApiService
import com.shodo.android.shared.api.core.urlprovider.BaseUrlProvider
import com.shodo.android.shared.api.getSuperHeroApiToken
import com.shodo.android.shared.api.request.FriendsRequestImpl

/** Same stack as Koin [com.shodo.android.shared.di.dataModule] / [com.shodo.android.shared.di.apiModule], without Koin on iOS. */
fun createUserRepositoryForIos(): UserRepository {
    val apiService =
        SuperHerosApiService(
            httpClient = HttpClientFactory.create(),
            baseUrlProvider = BaseUrlProvider(),
            apiToken = getSuperHeroApiToken(),
        )
    return UserRepositoryImpl(
        friendsRequest = FriendsRequestImpl(apiService),
        friendsDataStore = FriendsDataStoreImpl(sharedPokeManiacDatabase()),
    )
}
