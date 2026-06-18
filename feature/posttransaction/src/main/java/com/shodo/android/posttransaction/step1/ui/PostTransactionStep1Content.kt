package com.shodo.android.posttransaction.step1.ui

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.checkSelfPermission
import coil3.compose.rememberAsyncImagePainter
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.coreui.theme.PokeManiacTheme.typography
import com.shodo.android.coreui.ui.PrimaryButton
import com.shodo.android.coreui.ui.SecondaryButton
import java.io.File

@Composable
fun PostTransactionStep1Content(
    innerPadding: PaddingValues,
    onNextStep: (Uri) -> Unit,
    createImageFile: () -> File?,
    getUriForImageFile: (File?) -> Uri?,
    onCameraPermissionDenied: () -> Unit
) {
    var capturedImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    Column(
        modifier = Modifier
            .background(color = colors.backgroundApp)
            .padding(innerPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.new_post_step_1_title),
            modifier = Modifier
                .padding(dimens.standard)
                .align(Start),
            color = colors.primaryText,
            style = typography.t4
        )
        if (capturedImageUri.path?.isNotEmpty() == true) {
            CapturedImageView(
                capturedImageUri = capturedImageUri,
                createImageFile = createImageFile,
                getUriForImageFile = getUriForImageFile,
                onNextStep = onNextStep,
                onSetUri = { uri -> capturedImageUri = uri },
                onCameraPermissionDenied = onCameraPermissionDenied
            )
        } else {
            NoCapturedImageView(
                createImageFile = createImageFile,
                getUriForImageFile = getUriForImageFile,
                onSetUri = { uri -> capturedImageUri = uri },
                onCameraPermissionDenied = onCameraPermissionDenied
            )
        }
    }
}

@Composable
private fun ColumnScope.CapturedImageView(
    capturedImageUri: Uri,
    createImageFile: () -> File?,
    getUriForImageFile: (File?) -> Uri?,
    onSetUri: (Uri) -> Unit,
    onNextStep: (Uri) -> Unit,
    onCameraPermissionDenied: () -> Unit
) {
    Image(
        modifier = Modifier
            .padding(top = dimens.standard)
            .fillMaxWidth()
            .aspectRatio(1f),
        painter = rememberAsyncImagePainter(capturedImageUri),
        contentDescription = null
    )
    Spacer(modifier = Modifier.weight(1f))
    SnapPhotoButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.small),
        text = stringResource(R.string.new_post_step_1_retake_photo),
        onSetUri = onSetUri,
        createImageFile = createImageFile,
        getUriForImageFile = getUriForImageFile,
        onCameraPermissionDenied = onCameraPermissionDenied
    )
    PrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dimens.small, end = dimens.small, bottom = dimens.xxLarge),
        text = stringResource(R.string.next),
        onClick = { onNextStep(capturedImageUri) }
    )
}

@Composable
private fun ColumnScope.NoCapturedImageView(
    onSetUri: (Uri) -> Unit,
    createImageFile: () -> File?,
    getUriForImageFile: (File?) -> Uri?,
    onCameraPermissionDenied: () -> Unit
) {
    Spacer(modifier = Modifier.weight(1f))
    Image(
        modifier = Modifier
            .wrapContentSize()
            .align(CenterHorizontally),
        painter = painterResource(id = R.drawable.pokemaniac),
        contentDescription = null
    )
    Spacer(modifier = Modifier.weight(1f))
    SnapPhotoButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dimens.small, end = dimens.small, bottom = dimens.xxLarge),
        text = stringResource(R.string.new_post_step_1_take_photo),
        onSetUri = onSetUri,
        createImageFile = createImageFile,
        getUriForImageFile = getUriForImageFile,
        onCameraPermissionDenied = onCameraPermissionDenied
    )
}

@Composable
private fun ColumnScope.SnapPhotoButton(
    modifier: Modifier = Modifier,
    text: String,
    onSetUri: (Uri) -> Unit,
    createImageFile: () -> File?,
    getUriForImageFile: (File?) -> Uri?,
    onCameraPermissionDenied: () -> Unit
) {
    val context = LocalContext.current

    val imageFile = remember { createImageFile() }
    val uri = getUriForImageFile(imageFile)

    val launcher = rememberLauncherForActivityResult(contract = TakePicture()) { success ->
        if (success) { uri?.let(onSetUri) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            uri?.let(launcher::launch)
        } else {
            onCameraPermissionDenied()
        }
    }

    SecondaryButton(
        modifier = modifier,
        text = text
    ) {
        if (checkSelfPermission(context, CAMERA) == PERMISSION_GRANTED) {
            uri?.let(launcher::launch)
        } else {
            permissionLauncher.launch(CAMERA)
        }
    }
}
