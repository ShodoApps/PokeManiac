package com.shodo.android.posttransaction.step2.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.ui.GenericLoader
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.posttransaction.PostTransactionStepTopBar
import com.shodo.android.presentation.posttransaction.PostTransactionStep2UiState
import com.shodo.android.presentation.posttransaction.PostTransactionStep2UiState.Filling
import com.shodo.android.presentation.posttransaction.PostTransactionStep2UiState.Loading

/**
 * Main Composable displaying the application's PostTransactionStep2 screen.
 * It uses a `Scaffold` to display a top bar (`PostTransactionStepTopBar`),
 * main content, and a `SnackbarHost` for displaying temporary messages.
 *
 * @param modifier              Modifier to customize the root layout.
 * @param imageUri              The local uri of the taken image of the new transaction's post.
 * @param uiState               UI state representing the current screen state. Possible states:
 *                              - `Loading`: Displays a loading indicator.
 *                              - `Filling`: Currently filling the Post Transaction data.
 * @param onBackPressed         Callback to navigate back.
 * @param onSaveActivity        Callback triggered when the new transaction is saved and posted.
 * @param snackbarHostState     State of the `SnackbarHost` to display temporary messages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTransactionStep2View(
    modifier: Modifier = Modifier,
    imageUri: Uri,
    uiState: PostTransactionStep2UiState,
    onBackPressed: () -> Unit,
    onSaveActivity: (pokemonName: String, pokemonNumber: Int, transactionType: NewActivityType, transactionPrice: Int, uri: Uri) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.backgroundApp),
        topBar = { PostTransactionStepTopBar(onBackPressed) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundApp)
                .padding(innerPadding)
        ) {
            when (uiState) {
                Loading -> GenericLoader()
                Filling -> PostTransactionStep2Content(modifier, imageUri, onSaveActivity)
            }
        }
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "PostTransactionStep2View - Loading - LightTheme")
@Composable
fun PreviewPostTransactionStep2View_Loading_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        PostTransactionStep2View(
            imageUri = "".toUri(),
            uiState = Loading,
            onBackPressed = {},
            onSaveActivity = { _, _, _, _, _ -> },
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "PostTransactionStep2View - No Photo - DarkTheme")
@Composable
fun PreviewPostTransactionStep2View_Loading_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        PostTransactionStep2View(
            imageUri = "".toUri(),
            uiState = Loading,
            onBackPressed = {},
            onSaveActivity = { _, _, _, _, _ -> },
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "PostTransactionStep2View - Filling - LightTheme")
@Composable
fun PreviewMyFriendsListContent_Filling_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        PostTransactionStep2View(
            imageUri = "".toUri(),
            uiState = Filling,
            onBackPressed = {},
            onSaveActivity = { _, _, _, _, _ -> },
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "PostTransactionStep2View - No Photo - DarkTheme")
@Composable
fun PreviewMyFriendsListContent_Filling_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        PostTransactionStep2View(
            imageUri = "".toUri(),
            uiState = Filling,
            onBackPressed = {},
            onSaveActivity = { _, _, _, _, _ -> },
            snackbarHostState = SnackbarHostState()
        )
    }
}

//endregion Previews
