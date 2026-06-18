package com.shodo.android.myfriends.myfrienddetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.ui.GenericLoader
import com.shodo.android.presentation.myfriends.MyFriendDetailUiState
import com.shodo.android.presentation.myfriends.MyFriendDetailUiState.Data
import com.shodo.android.presentation.myfriends.MyFriendDetailUiState.Loading
import com.shodo.android.presentation.myfriends.MyFriendUiModel
import kotlinx.collections.immutable.persistentListOf

/**
 * Main Composable displaying the application's MyFriendDetail screen.
 * It uses a `Scaffold` to display a top bar (`MyFriendDetailTopBar`),
 * main content, and a `SnackbarHost` for displaying temporary messages.
 *
 * @param modifier           Modifier to customize the root layout.
 * @param uiState            UI state representing the current screen state. Possible states:
 *                           - `Loading`: Displays a loading indicator.
 *                           - `Data`: Displays MyFriendDetail data.
 *
 * @param onUnsubscribePressed  Callback triggered when a user clicks "unsubscribe" on a friend.
 * @param onBackPressed         Callback to navigate back.
 * @param snackbarHostState     State of the `SnackbarHost` to display temporary messages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFriendDetailView(
    modifier: Modifier = Modifier,
    uiState: MyFriendDetailUiState,
    onUnsubscribePressed: (String) -> Unit,
    onBackPressed: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val friendName = if (uiState is Data) uiState.friend.name else ""

    Scaffold(
        modifier = modifier
            .background(color = colors.backgroundApp)
            .fillMaxSize(),
        topBar = {
            MyFriendDetailTopBar(friendName, onBackPressed)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp)
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                Loading -> GenericLoader()
                is Data -> FriendDetailContent(
                    friend = state.friend,
                    onUnsubscribePressed = {
                        onUnsubscribePressed(state.friend.id)
                    }
                )
            }
        }
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendDetailView - Loading - LightTheme")
@Composable
fun PreviewMyFriendDetailView_Loading_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyFriendDetailView(
            uiState = Loading,
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListView - Data - DarkTheme")
@Composable
fun PreviewMyFriendDetailView_Loading_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyFriendDetailView(
            uiState = Loading,
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendDetailView - Data - LightTheme")
@Composable
fun PreviewMyFriendDetailView_Data_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyFriendDetailView(
            uiState = Data(
                MyFriendUiModel(
                    id = "friendId",
                    name = "friendName",
                    imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
                    description = "description",
                    pokemonCards = persistentListOf()
                )
            ),
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListView - Data - DarkTheme")
@Composable
fun PreviewMyFriendDetailView_Data_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyFriendDetailView(
            uiState = Data(
                MyFriendUiModel(
                    id = "friendId",
                    name = "friendName",
                    imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
                    description = "description",
                    pokemonCards = persistentListOf()
                )
            ),
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

//endregion Previews
