package com.shodo.android.posttransaction.step1.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.posttransaction.PostTransactionStepTopBar
import java.io.File

/**
 * Main Composable displaying the application's PostTransactionStep1 screen.
 * It uses a `Scaffold` to display a top bar (`PostTransactionStepTopBar`),
 * main content, and a `SnackbarHost` for displaying temporary messages.
 *
 * @param modifier                      Modifier to customize the root layout.
 * @param onNextStep                    Callback to navigate to the next PostTransaction's step.
 * @param createImageFile               Callback to create the local image file.
 * @param getUriForImageFile            Callback to get the Uri for the file (context unused; kept for call-site symmetry).
 * @param onBackPressed                 Callback to navigate back.
 * @param onCameraPermissionDenied      Callback triggered when the user denies the Camera Permission.
 * @param snackbarHostState             State of the `SnackbarHost` to display temporary messages.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTransactionStep1View(
    modifier: Modifier = Modifier,
    onNextStep: (Uri) -> Unit,
    createImageFile: () -> File?,
    getUriForImageFile: (File?) -> Uri?,
    onBackPressed: () -> Unit,
    onCameraPermissionDenied: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.backgroundApp),
        topBar = { PostTransactionStepTopBar(onBackPressed) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        PostTransactionStep1Content(
            innerPadding = innerPadding,
            onNextStep = onNextStep,
            createImageFile = createImageFile,
            getUriForImageFile = getUriForImageFile,
            onCameraPermissionDenied = onCameraPermissionDenied
        )
    }
}

//region Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "PostTransactionStep1View - No Photo - LightTheme")
@Composable
fun PreviewPostTransactionStep1View_NoPhoto_LightTheme() {
    PokeManiacTheme(darkTheme = false) {
        PostTransactionStep1View(
            onNextStep = {},
            createImageFile = { null },
            getUriForImageFile = { null },
            onBackPressed = {},
            onCameraPermissionDenied = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "PostTransactionStep1View - No Photo - DarkTheme")
@Composable
fun PreviewPostTransactionStep1View_NoPhoto_DarkTheme() {
    PokeManiacTheme(darkTheme = true) {
        PostTransactionStep1View(
            onNextStep = {},
            createImageFile = { null },
            getUriForImageFile = { null },
            onBackPressed = {},
            onCameraPermissionDenied = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

//endregion Previews
