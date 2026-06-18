package com.shodo.android.presentation

import com.shodo.android.data.ios.createUserRepositoryForIos
import com.shodo.android.presentation.searchfriend.SearchFriendScreenModel
import com.shodo.android.tracking.createTrackingRepositoryForIos
import kotlinx.coroutines.CoroutineScope

/** iOS composition root for Search Friend (real [UserRepositoryImpl] + SuperHero API + in-memory friends store). */
fun createSearchFriendScreenModelForIos(coroutineScope: CoroutineScope): SearchFriendScreenModel =
    SearchFriendScreenModel(
        userRepository = createUserRepositoryForIos(),
        trackingRepository = createTrackingRepositoryForIos(),
        coroutineScope = coroutineScope
    )
