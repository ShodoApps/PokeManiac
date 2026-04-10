package com.shodo.android.posttransaction.step2

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.domain.repositories.entities.ImageSource.FileSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import com.shodo.android.domain.repositories.news.NewsFeedRepository
import com.shodo.android.posttransaction.step2.PostTransactionStep2UiState.Filling
import com.shodo.android.posttransaction.step2.PostTransactionStep2UiState.Loading
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

sealed class PostTransactionStep2UiState {
    data object Filling : PostTransactionStep2UiState()
    data object Loading : PostTransactionStep2UiState()
}

class PostTransactionStep2ViewModel(
    private val newsFeedRepository: NewsFeedRepository
) : ViewModel() {

    private val _error = MutableSharedFlow<Exception>()
    val error = _error.asSharedFlow()

    private val _success = MutableSharedFlow<Boolean>()
    val success = _success.asSharedFlow()

    private val _uiState: MutableStateFlow<PostTransactionStep2UiState> = MutableStateFlow(Filling)
    val uiState: StateFlow<PostTransactionStep2UiState> = _uiState.asStateFlow()

    fun saveActivity(pokemonName: String, pokemonNumber: Int, transactionType: NewActivityType, transactionPrice: Int, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { Loading }
            try {
                newsFeedRepository.saveNewActivity(
                    NewActivity(
                        userName = "Super Collectionneur",
                        userImageUrl = null,
                        date = LocalDateTime.now(),
                        pokemonCard = UserPokemonCard(
                            pokemonId = pokemonNumber,
                            name = pokemonName,
                            imageSource = FileSource(uri),
                            totalVotes = 0,
                            hasMyVote = false
                        ),
                        activityType = transactionType,
                        price = transactionPrice
                    )
                )
                _success.emit(true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { Filling }
                _error.emit(e)
            }
        }
    }
}