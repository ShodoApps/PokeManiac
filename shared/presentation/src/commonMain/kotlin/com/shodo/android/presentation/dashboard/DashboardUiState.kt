package com.shodo.android.presentation.dashboard

import kotlinx.collections.immutable.PersistentList

/**
 * Dashboard screen state from [DashboardScreenModel].
 */
sealed class DashboardUiState {
    data class Data(val news: PersistentList<NewActivityUiModel>) : DashboardUiState()
    data object EmptyResult : DashboardUiState()
    data object Loading : DashboardUiState()
}

/** Plain [List] for SwiftUI `ForEach` (iOS). */
fun DashboardUiState.Data.newsList(): List<NewActivityUiModel> = news.toList()
