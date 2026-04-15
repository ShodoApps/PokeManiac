package com.shodo.android.searchfriend.di

import com.shodo.android.presentation.searchfriend.SearchFriendScreenModel
import kotlinx.coroutines.CoroutineScope

/**
 * Creates a [SearchFriendScreenModel] bound to the given [CoroutineScope] (typically [androidx.lifecycle.ViewModel.viewModelScope]).
 *
 * Registered in Koin as a **factory** so each AndroidX `ViewModel` instance gets a screen coordinator tied to its lifecycle scope
 * (same idea as `factory { (scope: CoroutineScope) -> … }` + `get { parametersOf(scope) }` — see `viewmodel-patterns.mdc`).
 */
fun interface SearchFriendScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): SearchFriendScreenModel
}
