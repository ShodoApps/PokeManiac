package com.shodo.android.coreui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.unit.sp

data class PokeManiacTypography(
    val t1: TextStyle = TextStyle(fontWeight = Bold, fontSize = 24.sp),
    val t2: TextStyle = TextStyle(fontWeight = Normal, fontSize = 18.sp),
    val t3: TextStyle = TextStyle(fontWeight = Normal, fontSize = 16.sp),
    val t4: TextStyle = TextStyle(fontWeight = Bold, fontSize = 16.sp),
    val t5: TextStyle = TextStyle(fontWeight = Bold, fontSize = 12.sp),
    val t6: TextStyle = TextStyle(fontWeight = Bold, fontSize = 16.sp),
    val t7: TextStyle = TextStyle(fontWeight = Bold, fontSize = 14.sp),
    val t8: TextStyle = TextStyle(fontWeight = Normal, fontSize = 14.sp)
)

val localTypography = staticCompositionLocalOf { PokeManiacTypography() }
