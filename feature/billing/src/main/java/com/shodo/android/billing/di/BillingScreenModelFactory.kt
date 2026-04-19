package com.shodo.android.billing.di

import com.shodo.android.presentation.billing.BillingScreenModel
import kotlinx.coroutines.CoroutineScope

fun interface BillingScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): BillingScreenModel
}
