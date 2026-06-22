package com.dayynime.anikukomu.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AnikuTypography = Typography(
    displayLarge  = TextStyle(
        fontFamily = FontFamily.Serif, // Cinzel style fallback
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        color = AnikuTextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = AnikuTextPrimary
    ),
    bodyLarge     = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp,
        color = AnikuTextPrimary
    ),
    bodySmall     = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        color = AnikuTextSecondary
    ),
    labelSmall    = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        color = AnikuTextSecondary
    )
)
