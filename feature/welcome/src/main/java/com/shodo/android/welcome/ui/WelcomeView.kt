package com.shodo.android.welcome.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.shodo.android.coreui.R
import com.shodo.android.coreui.theme.PokeManiacTheme.colors
import com.shodo.android.coreui.theme.PokeManiacTheme.dimens
import com.shodo.android.coreui.theme.PokeManiacTheme.typography
import com.shodo.android.coreui.ui.PrimaryButton
import com.shodo.android.coreui.ui.SecondaryButton

@Composable
fun WelcomeView(
    modifier: Modifier = Modifier,
    onSignUpClicked: () -> Unit,
    onSignInClicked: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.backgroundApp),
        containerColor = colors.backgroundApp,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = colors.backgroundApp)
        ) {
            Text(
                text = stringResource(R.string.welcome),
                modifier = Modifier.padding(dimens.small),
                color = colors.primaryText,
                style = typography.t1
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .wrapContentSize()
                    .align(CenterHorizontally),
                painter = painterResource(id = R.drawable.pokemaniac),
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.small, vertical = dimens.small),
                text = stringResource(R.string.signup),
                onClick = onSignUpClicked
            )

            SecondaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dimens.small, end = dimens.small, bottom = dimens.xxLarge),
                text = stringResource(R.string.signin),
                onClick = onSignInClicked
            )
        }
    }
}
