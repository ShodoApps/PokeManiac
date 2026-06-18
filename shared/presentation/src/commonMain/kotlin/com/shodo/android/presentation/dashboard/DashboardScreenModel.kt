package com.shodo.android.presentation.dashboard

import com.shodo.android.domain.repositories.news.NewsFeedRepository
import com.shodo.android.presentation.PresentationError
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Dashboard **shared** screen logic (KMP `commonMain`). Android: [DashboardScreenModelFactory] + `viewModelScope`.
 */
class DashboardScreenModel(
    private val newsFeedRepository: NewsFeedRepository,
    private val coroutineScope: CoroutineScope
) {

    private val _error = MutableSharedFlow<PresentationError>()
    val error = _error.asSharedFlow()

    private val _uiState: MutableStateFlow<DashboardUiState> = MutableStateFlow(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var newsFeedJob: Job? = null

    fun start() {
        newsFeedJob?.cancel()
        newsFeedJob = coroutineScope.launch {
            _uiState.update { DashboardUiState.Loading }
            fetchNewActivities()
        }
    }

    fun refreshNewsFeed() {
        newsFeedJob?.cancel()
        newsFeedJob = coroutineScope.launch {
            _uiState.update { DashboardUiState.Loading }
            try {
                delay(2_000L) // Mock Delay to show the loading state
                fetchNewActivities()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.emit(PresentationError.from(e))
            }
        }
    }

    private suspend fun fetchNewActivities() {
        try {
            newsFeedRepository.getNewActivities().collect { result ->
                if (result.isNotEmpty()) {
                    val uiItems = withContext(Dispatchers.Default) {
                        result.map { it.mapToNewActivityUiModel() }.toPersistentList()
                    }
                    _uiState.update { DashboardUiState.Data(uiItems) }
                } else {
                    _uiState.update { DashboardUiState.EmptyResult }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _error.emit(PresentationError.from(e))
            _uiState.update { DashboardUiState.EmptyResult }
        }
    }
}
