//package com.shodo.android.domain.usecases
//
//import com.shodo.android.domain.repositories.tracking.TrackingEventClick
//import com.shodo.android.domain.repositories.tracking.TrackingRepository
//
//class SendTrackingEventClickUseCase(private val trackingRepository: TrackingRepository) {
//
//    suspend fun execute(trackingEventClick: TrackingEventClick) {
//        trackingRepository.sendEventClick(trackingEventClick)
//    }
//}