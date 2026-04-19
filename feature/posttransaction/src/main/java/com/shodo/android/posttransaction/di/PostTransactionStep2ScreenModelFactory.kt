package com.shodo.android.posttransaction.di

import com.shodo.android.presentation.posttransaction.PostTransactionStep2ScreenModel
import kotlinx.coroutines.CoroutineScope

fun interface PostTransactionStep2ScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): PostTransactionStep2ScreenModel
}
