package com.dayynime.anikukomu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dayynime.anikukomu.ui.theme.AnikuAccent
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuTypography

@Composable
fun AnimeTagChip(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(AnikuAccent, AnikuPink)
    )

    var chipModifier = modifier
        .clip(RoundedCornerShape(50.dp))
        .background(gradient)

    if (onClick != null) {
        chipModifier = chipModifier.clickable { onClick() }
    }

    Box(
        modifier = chipModifier.padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = AnikuTypography.labelSmall,
            color = Color.White
        )
    }
}
