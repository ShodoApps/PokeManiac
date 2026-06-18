package com.shodo.android.posttransaction.navigator

import android.content.Context
import android.content.Intent
import com.shodo.android.coreui.navigator.PostTransactionNavigator
import com.shodo.android.posttransaction.PostTransactionActivity

class PostTransactionNavigatorImpl : PostTransactionNavigator {
    override fun navigate(context: Context) {
        val intent = Intent(context, PostTransactionActivity::class.java)
        context.startActivity(intent)
    }
}
