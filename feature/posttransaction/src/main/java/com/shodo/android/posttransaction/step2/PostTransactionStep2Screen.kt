package com.shodo.android.posttransaction.step2

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shodo.android.coreui.extensions.observeWithLifecycle
import com.shodo.android.posttransaction.step2.ui.PostTransactionStep2View

/**
 * PostTransactionStep2Screen is a container composable responsible for:
 * - Collecting the UI state from the PostTransactionStep2ViewModel.
 * - Handling lifecycle events to trigger ViewModel actions.
 * - Displaying error messages using a Snackbar.
 * - Delegating the UI rendering to the stateless PostTransactionStep2View composable.
 *
 * @param modifier          Modifier to apply to the root of the screen.
 * @param imageUri          The local uri of the taken image of the new transaction's post.
 * @param viewModel         The ViewModel handling the logic to save a photo.
 * @param onActivitySaved   Callback to navigate to dashboard when the new Transaction's post is saved.
 * @param onBackPressed     Callback triggered when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTransactionStep2Screen(
    modifier: Modifier = Modifier,
    imageUri: Uri,
    viewModel: PostTransactionStep2ViewModel,
    onBackPressed: () -> Unit,
    onActivitySaved: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.error.observeWithLifecycle { error ->
        snackbarHostState.showSnackbar(error.message)
    }

    val currentOnActivitySaved by rememberUpdatedState(onActivitySaved)
    viewModel.success.observeWithLifecycle { currentOnActivitySaved() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostTransactionStep2View(
        modifier = modifier,
        imageUri = imageUri,
        uiState = uiState,
        onBackPressed = onBackPressed,
        onSaveActivity = viewModel::saveActivity,
        snackbarHostState = snackbarHostState
    )
}
