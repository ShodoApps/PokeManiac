package com.shodo.android.coreui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class PokeManiacSpaces(
    val separator: Dp = 1.dp,
    val xxSmall: Dp = 2.dp,
    val xSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val standard: Dp = 16.dp,
    val large: Dp = 24.dp,
    val xLarge: Dp = 40.dp,
    val xxLarge: Dp = 60.dp,
    val xxxLarge: Dp = 80.dp,
    val xxxxLarge: Dp = 120.dp,
    val minGridSearchCellSize: Dp = 150.dp,
    val minGridUserCellSize: Dp = 120.dp
)

val localSpaces = staticCompositionLocalOf { PokeManiacSpaces() }
