package com.shodo.android.pokemaniac.di

import android.app.Application
import com.shodo.android.database.PokeManiacDatabase
import com.shodo.android.database.TrackingDataStoreImpl
import com.shodo.android.database.buildPokeManiacDatabase
import com.shodo.android.database.friends.FriendsDataStoreImpl
import com.shodo.android.database.myactivities.MyActivitiesDataStoreImpl
import com.shodo.android.domain.datastore.FriendsDataStore
import com.shodo.android.domain.datastore.MyActivitiesDataStore
import com.shodo.android.domain.datastore.TrackingDataStore
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Android-only Koin module: [Application], Room, and DataStore implementations.
 * Kept in `app` so `:shared:di` `commonMain` code stays free of Android APIs (Phase E).
 */
val databaseModule = module {
    fun provideDataBase(application: Application): PokeManiacDatabase =
        buildPokeManiacDatabase(application)

    single { provideDataBase(androidApplication()) }

    single<FriendsDataStore> { FriendsDataStoreImpl(get()) }
    single<MyActivitiesDataStore> { MyActivitiesDataStoreImpl(get()) }
    single<TrackingDataStore> { TrackingDataStoreImpl(get()) }
}
