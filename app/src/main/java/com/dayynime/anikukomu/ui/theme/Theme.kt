package com.dayynime.anikukomu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AnikuAccent,
    secondary = AnikuAccentLight,
    tertiary = AnikuPink,
    background = AnikuBackground,
    surface = AnikuSurface,
    surfaceVariant = AnikuSurfaceVar,
    onPrimary = AnikuTextPrimary,
    onSecondary = AnikuTextPrimary,
    onBackground = AnikuTextPrimary,
    onSurface = AnikuTextPrimary,
    onSurfaceVariant = AnikuTextSecondary,
    outline = AnikuBorder,
    error = AnikuError
)

@Composable
fun AnikuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AnikuTypography,
        content = content
    )
}

// Custom Glassmorphism organic background glowing blobs modifier
fun Modifier.frostedGlassBackground(): Modifier = this.drawBehind {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(AnikuPink.copy(alpha = 0.18f), Color.Transparent),
            center = Offset(size.width * 0.8f, size.height * 0.2f),
            radius = size.width * 0.7f
        ),
        center = Offset(size.width * 0.8f, size.height * 0.2f),
        radius = size.width * 0.7f
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(AnikuAccent.copy(alpha = 0.18f), Color.Transparent),
            center = Offset(size.width * 0.2f, size.height * 0.8f),
            radius = size.width * 0.8f
        ),
        center = Offset(size.width * 0.2f, size.height * 0.8f),
        radius = size.width * 0.8f
    )
}
