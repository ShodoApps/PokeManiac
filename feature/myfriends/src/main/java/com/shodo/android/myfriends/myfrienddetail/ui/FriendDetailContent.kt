package com.shodo.android.myfriends.myfrienddetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.coreui.theme.PokeManiacTheme.typography
import com.shodo.android.coreui.ui.GenericEmptyScreen
import com.shodo.android.coreui.ui.SecondaryButton
import com.shodo.android.presentation.myfriends.MyFriendPokemonCardUiModel
import com.shodo.android.presentation.myfriends.MyFriendUiModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FriendDetailContent(
    friend: MyFriendUiModel,
    onUnsubscribePressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalVotes = remember(friend.pokemonCards) { friend.pokemonCards.sumOf { it.totalVotes } }

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.backgroundApp),
        columns = GridCells.Adaptive(minSize = dimens.minGridUserCellSize),
        contentPadding = PaddingValues(bottom = dimens.xSmall)
    ) {
        friendHeader(friend.imageUrl, friend.name, onUnsubscribePressed)
        friendDescriptions(friend.description, totalVotes)
        if (friend.pokemonCards.isEmpty()) {
            friendEmptyState(friend.name)
        } else {
            friendCardsSeparator()
            friendCards(friend.pokemonCards)
        }
    }
}

private fun LazyGridScope.friendHeader(imageUrl: String, name: String, onUnsubscribePressed: () -> Unit) {
    item(key = "header", span = { GridItemSpan(maxLineSpan) }) {
        FriendDetailHeader(imageUrl, name, onUnsubscribePressed)
    }
}

private fun LazyGridScope.friendDescriptions(description: String, totalVotes: Int) {
    item(key = "descriptions", span = { GridItemSpan(maxLineSpan) }) {
        FriendDescriptions(description, totalVotes)
    }
}

private fun LazyGridScope.friendEmptyState(friendName: String) {
    item(key = "empty_state", span = { GridItemSpan(maxLineSpan) }) {
        GenericEmptyScreen(
            text = stringResource(R.string.my_friend_detail_no_activity_yet, friendName)
        )
    }
}

private fun LazyGridScope.friendCardsSeparator() {
    item(key = "separator", span = { GridItemSpan(maxLineSpan) }) {
        Box(
            modifier = Modifier
                .padding(top = dimens.small)
                .fillMaxWidth()
                .height(dimens.separator)
                .background(color = colors.tertiary)
        )
    }
}

private fun LazyGridScope.friendCards(pokemonCards: PersistentList<MyFriendPokemonCardUiModel>) {
    items(items = pokemonCards, key = { it.id }) { card ->
        AsyncImage(
            modifier = Modifier
                .padding(dimens.xxSmall)
                .clip(RectangleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .data(card.imageUrl)
                .crossfade(true)
                .build(),
            contentScale = Crop,
            contentDescription = card.name
        )
    }
}

@Composable
private fun FriendDetailHeader(
    friendImageUrl: String,
    friendName: String,
    onUnsubscribePressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.standard),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(CircleShape)
                .size(dimens.xxxLarge),
            model = ImageRequest.Builder(LocalContext.current)
                .data(friendImageUrl)
                .crossfade(true)
                .build(),
            contentScale = Crop,
            contentDescription = friendName
        )

        SecondaryButton(
            modifier = Modifier
                .wrapContentWidth()
                .padding(vertical = dimens.small),
            text = stringResource(R.string.unsubscribe),
            onClick = onUnsubscribePressed
        )
    }
}

@Composable
private fun FriendDescriptions(friendDescription: String, totalVotes: Int) {
    if (friendDescription.length > 1) { // To avoid the descriptions of 1 character
        Text(
            modifier = Modifier.padding(top = dimens.standard, start = dimens.standard, end = dimens.standard),
            color = colors.primaryText,
            style = typography.t8,
            text = friendDescription
        )
    }
    Text(
        modifier = Modifier.padding(top = dimens.xSmall, start = dimens.standard, end = dimens.standard),
        color = colors.primaryText,
        style = typography.t5,
        text = totalVotes.takeIf { it > 0 }?.let {
            stringResource(R.string.my_friend_detail_total_votes, it)
        } ?: stringResource(R.string.my_friend_detail_total_votes_none)
    )
}

