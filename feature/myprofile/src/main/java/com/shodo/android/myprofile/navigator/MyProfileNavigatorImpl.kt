package com.shodo.android.myprofile.navigator

import android.content.Context
import android.content.Intent
import com.shodo.android.coreui.navigator.MyProfileNavigator
import com.shodo.android.myprofile.MyProfileActivity

class MyProfileNavigatorImpl : MyProfileNavigator {
    override fun navigate(context: Context) {
        val intent = Intent(context, MyProfileActivity::class.java)
        context.startActivity(intent)
    }
}
