package com.shodo.android.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/** iOS SwiftUI bridge: [MainScope] for screen-level work; cancel when adapter tears down. */
fun createPresentationCoroutineScope(): CoroutineScope = MainScope()

fun cancelPresentationCoroutineScope(scope: CoroutineScope) {
    scope.cancel()
}
