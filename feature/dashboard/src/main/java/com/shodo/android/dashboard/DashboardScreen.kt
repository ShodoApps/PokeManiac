package com.shodo.android.dashboard

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.shodo.android.coreui.extensions.OnLifecycleEventEffect
import com.shodo.android.coreui.extensions.observeWithLifecycle
import com.shodo.android.dashboard.ui.DashboardView

/**
 * DashboardScreen is a container composable responsible for:
 * - Collecting the UI state from the DashboardViewModel.
 * - Handling lifecycle events to trigger ViewModel actions.
 * - Displaying error messages using a Snackbar.
 * - Delegating the UI rendering to the stateless DashboardView composable.
 *
 * @param modifier Modifier to be applied to the composable.
 * @param viewModel The ViewModel providing the UI state and handling actions.
 * @param lifecycleOwner The LifecycleOwner used to observe lifecycle events.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.error.observeWithLifecycle(lifecycleOwner) { error ->
        snackbarHostState.showSnackbar(error.message)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnLifecycleEventEffect(Lifecycle.Event.ON_START, lifecycleOwner) { viewModel.start() }

    val context = LocalContext.current
    val onFriendsPressed = remember(viewModel, context) { { viewModel.navigateToMyFriends(context) } }
    val onProfilePressed = remember(viewModel, context) { { viewModel.navigateToMyProfile(context) } }
    val onSearchFriendsPressed = remember(viewModel, context) { { viewModel.navigateToSearchFriends(context) } }
    val onPostTransactionPressed = remember(viewModel, context) { { viewModel.navigateToPostTransaction(context) } }

    DashboardView(
        modifier = modifier,
        uiState = uiState,
        onRefresh = viewModel::refreshNewsFeed,
        onFriendsPressed = onFriendsPressed,
        onProfilePressed = onProfilePressed,
        onSearchFriendsPressed = onSearchFriendsPressed,
        onPostTransactionPressed = onPostTransactionPressed,
        snackbarHostState = snackbarHostState
    )
}