package com.shodo.android.dashboard.di

import com.shodo.android.coreui.navigator.DashboardNavigator
import com.shodo.android.dashboard.DashboardViewModel
import com.shodo.android.dashboard.navigator.DashboardNavigatorImpl
import com.shodo.android.presentation.dashboard.DashboardScreenModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dashboardModule = module {
    single<DashboardNavigator> { DashboardNavigatorImpl() }
    factory<DashboardScreenModelFactory> {
        DashboardScreenModelFactory { scope: CoroutineScope ->
            DashboardScreenModel(
                newsFeedRepository = get(),
                coroutineScope = scope,
            )
        }
    }
    viewModelOf(::DashboardViewModel)
}