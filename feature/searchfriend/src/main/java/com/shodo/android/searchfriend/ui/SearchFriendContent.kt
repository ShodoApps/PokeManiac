package com.shodo.android.searchfriend.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.presentation.searchfriend.SearchFriendUiModel
import com.shodo.android.presentation.searchfriend.SubscriptionState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * Scrollable grid of search results; each row is a [com.shodo.android.presentation.searchfriend.SearchFriendUiModel].
 */
@Composable
fun SearchFriendContent(
    users: PersistentList<SearchFriendUiModel>,
    onSubscribeUserPressed: (String) -> Unit,
    onUnsubscribeUserPressed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = dimens.minGridSearchCellSize),
        modifier = modifier.padding(dimens.xSmall),
        contentPadding = PaddingValues(dimens.xSmall),
        horizontalArrangement = Arrangement.spacedBy(dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(dimens.small)
    ) {
        items(items = users, key = { it.id }) { user ->
            UserCard(
                id = user.id,
                name = user.name,
                imageUrl = user.imageUrl,
                subscriptionState = user.subscriptionState,
                onSubscribeUserPressed = onSubscribeUserPressed,
                onUnsubscribeUserPressed = onUnsubscribeUserPressed
            )
        }
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendContent - With Users Found - LightTheme")
@Composable
fun PreviewSearchFriendContent_WithUsersFound_LightTheme() {
    PreviewSearchFriendContent(false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "SearchFriendContent - With Users Found - DarkTheme")
@Composable
fun PreviewSearchFriendContent_WithUsersFound_DarkTheme() {
    PreviewSearchFriendContent(true)
}

@Composable
private fun PreviewSearchFriendContent(darkTheme: Boolean) {
    PokeManiacTheme(darkTheme = darkTheme) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = colors.backgroundApp)) {
            SearchFriendContent(
                users = previewUsers(),
                onSubscribeUserPressed = { },
                onUnsubscribeUserPressed = { }
            )
        }
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