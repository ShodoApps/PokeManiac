package com.shodo.android.myfriends.myfriendlist.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.ui.GenericEmptyScreen
import com.shodo.android.coreui.ui.GenericLoader
import com.shodo.android.myfriends.myfriendlist.MyFriendListUiState
import com.shodo.android.myfriends.myfriendlist.MyFriendListUiState.Data
import com.shodo.android.myfriends.myfriendlist.MyFriendListUiState.Empty
import com.shodo.android.myfriends.myfriendlist.MyFriendListUiState.Loading
import com.shodo.android.myfriends.uimodel.MyFriendUI
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * Main Composable displaying the application's MyFriendList screen.
 * It uses a `Scaffold` to display a top bar (`MyFriendListTopBar`),
 * main content, and a `SnackbarHost` for displaying temporary messages.
 *
 * @param modifier           Modifier to customize the root layout.
 * @param uiState            UI state representing the current screen state. Possible states:
 *                           - `Loading`: Displays a loading indicator.
 *                           - `Data`: Displays a list of subscribed friends.
 *                           - `Empty`: Displays a message indicating no friends yet.
 *
 * @param onFriendPressed      Callback to navigate to the friend's detail.
 * @param onSearchFriendsPressed    Callback to navigate to the search friends screen.
 * @param onUnsubscribePressed    Callback triggered when a user clicks "unsubscribe" on a friend.
 * @param onBackPressed        Callback to navigate back.
 * @param snackbarHostState    State of the `SnackbarHost` to display temporary messages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFriendListView(
    modifier: Modifier = Modifier,
    uiState: MyFriendListUiState,
    onFriendPressed: (id: String) -> Unit,
    onSearchFriendsPressed: () -> Unit,
    onUnsubscribePressed: (String) -> Unit,
    onBackPressed: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.backgroundApp),
        topBar = {
            MyFriendListTopBar(
                onSearchFriendsPressed = onSearchFriendsPressed,
                onBackPressed = onBackPressed
            )
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
                Empty -> GenericEmptyScreen(stringResource(R.string.no_friends_yet))
                is Data -> MyFriendsListContent(
                    friends = state.friends,
                    onFriendClicked = onFriendPressed,
                    onUnsubscribeFriend = onUnsubscribePressed
                )
            }
        }
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListView - Loading - LightTheme")
@Composable
fun PreviewMyFriendsListView_Loading_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyFriendListView(
            uiState = Loading,
            onFriendPressed = {},
            onSearchFriendsPressed = {},
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListView - Loading - DarkTheme")
@Composable
fun PreviewMyFriendsListView_Loading_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyFriendListView(
            uiState = Loading,
            onFriendPressed = {},
            onSearchFriendsPressed = {},
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListView - Empty - LightTheme")
@Composable
fun PreviewMyFriendsListView_Empty_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyFriendListView(
            uiState = Empty,
            onFriendPressed = {},
            onSearchFriendsPressed = {},
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListView - Empty - DarkTheme")
@Composable
fun PreviewMyFriendsListView_Empty_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyFriendListView(
            uiState = Empty,
            onFriendPressed = {},
            onSearchFriendsPressed = {},
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListView - Data - LightTheme")
@Composable
fun PreviewMyFriendsListView_Data_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyFriendListView(
            uiState = Data(previewFriends()),
            onFriendPressed = {},
            onSearchFriendsPressed = {},
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListView - Data - DarkTheme")
@Composable
fun PreviewMyFriendsListView_Data_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyFriendListView(
            uiState = Data(previewFriends()),
            onFriendPressed = {},
            onSearchFriendsPressed = {},
            onUnsubscribePressed = {},
            onBackPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

private fun previewFriends(): PersistentList<MyFriendUI> = persistentListOf(
    MyFriendUI(
        id = "friendId",
        name = "friendName",
        imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
        description = "description",
        pokemonCards = persistentListOf()
    ),
    MyFriendUI(
        id = "friendId1",
        name = "friendName1",
        imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/891.jpg",
        description = "description",
        pokemonCards = persistentListOf()
    ),
    MyFriendUI(
        id = "friendId2",
        name = "friendName2",
        imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/1345.jpg",
        description = "description",
        pokemonCards = persistentListOf()
    )
)

//endregion Previews