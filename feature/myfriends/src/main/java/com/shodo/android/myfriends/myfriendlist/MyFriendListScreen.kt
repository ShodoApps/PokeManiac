package com.shodo.android.myfriends.myfriendlist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shodo.android.coreui.extensions.OnLifecycleEventEffect
import com.shodo.android.coreui.extensions.observeWithLifecycle
import com.shodo.android.myfriends.myfriendlist.ui.MyFriendListView

/**
 * MyFriendListScreen is a container composable responsible for:
 * - Collecting the UI state from the MyFriendListViewModel.
 * - Handling lifecycle events to trigger ViewModel actions.
 * - Displaying error messages using a Snackbar.
 * - Delegating the UI rendering to the stateless MyFriendListView composable.
 *
 * @param modifier            Modifier to apply to the root of the screen.
 * @param viewModel           The ViewModel handling the logic and state for the friend list.
 * @param lifecycleOwner      The lifecycle owner to observe for automatic data refresh (default: current).
 * @param onFriendPressed     Callback triggered when a friend item is clicked.
 * @param onBackPressed       Callback triggered when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFriendListScreen(
    modifier: Modifier = Modifier,
    viewModel: MyFriendListViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onFriendPressed: (id: String) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.error.observeWithLifecycle(lifecycleOwner) { error ->
        snackbarHostState.showSnackbar(error.message)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnLifecycleEventEffect(Lifecycle.Event.ON_START, lifecycleOwner) { viewModel.fetchMyFriends() }

    val onSearchFriendsPressed = remember(viewModel, context) { { viewModel.navigateToSearchFriend(context) } }

    MyFriendListView(
        modifier = modifier,
        uiState = uiState,
        onFriendPressed = onFriendPressed,
        onSearchFriendsPressed = onSearchFriendsPressed,
        onUnsubscribePressed = viewModel::unsubscribeFriend,
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState
    )
}
