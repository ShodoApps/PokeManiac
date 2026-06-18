package com.shodo.android.searchfriend.di

import com.shodo.android.coreui.navigator.SearchFriendNavigator
import com.shodo.android.presentation.searchfriend.SearchFriendScreenModel
import com.shodo.android.searchfriend.SearchFriendViewModel
import com.shodo.android.searchfriend.navigator.SearchFriendNavigatorImpl
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin bindings for Search Friend.
 *
 * [SearchFriendScreenModel] is created through [SearchFriendScreenModelFactory] so each [SearchFriendViewModel] passes
 * its own [androidx.lifecycle.ViewModel.viewModelScope] (explicit scope; same intent as `factory { (scope: CoroutineScope) -> … }`).
 */
val searchFriendModule = module {
    single<SearchFriendNavigator> { SearchFriendNavigatorImpl() }
    factory<SearchFriendScreenModelFactory> {
        SearchFriendScreenModelFactory { scope: CoroutineScope ->
            SearchFriendScreenModel(
                userRepository = get(),
                trackingRepository = get(),
                coroutineScope = scope
            )
        }
    }
    viewModelOf(::SearchFriendViewModel)
}
