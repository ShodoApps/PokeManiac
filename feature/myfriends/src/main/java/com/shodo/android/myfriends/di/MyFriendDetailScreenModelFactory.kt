package com.shodo.android.myfriends.di

import com.shodo.android.presentation.myfriends.MyFriendDetailScreenModel
import kotlinx.coroutines.CoroutineScope

/**
 * Creates a [MyFriendDetailScreenModel] bound to the given [CoroutineScope] (typically [androidx.lifecycle.ViewModel.viewModelScope]).
 */
fun interface MyFriendDetailScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): MyFriendDetailScreenModel
}
