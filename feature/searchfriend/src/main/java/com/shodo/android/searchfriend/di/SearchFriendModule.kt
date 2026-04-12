package com.shodo.android.searchfriend.di

import com.shodo.android.coreui.navigator.SearchFriendNavigator
import com.shodo.android.searchfriend.SearchFriendViewModel
import com.shodo.android.searchfriend.navigator.SearchFriendNavigatorImpl
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Koin bindings for Search Friend: AndroidX [com.shodo.android.searchfriend.SearchFriendViewModel] → [com.shodo.android.presentation.searchfriend.SearchFriendScreenModel]. */
val searchFriendModule = module {
    single<SearchFriendNavigator> { SearchFriendNavigatorImpl() }
    viewModelOf(::SearchFriendViewModel)
}