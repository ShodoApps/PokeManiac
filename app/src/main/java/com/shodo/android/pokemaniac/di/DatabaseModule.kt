package com.shodo.android.pokemaniac.di

import android.app.Application
import androidx.room.Room
import com.shodo.android.data.myfriends.FriendsDataStore
import com.shodo.android.data.myprofile.MyActivitiesDataStore
import com.shodo.android.data.tracking.TrackingDataStore
import com.shodo.android.database.PokeManiacDatabase
import com.shodo.android.database.TrackingDataStoreImpl
import com.shodo.android.database.friends.FriendsDataStoreImpl
import com.shodo.android.database.myactivities.MyActivitiesDataStoreImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Android-only Koin module: [Application], Room, and DataStore implementations.
 * Kept in `app` so `:shared:di` `commonMain` code stays free of Android APIs (Phase E).
 */
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
