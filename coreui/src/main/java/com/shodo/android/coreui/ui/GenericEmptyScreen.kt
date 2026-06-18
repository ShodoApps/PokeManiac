package com.shodo.android.coreui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.coreui.theme.PokeManiacTheme.typography

@Composable
fun GenericEmptyScreen(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundApp),
        contentAlignment = Center
    ) {
        Text(
            color = colors.primaryText,
            style = typography.t3,
            text = text,
            modifier = Modifier.padding(dimens.small)
        )
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardEmpty - LightTheme")
@Composable
fun PreviewDashboardEmptyResult_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        GenericEmptyScreen(
            text = "GenericEmptyScreen text",
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp)
                .padding()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DashboardEmpty - DarkTheme")
@Composable
fun PreviewDashboardEmptyResult_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        GenericEmptyScreen(
            text = "GenericEmptyScreen text",
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp)
                .padding()
        )
    }
}

//endregion Previews
