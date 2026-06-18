package com.shodo.android.myprofile.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileTopBar(
    onBackPressed: () -> Unit,
    onBillingPressed: () -> Unit,
    onPostTransaction: () -> Unit
) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = colors.backgroundApp
        ),
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.primaryText
                )
            }
        },
        actions = {
            IconButton(onClick = onPostTransaction) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    tint = colors.primaryText,
                    contentDescription = "Search Friends"
                )
            }
            IconButton(onClick = onBillingPressed) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = "Back",
                    tint = colors.primaryText
                )
            }
        }
    )
}

//region previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyProfileTopBar - LightTheme")
@Composable
fun PreviewMyFriendListTopBar_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        MyProfileTopBar(
            onPostTransaction = {},
            onBillingPressed = {},
            onBackPressed = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "MyProfileTopBar - DarkTheme")
@Composable
fun PreviewMyFriendListTopBar_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        MyProfileTopBar(
            onPostTransaction = {},
            onBillingPressed = {},
            onBackPressed = {}
        )
    }
}

//endregion previews
