package com.shodo.android.myfriends.myfriendlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.coreui.navigator.SearchFriendNavigator
import com.shodo.android.myfriends.di.MyFriendListScreenModelFactory
import com.shodo.android.presentation.myfriends.MyFriendListScreenModel

/**
 * AndroidX [ViewModel] for My Friends list. Forwards to [MyFriendListScreenModel] in `:shared:presentation`.
 */
class MyFriendListViewModel(
    private val screenModelFactory: MyFriendListScreenModelFactory,
    private val searchFriendNavigator: SearchFriendNavigator
) : ViewModel() {

    private val screenModel: MyFriendListScreenModel by lazy {
        screenModelFactory.create(viewModelScope)
    }

    val uiState get() = screenModel.uiState
    val error get() = screenModel.error

    fun fetchMyFriends() = screenModel.fetchMyFriends()

    fun unsubscribeFriend(friendId: String) = screenModel.unsubscribeFriend(friendId)

    fun navigateToSearchFriend(context: Context) {
        searchFriendNavigator.navigate(context)
    }
}
