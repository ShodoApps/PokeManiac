package com.shodo.android.dashboard.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
fun DashboardTopBar(
    onSearchFriendsPressed: () -> Unit,
    onFriendsPressed: () -> Unit,
    onProfilePressed: () -> Unit,
    onPostTransactionPressed: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.backgroundApp
        ),
        title = {
            Text(
                text = stringResource(R.string.dashboard_title),
                style = typography.t1,
                textAlign = Center,
                color = colors.primaryText
            )
        },
        actions = {
            IconButton(onClick = onSearchFriendsPressed) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    tint = colors.primaryText,
                    contentDescription = "Search Friends"
                )
            }
            IconButton(onClick = onFriendsPressed) {
                Icon(
                    imageVector = Icons.Outlined.PeopleAlt,
                    tint = colors.primaryText,
                    contentDescription = "Friends"
                )
            }
            IconButton(onClick = onProfilePressed) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    tint = colors.primaryText,
                    contentDescription = "Profile"
                )
            }
            IconButton(onClick = onPostTransactionPressed) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    tint = colors.primaryText,
                    contentDescription = "Post Transaction"
                )
            }
        }
    )
}

//region previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardTopBar - LightTheme")
@Composable
fun PreviewDashboardTopBar_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        DashboardTopBar(
            onSearchFriendsPressed = {},
            onFriendsPressed = {},
            onProfilePressed = {},
            onPostTransactionPressed = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardTopBar - DarkTheme")
@Composable
fun PreviewDashboardTopBar_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        DashboardTopBar(
            onSearchFriendsPressed = {},
            onFriendsPressed = {},
            onProfilePressed = {},
            onPostTransactionPressed = {}
        )
    }
}

//endregion previews
