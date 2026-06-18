package com.shodo.android.coreui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class PokeManiacColors(
    primaryText: Color,
    primaryButton: Color,
    secondaryText: Color,
    thirdText: Color,
    fourthText: Color,
    tertiary: Color,
    backgroundApp: Color,
    backgroundCell: Color,
    accent: Color,
    accentDark: Color
) {
    var primaryText by mutableStateOf(primaryText)
        private set
    var primaryButton by mutableStateOf(primaryButton)
        private set
    var secondaryText by mutableStateOf(secondaryText)
        private set
    var thirdText by mutableStateOf(thirdText)
        private set
    var fourthText by mutableStateOf(fourthText)
        private set
    var tertiary by mutableStateOf(tertiary)
        private set
    var backgroundApp by mutableStateOf(backgroundApp)
        private set
    var backgroundCell by mutableStateOf(backgroundCell)
        private set
    var accent by mutableStateOf(accent)
        private set
    var accentDark by mutableStateOf(accentDark)
        private set

    fun copy(
        primaryText: Color = this.primaryText,
        primaryButton: Color = this.primaryButton,
        secondaryText: Color = this.secondaryText,
        thirdText: Color = this.thirdText,
        fourthText: Color = this.fourthText,
        tertiary: Color = this.tertiary,
        backgroundApp: Color = this.backgroundApp,
        backgroundCell: Color = this.backgroundCell,
        accent: Color = this.accent,
        accentDark: Color = this.accentDark
    ) = PokeManiacColors(
        primaryText = primaryText,
        primaryButton = primaryButton,
        secondaryText = secondaryText,
        thirdText = thirdText,
        fourthText = fourthText,
        tertiary = tertiary,
        backgroundApp = backgroundApp,
        backgroundCell = backgroundCell,
        accent = accent,
        accentDark = accentDark
    )

    fun updateColorsFrom(other: PokeManiacColors) {
        primaryText = other.primaryText
        primaryButton = other.primaryButton
        secondaryText = other.secondaryText
        thirdText = other.thirdText
        fourthText = other.fourthText
        tertiary = other.tertiary
        backgroundApp = other.backgroundApp
        backgroundCell = other.backgroundCell
        accent = other.accent
        accentDark = other.accentDark
    }
}

fun lightColors() = PokeManiacColors(
    primaryText = black,
    primaryButton = white,
    secondaryText = grey3,
    thirdText = brokenBlack,
    fourthText = brokenWhitePlaceHolder,
    tertiary = grey2,
    backgroundApp = lightBackgroundApp,
    backgroundCell = lightBackgroundCell,
    accent = red,
    accentDark = redDark
)

fun darkColors() = PokeManiacColors(
    primaryText = white,
    primaryButton = white,
    secondaryText = black,
    thirdText = brokenWhite,
    fourthText = brokenWhitePlaceHolder,
    tertiary = grey,
    backgroundApp = darkBackgroundApp,
    backgroundCell = darkBackgroundCell,
    accent = red,
    accentDark = redDark
)

val black = Color(0xFF000000)
val white = Color(0xFFFFFFFF)
val brokenWhite = Color(0xFFB0BEC5)
val brokenBlack = Color(0xFF8E8E8E)
val brokenWhitePlaceHolder = Color(0xFF90A4AE)
val grey = Color(0xFF6B6E70)
val grey2 = Color(0xFF9E9E9E)
val grey3 = Color(0xFF4A4A4A)
val darkBackgroundApp = Color(0xFF101214)
val darkBackgroundCell = Color(0xFF222529)
val lightBackgroundApp = Color(0xFFEAEAEA)
val lightBackgroundCell = Color(0xFFE0E0E0)
val red = Color(0xFFBF0000)
val redDark = Color(0xFF800000)
