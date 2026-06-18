package com.shodo.android.myprofile.di

import com.shodo.android.coreui.navigator.MyProfileNavigator
import com.shodo.android.myprofile.MyProfileViewModel
import com.shodo.android.myprofile.navigator.MyProfileNavigatorImpl
import com.shodo.android.presentation.myprofile.MyProfileScreenModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val myProfileModule = module {
    single<MyProfileNavigator> { MyProfileNavigatorImpl() }
    factory<MyProfileScreenModelFactory> {
        MyProfileScreenModelFactory { scope: CoroutineScope ->
            MyProfileScreenModel(
                myProfileRepository = get(),
                coroutineScope = scope
            )
        }
    }
    viewModelOf(::MyProfileViewModel)
}
