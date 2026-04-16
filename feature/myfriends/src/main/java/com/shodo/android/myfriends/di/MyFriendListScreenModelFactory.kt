package com.shodo.android.myfriends.di

import com.shodo.android.presentation.myfriends.MyFriendListScreenModel
import kotlinx.coroutines.CoroutineScope

/**
 * Creates a [MyFriendListScreenModel] bound to the given [CoroutineScope] (typically [androidx.lifecycle.ViewModel.viewModelScope]).
 */
fun interface MyFriendListScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): MyFriendListScreenModel
}
