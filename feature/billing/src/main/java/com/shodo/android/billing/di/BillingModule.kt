package com.shodo.android.billing.di

import com.shodo.android.billing.BillingViewModel
import com.shodo.android.billing.navigator.BillingNavigatorImpl
import com.shodo.android.coreui.navigator.BillingNavigator
import com.shodo.android.presentation.billing.BillingScreenModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val billingModule = module {
    single<BillingNavigator> { BillingNavigatorImpl() }
    factory<BillingScreenModelFactory> {
        BillingScreenModelFactory { scope: CoroutineScope ->
            BillingScreenModel(coroutineScope = scope)
        }
    }
    viewModelOf(::BillingViewModel)
}