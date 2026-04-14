package com.shodo.android.di

import com.shodo.android.domain.repositories.tracking.TrackingRepository
import com.shodo.android.tracking.TrackingRepositoryImpl
import org.koin.dsl.module

val trackingModule = module {
    single<TrackingRepository> { TrackingRepositoryImpl(get()) }
}
