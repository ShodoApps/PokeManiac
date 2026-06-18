package com.shodo.android.searchfriend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.coreui.ui.GenericEmptyScreen
import com.shodo.android.coreui.ui.GenericLoader
import com.shodo.android.presentation.searchfriend.SearchFriendUiModel
import com.shodo.android.presentation.searchfriend.SearchFriendUiState
import com.shodo.android.presentation.searchfriend.SearchFriendUiState.Data
import com.shodo.android.presentation.searchfriend.SearchFriendUiState.EmptyResult
import com.shodo.android.presentation.searchfriend.SearchFriendUiState.EmptySearch
import com.shodo.android.presentation.searchfriend.SearchFriendUiState.Loading
import com.shodo.android.presentation.searchfriend.SubscriptionState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * Stateless Search Friend **UI** (Compose): scaffold, top bar, loader / empty / grid of results.
 *
 * [uiState] and list row models come from `:shared:presentation`
 * ([com.shodo.android.presentation.searchfriend.SearchFriendUiState],
 * [com.shodo.android.presentation.searchfriend.SearchFriendUiModel]); this composable has no ViewModel reference.
 *
 * @param modifier Modifier for the root [androidx.compose.material3.Scaffold].
 * @param uiState Current screen state (`Loading`, `Data`, `EmptySearch`, `EmptyResult`).
 * @param onSearchFriend Invoked with the search query string.
 * @param onSubscribeFriendPressed Invoked with the friend id to subscribe.
 * @param onUnsubscribeFriendPressed Invoked with the friend id to unsubscribe.
 * @param onBackPressed Invoked when the user requests back navigation.
 * @param snackbarHostState Host state for transient messages (e.g. errors collected in [com.shodo.android.searchfriend.SearchFriendScreen]).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFriendView(
    modifier: Modifier = Modifier,
    uiState: SearchFriendUiState,
    onSearchFriend: (String) -> Unit,
    onBackPressed: () -> Unit,
    onSubscribeFriendPressed: (String) -> Unit,
    onUnsubscribeFriendPressed: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.backgroundApp),
        topBar = { SearchFriendTopBar(onSearchFriend = onSearchFriend, onBackPressed = onBackPressed) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = dimens.separator)
                    .background(color = colors.tertiary)
                    .padding(top = dimens.small)
            )

            when (val state = uiState) {
                Loading -> GenericLoader()
                EmptySearch -> GenericEmptyScreen(stringResource(R.string.search_friends_description))
                is EmptyResult -> GenericEmptyScreen(stringResource(R.string.search_friends_no_result, state.query))
                is Data -> SearchFriendContent(
                    modifier = Modifier.padding(top = dimens.small),
                    users = state.people,
                    onSubscribeUserPressed = onSubscribeFriendPressed,
                    onUnsubscribeUserPressed = onUnsubscribeFriendPressed
                )
            }
        }
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendView - Loading - LightTheme")
@Composable
fun PreviewSearchFriendView_Loading_LightTheme() {
    PreviewSearchFriendView(false, Loading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendView - Loading - DarkTheme")
@Composable
fun PreviewSearchFriendView_Loading_DarkTheme() {
    PreviewSearchFriendView(true, Loading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendView - No Query - LightTheme")
@Composable
fun PreviewSearchFriendView_NoQuery_LightTheme() {
    PreviewSearchFriendView(false, EmptySearch)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendView - No Query - DarkTheme")
@Composable
fun PreviewSearchFriendView_NoQuery_DarkTheme() {
    PreviewSearchFriendView(true, EmptySearch)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendView - No User Found - LightTheme")
@Composable
fun PreviewSearchFriendView_NoUserFound_LightTheme() {
    PreviewSearchFriendView(false, EmptyResult("query"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendView - No User Found - DarkTheme")
@Composable
fun PreviewSearchFriendView_NoUserFound_DarkTheme() {
    PreviewSearchFriendView(true, EmptyResult("query"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendView - With Users Found - LightTheme")
@Composable
fun PreviewSearchFriendView_WithUsersFound_LightTheme() {
    PreviewSearchFriendView(false, Data(previewUsers()))
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendView - With Users Found - DarkTheme")
@Composable
fun PreviewSearchFriendView_WithUsersFound_DarkTheme() {
    PreviewSearchFriendView(true, Data(previewUsers()))
}

@Composable
private fun PreviewSearchFriendView(darkTheme: Boolean, uiState: SearchFriendUiState) {
    PokeManiacTheme(darkTheme = darkTheme) {
        SearchFriendView(
            uiState = uiState,
            onSearchFriend = {},
            onBackPressed = {},
            onSubscribeFriendPressed = {},
            onUnsubscribeFriendPressed = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

private fun previewUsers(): PersistentList<SearchFriendUiModel> = persistentListOf(
    SearchFriendUiModel(
        id = "friendId",
        name = "friendName",
        imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
        description = "description",
        pokemonCards = persistentListOf(),
        subscriptionState = SubscriptionState.Subscribed
    ),
    SearchFriendUiModel(
        id = "friendId1",
        name = "friendName1",
        imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/891.jpg",
        description = "description",
        pokemonCards = persistentListOf(),
        subscriptionState = SubscriptionState.NotSubscribed
    ),
    SearchFriendUiModel(
        id = "friendId2",
        name = "friendName2",
        imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/1345.jpg",
        description = "description",
        pokemonCards = persistentListOf(),
        subscriptionState = SubscriptionState.NotSubscribed
    )
)

//endregion Previews
