package com.shodo.android.pokemaniac

import android.app.Application
import com.shodo.android.billing.di.billingModule
import com.shodo.android.dashboard.di.dashboardModule
import com.shodo.android.di.trackingModule
import com.shodo.android.pokemaniac.di.appCoreArchiModules
import com.shodo.android.myfriends.di.myFriendsModule
import com.shodo.android.myprofile.di.myProfileModule
import com.shodo.android.welcome.di.welcomeModule
import com.shodo.android.posttransaction.di.postTransactionModule
import com.shodo.android.searchfriend.di.searchFriendModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PokeManiacApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PokeManiacApplication)

            // Core archi: Room (Android) + `:shared:di` (`commonMain` Koin modules)
            modules(appCoreArchiModules())

            // Third Party Modules
            modules(trackingModule)

            // Feature Modules
            modules(
                welcomeModule,
                dashboardModule,
                searchFriendModule,
                myFriendsModule,
                postTransactionModule,
                myProfileModule,
                billingModule
            )
        }
    }
}
