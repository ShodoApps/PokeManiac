package com.shodo.android.myprofile.di

import com.shodo.android.presentation.myprofile.MyProfileScreenModel
import kotlinx.coroutines.CoroutineScope

fun interface MyProfileScreenModelFactory {
    fun create(coroutineScope: CoroutineScope): MyProfileScreenModel
}
