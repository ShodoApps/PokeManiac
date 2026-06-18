package com.shodo.android.posttransaction.di

import com.shodo.android.coreui.navigator.PostTransactionNavigator
import com.shodo.android.posttransaction.navigator.PostTransactionNavigatorImpl
import com.shodo.android.posttransaction.step1.PostTransactionStep1ViewModel
import com.shodo.android.posttransaction.step2.PostTransactionStep2ViewModel
import com.shodo.android.presentation.posttransaction.PostTransactionStep1ImageCapture
import com.shodo.android.presentation.posttransaction.PostTransactionStep1ImageCapturePort
import com.shodo.android.presentation.posttransaction.PostTransactionStep1ScreenModel
import com.shodo.android.presentation.posttransaction.PostTransactionStep2ScreenModel
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val postTransactionModule = module {
    single<PostTransactionNavigator> { PostTransactionNavigatorImpl() }
    single<PostTransactionStep1ImageCapturePort> {
        PostTransactionStep1ImageCapture(androidContext().applicationContext)
    }
    factory {
        PostTransactionStep1ScreenModel(
            imageCapture = get()
        )
    }
    viewModelOf(::PostTransactionStep1ViewModel)
    factory<PostTransactionStep2ScreenModelFactory> {
        PostTransactionStep2ScreenModelFactory { scope: CoroutineScope ->
            PostTransactionStep2ScreenModel(
                newsFeedRepository = get(),
                coroutineScope = scope
            )
        }
    }
    viewModelOf(::PostTransactionStep2ViewModel)
}
