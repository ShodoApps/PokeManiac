package com.shodo.android.myfriends.myfrienddetail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.shodo.android.myfriends.myfrienddetail.ui.MyFriendDetailView
import kotlinx.coroutines.flow.collectLatest

/**
 * MyFriendDetailScreen is a container composable responsible for:
 * - Collecting the UI state from the MyFriendDetailViewModel.
 * - Handling lifecycle events to trigger ViewModel actions.
 * - Displaying error messages using a Snackbar.
 * - Navigating back when the Friend is unsubscribed.
 * - Delegating the UI rendering to the stateless MyFriendDetailView composable.
 *
 * @param modifier          Modifier to be applied to the composable.
 * @param viewModel         The ViewModel providing the UI state and handling actions.
 * @param lifecycleOwner    The LifecycleOwner used to observe lifecycle events.
 * @param friendId          The displayed Friend's friendId, used to get the Friend's data from the ViewModel
 * @param friendName        The displayed Friend's friendName, used to get the Friend's data from the ViewModel
 * @param onBackPressed     Callback triggered when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFriendDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: MyFriendDetailViewModel,
    friendId: String,
    friendName: String,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onBackPressed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(error.message.toString())
        }
    }
    LaunchedEffect(Unit) {
        viewModel.unsubscribed.collectLatest {
            onBackPressed()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == ON_START) {
                viewModel.start(friendId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    MyFriendDetailView(
        modifier = modifier,
        uiState = uiState,
        friendName = friendName,
        onUnsubscribePressed = { friendId -> viewModel.unsubscribeFriend(friendId) },
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState
    )
}