//region Previews

//region FriendDetailContent

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDetailContent - With Posts - LightTheme")
@Composable
private fun PreviewFriendDetailContent_WithPosts_LightTheme() {
    PreviewFriendDetailContent(
        darkTheme = false,
        pokemonCards = previewPokemonCards()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDetailContent - With Posts - DarkTheme")
@Composable
private fun PreviewFriendDetailContent_WithPosts_DarkTheme() {
    PreviewFriendDetailContent(
        darkTheme = true,
        pokemonCards = previewPokemonCards()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDetailContent - No Posts - LightTheme")
@Composable
private fun PreviewFriendDetailContent_NoPosts_LightTheme() {
    PreviewFriendDetailContent(
        darkTheme = false,
        pokemonCards = persistentListOf()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDetailContent - No Posts - DarkTheme")
@Composable
fun PreviewFriendDetailContent_NoPosts_DarkTheme() {
    PreviewFriendDetailContent(
        darkTheme = true,
        pokemonCards = persistentListOf()
    )
}

@Composable
private fun PreviewFriendDetailContent(darkTheme: Boolean, pokemonCards: PersistentList<MyFriendPokemonCardUiModel>) {
    PokeManiacTheme(darkTheme = darkTheme) {
        FriendDetailContent(
            friend = MyFriendUiModel(
                id = "friendId",
                name = "friendName",
                imageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
                description = "description",
                pokemonCards = pokemonCards
            ),
            onUnsubscribePressed = {}
        )
    }
}

//endregion FriendDetailContent

//region FriendDetailHeader

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDetailHeader - LightTheme")
@Composable
fun PreviewFriendDetailHeader_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        FriendDetailHeader(
            friendImageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
            friendName = "friendName",
            onUnsubscribePressed = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDetailHeader - DarkTheme")
@Composable
fun PreviewFriendDetailHeader_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        FriendDetailHeader(
            friendImageUrl = "https://www.superherodb.com/pictures2/portraits/10/100/10831.jpg",
            friendName = "friendName",
            onUnsubscribePressed = {}
        )
    }
}

//endregion FriendDetailHeader

//region FriendDescriptions

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDescriptions - With Description - With Votes - LightTheme")
@Composable
fun PreviewFriendDescriptions_WithDescription_WithVotes_LightTheme() {
    PreviewFriendDescriptions(darkTheme = false, description = "description", totalVotes = 23)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDescriptions - With Description - With Votes - DarkTheme")
@Composable
fun PreviewFriendDescriptions_WithDescription_WithVotes_DarkTheme() {
    PreviewFriendDescriptions(darkTheme = true, description = "description", totalVotes = 23)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDescriptions - No Description - No Votes - LightTheme")
@Composable
fun PreviewFriendDescriptions_NoDescription_NoVotes_LightTheme() {
    PreviewFriendDescriptions(darkTheme = false, description = "-", totalVotes = 0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "FriendDescriptions - No Description - No Votes - DarkTheme")
@Composable
fun PreviewFriendDescriptions_NoDescription_NoVotes_DarkTheme() {
    PreviewFriendDescriptions(darkTheme = true, description = "-", totalVotes = 0)
}

@Composable
private fun PreviewFriendDescriptions(darkTheme: Boolean, description: String, totalVotes: Int) {
    PokeManiacTheme(darkTheme = darkTheme) {
        FriendDescriptions(friendDescription = description, totalVotes = totalVotes)
    }
}

//endregion FriendDescriptions

private fun previewPokemonCards() = persistentListOf(
    MyFriendPokemonCardUiModel(
        id = "Ivysaur" + "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png",
        pokemonId = 2,
        totalVotes = 19,
        hasMyVote = false,
        name = "Ivysaur",
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png"
    ),
    MyFriendPokemonCardUiModel(
        id = "Squirtle" + "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
        pokemonId = 7,
        totalVotes = 4,
        hasMyVote = false,
        name = "Squirtle",
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png"
    )
)
//endregion Previews
