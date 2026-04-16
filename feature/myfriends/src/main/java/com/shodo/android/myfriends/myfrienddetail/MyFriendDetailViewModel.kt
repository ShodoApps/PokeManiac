package com.shodo.android.myfriends.myfrienddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shodo.android.myfriends.di.MyFriendDetailScreenModelFactory
import com.shodo.android.presentation.myfriends.MyFriendDetailScreenModel

/**
 * AndroidX [ViewModel] for My Friend detail. Forwards to [MyFriendDetailScreenModel] in `:shared:presentation`.
 */
class MyFriendDetailViewModel(
    private val screenModelFactory: MyFriendDetailScreenModelFactory,
) : ViewModel() {

    private val screenModel: MyFriendDetailScreenModel by lazy {
        screenModelFactory.create(viewModelScope)
    }

    val error get() = screenModel.error
    val unsubscribed get() = screenModel.unsubscribed
    val uiState get() = screenModel.uiState

    fun start(id: String) = screenModel.start(id)

    fun unsubscribeFriend(friendId: String) = screenModel.unsubscribeFriend(friendId)
}
