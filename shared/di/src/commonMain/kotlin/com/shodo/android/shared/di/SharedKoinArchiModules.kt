package com.shodo.android.shared.di

import com.shodo.android.data.myfriends.UserRepositoryImpl
import com.shodo.android.data.myprofile.MyProfileRepositoryImpl
import com.shodo.android.data.newsfeed.NewsFeedRepositoryImpl
import com.shodo.android.domain.repositories.friends.FriendsRequest
import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.domain.repositories.myprofile.MyProfileRepository
import com.shodo.android.domain.repositories.news.NewsFeedRepository
import com.shodo.android.shared.api.core.factories.HttpClientFactory
import com.shodo.android.shared.api.core.factories.SuperHerosApiClientFactory
import com.shodo.android.shared.api.core.urlprovider.BaseUrlProvider
import com.shodo.android.shared.api.request.FriendsRequestImpl
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin modules defined in **`commonMain`**: no Android `Context`, no `Room.databaseBuilder`.
 * Loaded from Android `Application` together with Android-only modules (e.g. Room) in `app`.
 */
val dataModule = module {
    factory<UserRepository> { UserRepositoryImpl(get(), get()) }
    factory<NewsFeedRepository> { NewsFeedRepositoryImpl(get(), get()) }
    factory<MyProfileRepository> { MyProfileRepositoryImpl(get()) }
}

val apiModule = module {
    factory { BaseUrlProvider() }
    factory<HttpClient> { HttpClientFactory.create() }
    factory {
        SuperHerosApiClientFactory.createService(httpClient = get())
    }
    factory<FriendsRequest> { FriendsRequestImpl(get()) }
}

/** Order: `dataModule` then `apiModule` (after `databaseModule` in `app`). */
val sharedKoinArchiModules: List<Module> = listOf(dataModule, apiModule)
