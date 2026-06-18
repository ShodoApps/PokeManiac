package com.shodo.android.coreui.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.shodo.android.coreui.theme.PokeManiacTheme

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(PokeManiacTheme.dimens.small),
        modifier = modifier,
        contentPadding = PaddingValues(
            start = PokeManiacTheme.dimens.large,
            end = PokeManiacTheme.dimens.large,
            top = PokeManiacTheme.dimens.small,
            bottom = PokeManiacTheme.dimens.small
        ),
        colors = buttonColors(
            containerColor = PokeManiacTheme.colors.accentDark,
            contentColor = PokeManiacTheme.colors.primaryButton
        )
    ) {
        Text(
            text = text,
            style = PokeManiacTheme.typography.t3,
            textAlign = TextAlign.Center
        )
    }
}
