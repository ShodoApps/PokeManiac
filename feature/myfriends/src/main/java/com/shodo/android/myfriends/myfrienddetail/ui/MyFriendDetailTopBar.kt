package com.shodo.android.myfriends.myfrienddetail.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFriendDetailTopBar(friendName: String, onBackPressed: () -> Unit) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = colors.backgroundApp
        ),
        title = {
            Text(
                text = friendName,
                style = typography.t1,
                textAlign = Center,
                color = colors.primaryText
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.primaryText
                )
            }
        }
    )
}

//region previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendDetailTopBar - LightTheme")
@Composable
fun PreviewMyFriendDetailTopBar_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyFriendDetailTopBar(
            friendName = "friendname",
            onBackPressed = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyFriendDetailTopBar - DarkTheme")
@Composable
fun PreviewMyFriendDetailTopBar_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyFriendDetailTopBar(
            friendName = "friendname",
            onBackPressed = {}
        )
    }
}

//endregion previews
