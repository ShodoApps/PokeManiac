package com.shodo.android.dashboard.di

import com.shodo.android.presentation.dashboard.DashboardScreenModel
import kotlinx.coroutines.CoroutineScope

fun interface DashboardScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): DashboardScreenModel
}
