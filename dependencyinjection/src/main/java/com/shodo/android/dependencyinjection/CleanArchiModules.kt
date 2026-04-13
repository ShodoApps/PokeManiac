package com.shodo.android.dependencyinjection

import android.app.Application
import androidx.room.Room
import com.shodo.android.data.myfriends.FriendsDataStore
import com.shodo.android.data.myfriends.UserRepositoryImpl
import com.shodo.android.data.myprofile.MyActivitiesDataStore
import com.shodo.android.data.myprofile.MyProfileRepositoryImpl
import com.shodo.android.data.newsfeed.NewsFeedRepositoryImpl
import com.shodo.android.data.tracking.TrackingDataStore
import com.shodo.android.database.PokeManiacDatabase
import com.shodo.android.database.TrackingDataStoreImpl
import com.shodo.android.database.friends.FriendsDataStoreImpl
import com.shodo.android.database.myactivities.MyActivitiesDataStoreImpl
import com.shodo.android.domain.repositories.friends.FriendsRequest
import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.domain.repositories.myprofile.MyProfileRepository
import com.shodo.android.domain.repositories.news.NewsFeedRepository
import com.shodo.android.shared.api.core.factories.HttpClientFactory
import com.shodo.android.shared.api.core.factories.SuperHerosApiClientFactory
import com.shodo.android.shared.api.core.urlprovider.BaseUrlProvider
import com.shodo.android.shared.api.request.FriendsRequestImpl
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    fun provideDataBase(application: Application): PokeManiacDatabase {
        return Room.databaseBuilder(application, PokeManiacDatabase::class.java, "PokeManiacDB")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    single { provideDataBase(androidApplication()) }

    single<FriendsDataStore> { FriendsDataStoreImpl(get()) }
    single<MyActivitiesDataStore> { MyActivitiesDataStoreImpl(get()) }
    single<TrackingDataStore> { TrackingDataStoreImpl(get()) }
}

val dataModule = module {
    factory<UserRepository> { UserRepositoryImpl(get(), get()) }
    factory<NewsFeedRepository> { NewsFeedRepositoryImpl(get(), get()) }
    factory<MyProfileRepository> { MyProfileRepositoryImpl(get()) }
}

val apiModule = module {
    // BaseUrlProvider → HttpClient(OkHttp/Darwin factory) → typed service → FriendsRequest
    factory { BaseUrlProvider() }
    factory<HttpClient> { HttpClientFactory.create() }
    factory {
        SuperHerosApiClientFactory.createService(httpClient = get())
    }
    factory<FriendsRequest> { FriendsRequestImpl(get()) }
}

val cleanArchiModules = listOf(databaseModule, dataModule, apiModule)
