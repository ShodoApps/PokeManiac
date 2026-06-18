package com.shodo.android.coreui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

@Composable
fun PokeManiacTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColors()
    } else {
        lightColors()
    }

    ProvidePokemaniacTheme(
        colors,
        content
    )
}

@Composable
fun ProvidePokemaniacTheme(
    colors: PokeManiacColors,
    content: @Composable () -> Unit
) {
    val colorPalette = remember { colors }
    colorPalette.updateColorsFrom(colors)

    val spaces: PokeManiacSpaces = PokeManiacTheme.dimens
    val typography: PokeManiacTypography = PokeManiacTheme.typography

    CompositionLocalProvider(
        localAmbientPokemaniacColors provides colorPalette,
        localSpaces provides spaces,
        localTypography provides typography,
        content = content
    )
}

private val localAmbientPokemaniacColors = staticCompositionLocalOf<PokeManiacColors> {
    error("No PokemaniacColorPalette provided")
}

object PokeManiacTheme {

    val colors: PokeManiacColors
        @Composable
        @ReadOnlyComposable
        get() = localAmbientPokemaniacColors.current

    val typography: PokeManiacTypography
        @Composable
        @ReadOnlyComposable
        get() = localTypography.current

    val dimens: PokeManiacSpaces
        @Composable
        @ReadOnlyComposable
        get() = localSpaces.current
}
