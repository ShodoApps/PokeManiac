package com.shodo.android.myprofile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.coreui.navigator.BillingNavigator
import com.shodo.android.coreui.navigator.PostTransactionNavigator
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import com.shodo.android.domain.repositories.myprofile.MyProfileRepository
import com.shodo.android.myprofile.MyProfileUiState.Loading
import com.shodo.android.myprofile.uimodel.MyProfilePokemonCardUI
import com.shodo.android.myprofile.uimodel.MyProfileUI
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class MyProfileUiState {
    data object Loading: MyProfileUiState()
    data class Data(val profile: MyProfileUI): MyProfileUiState()
}

class MyProfileViewModel(
    private val myProfileRepository: MyProfileRepository,
    private val postTransactionNavigator: PostTransactionNavigator,
    private val billingNavigator: BillingNavigator
): ViewModel() {

    private val _uiState: MutableStateFlow<MyProfileUiState> = MutableStateFlow(Loading)
    val uiState = _uiState.asStateFlow()

    private val _error = MutableSharedFlow<Exception>()
    val error = _error.asSharedFlow()

    private var activitiesJob: Job? = null

    fun start() {
        activitiesJob?.cancel()
        activitiesJob = viewModelScope.launch {
            _uiState.update { Loading }
            try {
                myProfileRepository.getMyActivities().collect { myActivies ->
                    _uiState.update {
                        MyProfileUiState.Data(
                            profile = MyProfileUI(
                                name = null,
                                imageUrl = null,
                                pokemonCards = myActivies.sortedByDescending { it.date }.mapNotNull { it.pokemonCard.mapToUI() }.toPersistentList()
                            ))
                    }
                }
            } catch (e: Exception) {
                _error.emit(e)
            }
        }
    }

    fun navigateToPostTransaction(context: Context) {
        postTransactionNavigator.navigate(context)
    }
    fun navigateToBilling(context: Context) {
        billingNavigator.navigate(context)
    }
}

private fun UserPokemonCard.mapToUI(): MyProfilePokemonCardUI? {
    val fileSource = imageSource as? ImageSource.FileSource ?: return null
    return MyProfilePokemonCardUI(
        id = name + fileSource.fileUri,
        totalVotes = totalVotes,
        name = name,
        imageUri = fileSource.fileUri
    )
}
