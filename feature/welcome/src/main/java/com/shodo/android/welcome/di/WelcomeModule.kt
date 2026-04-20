package com.shodo.android.welcome.di

import com.shodo.android.presentation.welcome.WelcomeScreenModel
import com.shodo.android.welcome.WelcomeViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val welcomeModule = module {
    factory<WelcomeScreenModelFactory> {
        WelcomeScreenModelFactory { scope: CoroutineScope ->
            WelcomeScreenModel(coroutineScope = scope)
        }
    }
    viewModelOf(::WelcomeViewModel)
}
