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
 * Search Friend **screen container** (Android / Compose): collects state, shows errors, delegates UI to [SearchFriendView].
 *
 * [viewModel] is the AndroidX wrapper in this module; screen state types come from `:shared:presentation`
 * ([com.shodo.android.presentation.searchfriend.SearchFriendUiState]). One-shot errors are
 * [com.shodo.android.presentation.PresentationError] values collected with lifecycle awareness and shown in a snackbar.
 *
 * @param modifier Modifier to apply to the root of the screen.
 * @param viewModel AndroidX [SearchFriendViewModel] that forwards to [com.shodo.android.presentation.searchfriend.SearchFriendScreenModel].
 * @param onBackPressed Invoked when the user navigates back (e.g. top bar).
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
        snackbarHostState.showSnackbar(error.message)
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
