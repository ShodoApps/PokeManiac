package com.shodo.android.posttransaction

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun PostTransactionStepTopBar(onBackPressed: () -> Unit) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = colors.backgroundApp
        ),
        title = {
            Text(
                text = stringResource(R.string.new_post_title),
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
        }
    )
}

//region previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "PostTransactionStepTopBar - LightTheme")
@Composable
fun PreviewPostTransactionStepTopBar_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        PostTransactionStepTopBar(onBackPressed = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "PostTransactionStep1TopBar - DarkTheme")
@Composable
fun PreviewPostTransactionStepTopBar_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        PostTransactionStepTopBar(onBackPressed = {})
    }
}

//endregion previews
