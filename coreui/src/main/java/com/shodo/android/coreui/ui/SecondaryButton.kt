package com.shodo.android.coreui.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.shodo.android.coreui.theme.PokeManiacTheme

@Composable
fun SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(PokeManiacTheme.dimens.small),
        border = BorderStroke(PokeManiacTheme.dimens.separator, PokeManiacTheme.colors.accentDark),
        modifier = modifier,
        contentPadding = PaddingValues(
            start = PokeManiacTheme.dimens.large,
            end = PokeManiacTheme.dimens.large,
            top = PokeManiacTheme.dimens.small,
            bottom = PokeManiacTheme.dimens.small
        )
    ) {
        Text(
            text = text,
            style = PokeManiacTheme.typography.t3,
            textAlign = TextAlign.Center,
            color = PokeManiacTheme.colors.primaryText
        )
    }
}

@Preview
@Composable
fun SecondaryButtonPreview() {
    PokeManiacTheme(darkTheme = false) {
        SecondaryButton(text = "buttonText", onClick = {})
    }
}
