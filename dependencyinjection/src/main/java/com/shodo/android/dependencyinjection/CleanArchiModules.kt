package com.shodo.android.dependencyinjection

import android.app.Application
import androidx.room.Room
import com.shodo.android.api.core.factories.RetrofitFactory.retrofitWith
import com.shodo.android.api.core.remote.SuperHerosApiService
import com.shodo.android.api.core.urlprovider.BaseUrlProvider
import com.shodo.android.api.request.FriendsRequestImpl
import com.shodo.android.data.myfriends.FriendsDataStore
import com.shodo.android.data.myfriends.FriendsRequest
import com.shodo.android.data.myfriends.UserRepositoryImpl
import com.shodo.android.data.myprofile.MyActivitiesDataStore
import com.shodo.android.data.myprofile.MyProfileRepositoryImpl
import com.shodo.android.data.newsfeed.NewsFeedRepositoryImpl
import com.shodo.android.data.tracking.TrackingDataStore
import com.shodo.android.database.PokeManiacDatabase
import com.shodo.android.database.TrackingDataStoreImpl
import com.shodo.android.database.friends.FriendsDataStoreImpl
import com.shodo.android.database.myactivities.MyActivitiesDataStoreImpl
import com.shodo.android.domain.repositories.friends.UserRepository
import com.shodo.android.domain.repositories.myprofile.MyProfileRepository
import com.shodo.android.domain.repositories.news.NewsFeedRepository
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    // database
    fun provideDataBase(application: Application): PokeManiacDatabase {
        return Room.databaseBuilder(application, PokeManiacDatabase::class.java, "PokeManiacDB")
            .fallbackToDestructiveMigration(false)
            .allowMainThreadQueries()
            .build()
    }

    single { provideDataBase(androidApplication()) }

    // datastores
    single<FriendsDataStore> { FriendsDataStoreImpl(get()) }
    single<MyActivitiesDataStore> { MyActivitiesDataStoreImpl(get()) }
    single<TrackingDataStore> { TrackingDataStoreImpl(get()) }
}

val dataModule = module {
    // repositories
    factory<UserRepository> { UserRepositoryImpl(get(), get()) }
    factory<NewsFeedRepository> { NewsFeedRepositoryImpl(get(), get()) }
    factory<MyProfileRepository> { MyProfileRepositoryImpl(get()) }
}

val apiModule = module {
    // network
    factory { BaseUrlProvider() }
    factory { OkHttpClient.Builder() }
    factory { retrofitWith().create(SuperHerosApiService::class.java) }

    // requests
    factory<FriendsRequest> { FriendsRequestImpl(get()) }
}

val cleanArchiModules = listOf(databaseModule, dataModule, apiModule)