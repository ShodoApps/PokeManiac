package com.shodo.android.dashboard.navigator

import android.content.Context
import android.content.Intent
import com.shodo.android.coreui.navigator.DashboardNavigator
import com.shodo.android.dashboard.DashboardActivity

class DashboardNavigatorImpl : DashboardNavigator {
    override fun navigate(context: Context) {
        val intent = Intent(context, DashboardActivity::class.java)
        context.startActivity(intent)
    }
}
