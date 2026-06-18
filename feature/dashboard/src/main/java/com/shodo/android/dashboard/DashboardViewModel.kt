package com.shodo.android.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.coreui.navigator.MyFriendsNavigator
import com.shodo.android.coreui.navigator.MyProfileNavigator
import com.shodo.android.coreui.navigator.PostTransactionNavigator
import com.shodo.android.coreui.navigator.SearchFriendNavigator
import com.shodo.android.dashboard.di.DashboardScreenModelFactory
import com.shodo.android.presentation.dashboard.DashboardScreenModel

/**
 * AndroidX shell; logic in [DashboardScreenModel] (`:shared:presentation`).
 */
class DashboardViewModel(
    private val screenModelFactory: DashboardScreenModelFactory,
    private val searchFriendNavigator: SearchFriendNavigator,
    private val myFriendsNavigator: MyFriendsNavigator,
    private val myProfileNavigator: MyProfileNavigator,
    private val postTransactionNavigator: PostTransactionNavigator
) : ViewModel() {

    private val screenModel: DashboardScreenModel by lazy {
        screenModelFactory.create(viewModelScope)
    }

    val error get() = screenModel.error
    val uiState get() = screenModel.uiState

    fun start() = screenModel.start()

    fun refreshNewsFeed() = screenModel.refreshNewsFeed()

    fun navigateToSearchFriends(context: Context) {
        searchFriendNavigator.navigate(context)
    }

    fun navigateToMyFriends(context: Context) {
        myFriendsNavigator.navigate(context)
    }

    fun navigateToMyProfile(context: Context) {
        myProfileNavigator.navigate(context)
    }

    fun navigateToPostTransaction(context: Context) {
        postTransactionNavigator.navigate(context)
    }
}
