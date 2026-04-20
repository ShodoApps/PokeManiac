package com.shodo.android.tracking

import com.shodo.android.database.TrackingDataStoreImpl
import com.shodo.android.database.sharedPokeManiacDatabase
import com.shodo.android.domain.repositories.tracking.TrackingRepository

fun createTrackingRepositoryForIos(): TrackingRepository =
    TrackingRepositoryImpl(TrackingDataStoreImpl(sharedPokeManiacDatabase()))
