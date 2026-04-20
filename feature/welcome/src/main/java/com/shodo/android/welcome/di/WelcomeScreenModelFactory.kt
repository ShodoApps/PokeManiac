package com.shodo.android.welcome.di

import com.shodo.android.presentation.welcome.WelcomeScreenModel
import kotlinx.coroutines.CoroutineScope

fun interface WelcomeScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): WelcomeScreenModel
}
