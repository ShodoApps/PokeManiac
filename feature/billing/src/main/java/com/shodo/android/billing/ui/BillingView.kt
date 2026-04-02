package com.shodo.android.billing.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.shodo.android.billing.BillingUiState
import com.shodo.android.billing.BillingUiState.Data
import com.shodo.android.billing.BillingUiState.Error
import com.shodo.android.billing.BillingUiState.Loading
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.ui.GenericEmptyScreen
import com.shodo.android.coreui.ui.GenericLoader

@Composable
fun BillingView(
    modifier: Modifier = Modifier,
    uiState: BillingUiState,
    onBackPressed: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundApp),
        topBar = {
            BillingTopBar(onBackPressed)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp)
                .padding(innerPadding)
        ) {
            when (uiState) {
                Loading -> GenericLoader()
                Error -> GenericEmptyScreen(stringResource(R.string.billing_error))
                is Data -> BillingContent(uiState)
            }
        }
    }
}