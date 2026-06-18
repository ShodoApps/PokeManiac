package com.shodo.android.posttransaction

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
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
import com.shodo.android.posttransaction.Routes.Step1
import com.shodo.android.posttransaction.Routes.Step2
import com.shodo.android.posttransaction.step1.PostTransactionStep1Screen
import com.shodo.android.posttransaction.step2.PostTransactionStep2Screen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

class PostTransactionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeManiacTheme {
                PostTransactionFeature(onBackPressed = onBackPressedDispatcher::onBackPressed) { finish() }
            }
        }
    }
}

@Composable
fun PostTransactionFeature(onBackPressed: () -> Unit, onActivitySaved: () -> Unit) {
    val navController = rememberNavController()
    PostTransactionNavHost(navController = navController, onBackPressed = onBackPressed, onActivitySaved = onActivitySaved)
}

sealed class Routes {
    @Serializable
    data object Step1 : Routes()

    @Serializable
    data class Step2(val uri: String) : Routes()
}

@Composable
fun PostTransactionNavHost(modifier: Modifier = Modifier, navController: NavHostController, onBackPressed: () -> Unit, onActivitySaved: () -> Unit) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Step1
    ) {
        composable<Step1>(
            enterTransition = enterTransition(Step2::class.java.canonicalName),
            exitTransition = exitTransition(Step2::class.java.canonicalName),
            popEnterTransition = popEnterTransition(Step2::class.java.canonicalName),
            popExitTransition = popExitTransition(Step2::class.java.canonicalName)
        ) {
            PostTransactionStep1Screen(
                viewModel = koinViewModel(),
                onNextStep = { imageUri -> navController.navigate(Step2(imageUri.toString())) },
                onBackPressed = onBackPressed
            )
        }

        composable<Step2>(
            enterTransition = enterTransition(Step1::class.java.canonicalName),
            exitTransition = exitTransition(Step1::class.java.canonicalName),
            popEnterTransition = popEnterTransition(Step1::class.java.canonicalName),
            popExitTransition = popExitTransition(Step1::class.java.canonicalName)
        ) { backStackEntry ->
            val step2Arg: Step2 = backStackEntry.toRoute()
            PostTransactionStep2Screen(
                viewModel = koinViewModel(),
                imageUri = step2Arg.uri.toUri(),
                onBackPressed = onBackPressed,
                onActivitySaved = onActivitySaved
            )
        }
    }
}
