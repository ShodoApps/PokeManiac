package com.shodo.android.coreui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors

@Composable
fun GenericLoader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundApp),
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.align(CenterHorizontally), color = colors.primaryText)
        Text(
            modifier = Modifier.align(CenterHorizontally).padding(top = PokeManiacTheme.dimens.small),
            text = stringResource(R.string.loading),
            style = PokeManiacTheme.typography.t3,
            color = colors.primaryText
        )
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "GenericLoaderScreen - LightTheme")
@Composable
fun PreviewGenericLoaderScreen_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp),
            contentAlignment = Center
        ) {
            GenericLoader()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "GenericLoaderScreen - DarkTheme")
@Composable
fun PreviewGenericLoaderScreen_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp),
            contentAlignment = Center
        ) {
            GenericLoader()
        }
    }
}

//endregion Previews
