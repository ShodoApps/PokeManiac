package com.shodo.android.myfriends.di

import com.shodo.android.coreui.navigator.MyFriendsNavigator
import com.shodo.android.myfriends.myfrienddetail.MyFriendDetailViewModel
import com.shodo.android.myfriends.myfriendlist.MyFriendListViewModel
import com.shodo.android.myfriends.navigator.MyFriendsNavigatorImpl
import com.shodo.android.presentation.myfriends.MyFriendDetailScreenModel
import com.shodo.android.presentation.myfriends.MyFriendListScreenModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val myFriendsModule = module {
    single<MyFriendsNavigator> { MyFriendsNavigatorImpl() }
    factory<MyFriendListScreenModelFactory> {
        MyFriendListScreenModelFactory { scope: CoroutineScope ->
            MyFriendListScreenModel(
                userRepository = get(),
                coroutineScope = scope,
            )
        }
    }
    factory<MyFriendDetailScreenModelFactory> {
        MyFriendDetailScreenModelFactory { scope: CoroutineScope ->
            MyFriendDetailScreenModel(
                userRepository = get(),
                coroutineScope = scope,
            )
        }
    }
    viewModelOf(::MyFriendListViewModel)
    viewModelOf(::MyFriendDetailViewModel)
}