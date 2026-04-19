package com.shodo.android.posttransaction.step2

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.posttransaction.di.PostTransactionStep2ScreenModelFactory
import com.shodo.android.presentation.posttransaction.PostTransactionStep2ScreenModel
class PostTransactionStep2ViewModel(
    screenModelFactory: PostTransactionStep2ScreenModelFactory
) : ViewModel() {

    private val screenModel: PostTransactionStep2ScreenModel by lazy {
        screenModelFactory.create(viewModelScope)
    }

    val error get() = screenModel.error
    val success get() = screenModel.success
    val uiState get() = screenModel.uiState

    fun saveActivity(
        pokemonName: String,
        pokemonNumber: Int,
        transactionType: NewActivityType,
        transactionPrice: Int,
        uri: Uri
    ) = screenModel.saveActivity(
        pokemonName,
        pokemonNumber,
        transactionType,
        transactionPrice,
        uri.toString()
    )
}
