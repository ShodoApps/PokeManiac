package com.shodo.android.billing

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.shodo.android.billing.ui.BillingView
import com.shodo.android.coreui.extensions.OnLifecycleEventEffect
import com.shodo.android.coreui.extensions.observeWithLifecycle

@Composable
fun BillingScreen(
    modifier: Modifier = Modifier,
    viewModel: BillingViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onBackPressed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.error.observeWithLifecycle(lifecycleOwner) { error ->
        snackbarHostState.showSnackbar(error.message)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnLifecycleEventEffect(Lifecycle.Event.ON_START, lifecycleOwner) { viewModel.start() }

    BillingView(
        modifier = modifier,
        uiState = uiState,
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState
    )
}