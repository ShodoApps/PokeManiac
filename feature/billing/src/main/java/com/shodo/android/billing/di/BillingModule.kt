package com.shodo.android.billing.di

import com.shodo.android.billing.BillingViewModel
import com.shodo.android.billing.navigator.BillingNavigatorImpl
import com.shodo.android.coreui.navigator.BillingNavigator
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val billingModule = module {
    single<BillingNavigator> { BillingNavigatorImpl() }
    viewModelOf(::BillingViewModel)
}