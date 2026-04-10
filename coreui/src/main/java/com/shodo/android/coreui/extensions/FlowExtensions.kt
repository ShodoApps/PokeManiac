package com.shodo.android.coreui.extensions

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

/**
 * Collects this [Flow] with lifecycle awareness inside a Composable.
 *
 * Collection is paused when the lifecycle drops below [minActiveState] (e.g. when the app goes to
 * background) and automatically resumes when it returns. This prevents processing events or
 * triggering UI work (snackbars, navigation) while the screen is not visible.
 *
 * Note: [Flow.SharedFlow] has no replay by default, so events emitted while the lifecycle is below
 * [minActiveState] are dropped. This is intentional for error flows and navigation signals.
 *
 * Combine with [androidx.compose.runtime.rememberUpdatedState] when [action] captures a lambda
 * parameter that may change identity across recompositions.
 *
 * @param lifecycleOwner The lifecycle owner to observe. Defaults to [LocalLifecycleOwner.current].
 * @param minActiveState The minimum lifecycle state required for collection to be active.
 * @param action Suspend function invoked for each emitted value.
 */
@Composable
fun <T> Flow<T>.observeWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
) {
    LaunchedEffect(this, lifecycleOwner) {
        flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect(action)
    }
}
