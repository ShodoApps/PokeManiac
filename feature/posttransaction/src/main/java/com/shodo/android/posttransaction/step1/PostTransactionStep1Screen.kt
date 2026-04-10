package com.shodo.android.posttransaction.step1

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shodo.android.coreui.R
import com.shodo.android.coreui.extensions.observeWithLifecycle
import com.shodo.android.posttransaction.step1.ui.PostTransactionStep1View
import kotlinx.coroutines.launch

/**
 * PostTransactionStep1Screen is a container composable responsible for:
 * - Collecting the UI state from the PostTransactionStep1ViewModel.
 * - Handling lifecycle events to trigger ViewModel actions.
 * - Displaying error messages using a Snackbar.
 * - Delegating the UI rendering to the stateless PostTransactionStep1View composable.
 *
 * @param modifier            Modifier to apply to the root of the screen.
 * @param viewModel           The ViewModel handling the logic to save a photo.
 * @param onNextStep          Callback to navigate to the next step to post a transaction.
 * @param onBackPressed       Callback triggered when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTransactionStep1Screen(
    modifier: Modifier = Modifier,
    viewModel: PostTransactionStep1ViewModel,
    onNextStep: (Uri) -> Unit,
    onBackPressed: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.error.observeWithLifecycle { error ->
        snackbarHostState.showSnackbar(error.message.toString())
    }

    val cameraPermissionDeniedText = stringResource(R.string.camera_permission_denied)
    PostTransactionStep1View(
        modifier = modifier,
        onNextStep = onNextStep,
        createImageFile = viewModel::createImageFile,
        getUriForImageFile = viewModel::getUriForFile,
        onBackPressed = onBackPressed,
        onCameraPermissionDenied = { coroutineScope.launch { snackbarHostState.showSnackbar(cameraPermissionDeniedText) } },
        snackbarHostState = snackbarHostState
    )
}
