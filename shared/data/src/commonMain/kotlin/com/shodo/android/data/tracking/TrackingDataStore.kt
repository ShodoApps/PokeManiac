package com.shodo.android.data.tracking

interface TrackingDataStore {
    suspend fun sendEventScreen(screenName: String)
    suspend fun sendEventClick(clickName: String)
}