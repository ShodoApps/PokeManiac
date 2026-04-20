package com.shodo.android.welcome

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.shodo.android.coreui.navigator.DashboardNavigator
import com.shodo.android.presentation.welcome.WelcomeUiEvent
import com.shodo.android.welcome.ui.WelcomeView
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    viewModel: WelcomeViewModel,
    onNavigationDone: () -> Unit
) {
    val context = LocalContext.current
    val dashboardNavigator: DashboardNavigator = koinInject()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is WelcomeUiEvent.ShowMessage ->
                    snackbarHostState.showSnackbar(event.message.message)
                WelcomeUiEvent.NavigateToDashboard -> {
                    dashboardNavigator.navigate(context)
                    onNavigationDone()
                }
            }
        }
    }

    WelcomeView(
        modifier = modifier,
        onSignUpClicked = viewModel::onSignUpClicked,
        onSignInClicked = viewModel::onSignInClicked,
        snackbarHostState = snackbarHostState
    )
}
