package com.shodo.android.myfriends.myfriendlist.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFriendListTopBar(
    onSearchFriendsPressed: () -> Unit,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = colors.backgroundApp
        ),
        title = {
            Text(
                text = stringResource(R.string.my_friends_title),
                style = typography.t1,
                textAlign = Center,
                color = colors.primaryText
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.primaryText
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchFriendsPressed) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    tint = colors.primaryText,
                    contentDescription = "Search Friends"
                )
            }
        }
    )
}

//region previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListTopBar - LightTheme")
@Composable
fun PreviewMyFriendListTopBar_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyFriendListTopBar(
            onSearchFriendsPressed = {},
            onBackPressed = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendListTopBar - DarkTheme")
@Composable
fun PreviewMyFriendListTopBar_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyFriendListTopBar(
            onSearchFriendsPressed = {},
            onBackPressed = {}
        )
    }
}

//endregion previews
