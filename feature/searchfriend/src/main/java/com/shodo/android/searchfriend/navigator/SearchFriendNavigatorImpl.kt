package com.shodo.android.searchfriend.navigator

import android.content.Context
import android.content.Intent
import com.shodo.android.coreui.navigator.SearchFriendNavigator
import com.shodo.android.searchfriend.SearchFriendActivity

class SearchFriendNavigatorImpl : SearchFriendNavigator {
    override fun navigate(context: Context) {
        val intent = Intent(context, SearchFriendActivity::class.java)
        context.startActivity(intent)
    }
}
