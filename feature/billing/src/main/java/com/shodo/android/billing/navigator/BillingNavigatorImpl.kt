package com.shodo.android.billing.navigator

import android.content.Context
import android.content.Intent
import com.shodo.android.billing.BillingActivity
import com.shodo.android.coreui.navigator.BillingNavigator

class BillingNavigatorImpl : BillingNavigator {
    override fun navigate(context: Context) {
        val intent = Intent(context, BillingActivity::class.java)
        context.startActivity(intent)
    }
}
