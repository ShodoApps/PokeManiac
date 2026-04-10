package com.shodo.android.dashboard

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.shodo.android.dashboard.ui.DashboardView
import kotlinx.coroutines.flow.collectLatest

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
    LaunchedEffect(Unit) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(error.message.toString())
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == ON_START) {
                viewModel.start()
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    val context = LocalContext.current

    DashboardView(
        modifier = modifier,
        uiState = uiState,
        onRefresh = viewModel::refreshNewsFeed,
        onFriendsPressed = { viewModel.navigateToMyFriends(context) },
        onProfilePressed = { viewModel.navigateToMyProfile(context) },
        onSearchFriendsPressed = { viewModel.navigateToSearchFriends(context) },
        onPostTransactionPressed = { viewModel.navigateToPostTransaction(context) },
        snackbarHostState = snackbarHostState
    )
}