package com.shodo.android.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.ui.GenericEmptyScreen
import com.shodo.android.coreui.ui.GenericLoader
import com.shodo.android.presentation.dashboard.DashboardImageSourceUiModel
import com.shodo.android.presentation.dashboard.DashboardUiState
import com.shodo.android.presentation.dashboard.DashboardUiState.Data
import com.shodo.android.presentation.dashboard.DashboardUiState.EmptyResult
import com.shodo.android.presentation.dashboard.DashboardUiState.Loading
import com.shodo.android.presentation.dashboard.NewActivityTypeUiModel
import com.shodo.android.presentation.dashboard.NewActivityUiModel
import com.shodo.android.presentation.dashboard.PokemonCardUiModel
import kotlinx.collections.immutable.persistentListOf

/**
 * Main Composable displaying the application's Dashboard screen.
 * It uses a `Scaffold` to display a top bar (`DashboardTopBar`),
 * main content, and a `SnackbarHost` for displaying temporary messages.
 *
 * @param modifier           Modifier to customize the root layout.
 * @param uiState            UI state representing the current screen state. Possible states:
 *                           - `Loading`: Displays a loading indicator.
 *                           - `Data`: Displays a list of recent activities.
 *                           - `EmptyResult`: Displays a message indicating no results found.
 * @param onRefresh           Callback triggered when a refresh action is performed (Pull to Refresh).
 * @param onFriendsPressed         Callback to navigate to the friends screen.
 * @param onProfilePressed         Callback to navigate to the profile screen.
 * @param onSearchFriendsPressed   Callback to navigate to the search friends screen.
 * @param onPostTransactionPressed Callback to initiate a post transaction or publish content.
 * @param snackbarHostState   State of the `SnackbarHost` to display temporary messages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardView(
    modifier: Modifier = Modifier,
    uiState: DashboardUiState,
    onRefresh: () -> Unit,
    onFriendsPressed: () -> Unit,
    onProfilePressed: () -> Unit,
    onSearchFriendsPressed: () -> Unit,
    onPostTransactionPressed: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundApp),
        topBar = {
            DashboardTopBar(
                onSearchFriendsPressed = onSearchFriendsPressed,
                onFriendsPressed = onFriendsPressed,
                onProfilePressed = onProfilePressed,
                onPostTransactionPressed = onPostTransactionPressed
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState is Loading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp)
                .padding(innerPadding),
            contentAlignment = Center
        ) {
            when (uiState) {
                Loading -> GenericLoader()
                EmptyResult -> GenericEmptyScreen(stringResource(R.string.no_friends_yet))
                is Data -> DashboardContent(newActivities = uiState.news)
            }
        }
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardView - Loading - LightTheme")
@Composable
fun PreviewDashboardView_Loading_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        DashboardView(
            uiState = Loading,
            onRefresh = {},
            onFriendsPressed = {},
            onProfilePressed = {},
            onSearchFriendsPressed = {},
            onPostTransactionPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardView - Loading - DarkTheme")
@Composable
fun PreviewDashboardView_Loading_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        DashboardView(
            uiState = Loading,
            onRefresh = {},
            onFriendsPressed = {},
            onProfilePressed = {},
            onSearchFriendsPressed = {},
            onPostTransactionPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardView - EmptyResult - LightTheme")
@Composable
fun PreviewDashboardView_EmptyResult_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        DashboardView(
            uiState = EmptyResult,
            onRefresh = {},
            onFriendsPressed = {},
            onProfilePressed = {},
            onSearchFriendsPressed = {},
            onPostTransactionPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardView - EmptyResult - DarkTheme")
@Composable
fun PreviewDashboardView_EmptyResult_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        DashboardView(
            uiState = EmptyResult,
            onRefresh = {},
            onFriendsPressed = {},
            onProfilePressed = {},
            onSearchFriendsPressed = {},
            onPostTransactionPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardView - Data - LightTheme")
@Composable
fun PreviewDashboardView_Data_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        DashboardView(
            uiState = Data(previewNewActivities()),
            onRefresh = {},
            onFriendsPressed = {},
            onProfilePressed = {},
            onSearchFriendsPressed = {},
            onPostTransactionPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardView - Data - DarkTheme")
@Composable
fun PreviewDashboardView_Data_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        DashboardView(
            uiState = Data(previewNewActivities()),
            onRefresh = {},
            onFriendsPressed = {},
            onProfilePressed = {},
            onSearchFriendsPressed = {},
            onPostTransactionPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

private fun previewNewActivities() = persistentListOf(
    NewActivityUiModel(
        id = "friendName" + "01/06/2025 18:30",
        friendName = "friendName",
        friendImageUrl = null,
        date = "01/06/2025 18:30",
        activityType = NewActivityTypeUiModel.Sale,
        pokemonCard = PokemonCardUiModel(
            name = "pokemonName",
            imageSource = DashboardImageSourceUiModel.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png")
        ),
        price = 30
    ),
    NewActivityUiModel(
        id = "friendName2" + "01/06/2025 12:30",
        friendName = "friendName2",
        friendImageUrl = null,
        date = "01/06/2025 12:30",
        activityType = NewActivityTypeUiModel.Purchase,
        pokemonCard = PokemonCardUiModel(
            name = "pokemonName",
            imageSource = DashboardImageSourceUiModel.UrlSource("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png")
        ),
        price = 30
    )
)

//endregion Previews
