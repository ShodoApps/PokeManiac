package com.shodo.android.coreui.navigationtransitions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder

fun NavGraphBuilder.enterTransition(route: String?): AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards EnterTransition? = {
    when (initialState.destination.route) {
        route -> slideIntoContainer(Left, animationSpec = tween(200))
        else -> null
    }
}

fun NavGraphBuilder.exitTransition(route: String?): AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards ExitTransition? = {
    when (targetState.destination.route) {
        route -> slideOutOfContainer(Left, animationSpec = tween(200))
        else -> null
    }
}
fun NavGraphBuilder.popEnterTransition(route: String?): AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards EnterTransition? = {
    when (initialState.destination.route) {
        route -> slideIntoContainer(Right, animationSpec = tween(200))
        else -> null
    }
}

fun NavGraphBuilder.popExitTransition(route: String?): AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards ExitTransition? = {
    when (targetState.destination.route) {
        route -> slideOutOfContainer(Right, animationSpec = tween(200))
        else -> null
    }
}
