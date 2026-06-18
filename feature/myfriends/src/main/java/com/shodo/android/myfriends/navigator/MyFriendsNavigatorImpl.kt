package com.shodo.android.myfriends.navigator

import android.content.Context
import android.content.Intent
import com.shodo.android.coreui.navigator.MyFriendsNavigator
import com.shodo.android.myfriends.MyFriendsActivity

class MyFriendsNavigatorImpl : MyFriendsNavigator {
    override fun navigate(context: Context) {
        val intent = Intent(context, MyFriendsActivity::class.java)
        context.startActivity(intent)
    }
}
