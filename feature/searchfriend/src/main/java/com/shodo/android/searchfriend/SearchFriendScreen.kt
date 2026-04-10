package com.shodo.android.searchfriend

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shodo.android.coreui.extensions.observeWithLifecycle
import com.shodo.android.searchfriend.ui.SearchFriendView

/**
 * SearchFriendScreen is a container composable responsible for:
 * - Collecting the UI state from the SearchFriendViewModel.
 * - Handling lifecycle events to trigger ViewModel actions.
 * - Displaying error messages using a Snackbar.
 * - Delegating the UI rendering to the stateless SearchFriendView composable.
 *
 * @param modifier            Modifier to apply to the root of the screen.
 * @param viewModel           The ViewModel handling the logic and state for the Search Friends functionality.
 * @param onBackPressed       Callback triggered when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFriendScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchFriendViewModel,
    onBackPressed: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.error.observeWithLifecycle { error ->
        snackbarHostState.showSnackbar(error.message.toString())
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchFriendView(
        modifier = modifier,
        uiState = uiState,
        onSearchFriend = viewModel::searchFriend,
        onSubscribeFriendPressed = viewModel::subscribeFriend,
        onUnsubscribeFriendPressed = viewModel::unsubscribeFriend,
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState
    )
}
