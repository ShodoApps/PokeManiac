package com.shodo.android.domain.datastore

interface TrackingDataStore {
    suspend fun sendEventScreen(screenName: String)
    suspend fun sendEventClick(clickName: String)
}
