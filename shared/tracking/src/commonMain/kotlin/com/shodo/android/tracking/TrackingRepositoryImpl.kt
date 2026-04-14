package com.shodo.android.tracking

import com.shodo.android.data.tracking.TrackingDataStore
import com.shodo.android.domain.repositories.tracking.TrackingEventClick
import com.shodo.android.domain.repositories.tracking.TrackingEventScreen
import com.shodo.android.domain.repositories.tracking.TrackingRepository

class TrackingRepositoryImpl(
    private val trackingDataStore: TrackingDataStore,
) : TrackingRepository {

    override suspend fun sendEventScreen(trackingEventScreen: TrackingEventScreen) {
        trackingDataStore.sendEventScreen(trackingEventScreen.eventName)
    }

    override suspend fun sendEventClick(trackingEventClick: TrackingEventClick) {
        trackingDataStore.sendEventClick(trackingEventClick.eventName)
    }
}
