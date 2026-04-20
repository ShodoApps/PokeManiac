package com.shodo.android.presentation.posttransaction

import com.shodo.android.domain.repositories.entities.ImageSource.FileSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import com.shodo.android.domain.repositories.news.NewsFeedRepository
import com.shodo.android.presentation.PresentationError
import com.shodo.android.presentation.presentationIoDispatcher
import com.shodo.android.presentation.posttransaction.PostTransactionStep2UiState.Filling
import com.shodo.android.presentation.posttransaction.PostTransactionStep2UiState.Loading
import kotlin.time.Clock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PostTransactionStep2ScreenModel(
    private val newsFeedRepository: NewsFeedRepository,
    private val coroutineScope: CoroutineScope,
) {

    private val _error = MutableSharedFlow<PresentationError>()
    val error = _error.asSharedFlow()

    private val _success = MutableSharedFlow<Boolean>()
    val success = _success.asSharedFlow()

    private val _uiState: MutableStateFlow<PostTransactionStep2UiState> = MutableStateFlow(Filling)
    val uiState = _uiState.asStateFlow()

    fun saveActivity(
        pokemonName: String,
        pokemonNumber: Int,
        transactionType: NewActivityType,
        transactionPrice: Int,
        imageUriString: String
    ) {
        coroutineScope.launch {
            _uiState.update { Loading }
            try {
                withContext(presentationIoDispatcher) {
                    newsFeedRepository.saveNewActivity(
                        NewActivity(
                            userName = "Super Collectionneur",
                            userImageUrl = null,
                            date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                            pokemonCard = UserPokemonCard(
                                pokemonId = pokemonNumber,
                                name = pokemonName,
                                imageSource = FileSource(imageUriString),
                                totalVotes = 0,
                                hasMyVote = false
                            ),
                            activityType = transactionType,
                            price = transactionPrice
                        )
                    )
                }
                _success.emit(true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { Filling }
                _error.emit(PresentationError.from(e))
            }
        }
    }
}
