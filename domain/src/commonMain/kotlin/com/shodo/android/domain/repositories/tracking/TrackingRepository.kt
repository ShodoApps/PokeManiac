package com.shodo.android.domain.repositories.tracking

interface TrackingRepository {

    suspend fun sendEventScreen(trackingEventScreen: TrackingEventScreen)

    suspend fun sendEventClick(trackingEventClick: TrackingEventClick)
}