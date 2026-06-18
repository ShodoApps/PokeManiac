package com.shodo.android.presentation.myprofile

import com.shodo.android.domain.repositories.myprofile.MyProfileRepository
import com.shodo.android.presentation.PresentationError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * My Profile **shared** screen logic (KMP `commonMain`). Android wires [MyProfileScreenModelFactory] + `viewModelScope`.
 */
class MyProfileScreenModel(
    private val myProfileRepository: MyProfileRepository,
    private val coroutineScope: CoroutineScope
) {

    private val _uiState: MutableStateFlow<MyProfileUiState> = MutableStateFlow(MyProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<PresentationError>()
    val error = _error.asSharedFlow()

    private var activitiesJob: Job? = null

    fun start() {
        activitiesJob?.cancel()
        activitiesJob = coroutineScope.launch {
            _uiState.update { MyProfileUiState.Loading }
            try {
                myProfileRepository.getMyActivities().collect { myActivities ->
                    val profile = withContext(Dispatchers.Default) {
                        myActivities.mapToMyProfileUiModel()
                    }
                    _uiState.update { MyProfileUiState.Data(profile = profile) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.emit(PresentationError.from(e))
            }
        }
    }
}
