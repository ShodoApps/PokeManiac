package com.shodo.android.database

import com.shodo.android.domain.datastore.TrackingDataStore
import com.shodo.android.database.tracking.TrackingEventBase

// This TrackingDataStore should send tracking to adequate tracking libs (Firebase, Dynatrace, Segment, DataDog, ... )
// But here it is mocked by a database

class TrackingDataStoreImpl(private val database: PokeManiacDatabase) : TrackingDataStore {

    override suspend fun sendEventScreen(screenName: String) {
        val newEventCount = (database.localScreenEventDao().getEvent(screenName)?.count ?: 0) + 1
        database.localScreenEventDao().sendEvent(TrackingEventBase(screenName, newEventCount))
        println("TRACKING $screenName count = ${database.localScreenEventDao().getEvent(screenName)?.count ?: 0}")
    }

    override suspend fun sendEventClick(clickName: String) {
        val newEventCount = (database.localClickEventDao().getEvent(clickName)?.count ?: 0) + 1
        database.localClickEventDao().sendEvent(TrackingEventBase(clickName, newEventCount))
        println("TRACKING $clickName count = ${database.localClickEventDao().getEvent(clickName)?.count ?: 0}")
    }
}
