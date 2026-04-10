package com.shodo.android.myfriends

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shodo.android.coreui.navigationtransitions.enterTransition
import com.shodo.android.coreui.navigationtransitions.exitTransition
import com.shodo.android.coreui.navigationtransitions.popEnterTransition
import com.shodo.android.coreui.navigationtransitions.popExitTransition
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.myfriends.Routes.MyFriendDetail
import com.shodo.android.myfriends.Routes.MyFriendList
import com.shodo.android.myfriends.myfrienddetail.MyFriendDetailScreen
import com.shodo.android.myfriends.myfriendlist.MyFriendListScreen
import org.koin.androidx.compose.koinViewModel
import kotlinx.serialization.Serializable

class MyFriendsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeManiacTheme {
                MyFriendsFeature(onBackPressed = onBackPressedDispatcher::onBackPressed)
            }
        }
    }
}

@Composable
fun MyFriendsFeature(onBackPressed: () -> Unit) {
    val navController = rememberNavController()
    MyFriendsNavHost(navController = navController, onBackPressed = onBackPressed)
}

sealed class Routes {
    @Serializable
    data object MyFriendList: Routes()

    @Serializable
    data class MyFriendDetail(val id: String): Routes()
}

@Composable
fun MyFriendsNavHost(modifier: Modifier = Modifier, navController: NavHostController, onBackPressed: () -> Unit) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MyFriendList
    ) {
        composable<MyFriendList>(
            enterTransition = enterTransition(MyFriendDetail::class.java.canonicalName),
            exitTransition = exitTransition(MyFriendDetail::class.java.canonicalName),
            popEnterTransition = popEnterTransition(MyFriendDetail::class.java.canonicalName),
            popExitTransition = popExitTransition(MyFriendDetail::class.java.canonicalName)
        ) {
            MyFriendListScreen(
                modifier = Modifier,
                viewModel = koinViewModel(),
                onFriendPressed = { friendId -> navController.navigate(MyFriendDetail(friendId)) },
                onBackPressed = onBackPressed
            )
        }

        composable<MyFriendDetail>(
            enterTransition = enterTransition(MyFriendList::class.java.canonicalName),
            exitTransition = exitTransition(MyFriendList::class.java.canonicalName),
            popEnterTransition = popEnterTransition(MyFriendList::class.java.canonicalName),
            popExitTransition = popExitTransition(MyFriendList::class.java.canonicalName)
        ) { backStackEntry ->
            val myFriendDetail: MyFriendDetail = backStackEntry.toRoute()
            MyFriendDetailScreen(
                friendId = myFriendDetail.id,
                viewModel = koinViewModel(),
                onBackPressed = onBackPressed
            )
        }
    }
}