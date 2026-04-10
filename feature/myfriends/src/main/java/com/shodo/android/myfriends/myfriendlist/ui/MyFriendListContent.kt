package com.shodo.android.myfriends.myfriendlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.myfriends.uimodel.MyFriendUI
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MyFriendsListContent(
    friends: PersistentList<MyFriendUI>,
    onFriendClicked: (id: String) -> Unit,
    onUnsubscribeFriend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundApp),
        verticalArrangement = Arrangement.spacedBy(dimens.xSmall)
    ) {
        items(
            items = friends,
            key = { it.id }
        ) { friend ->
            val onFriendPressed = remember(friend.id, onFriendClicked) { { onFriendClicked(friend.id) } }
            val onUnsubscribePressed = remember(friend.id, onUnsubscribeFriend) { { onUnsubscribeFriend(friend.id) } }
            MyFriendCard(
                id = friend.id,
                name = friend.name,
                imageUrl = friend.imageUrl,
                onFriendPressed = onFriendPressed,
                onUnsubscribePressed = onUnsubscribePressed
            )
        }
    }
}


//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendsListContent - LightTheme")
@Composable
fun PreviewMyFriendsListContent_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyFriendsListContent(
            friends = previewFriends(),
            onFriendClicked = { },
            onUnsubscribeFriend = { }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendsListContent - DarkTheme")
@Composable
fun PreviewMyFriendsListContent_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyFriendsListContent(
            friends = previewFriends(),
            onFriendClicked = { },
            onUnsubscribeFriend = { }
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
