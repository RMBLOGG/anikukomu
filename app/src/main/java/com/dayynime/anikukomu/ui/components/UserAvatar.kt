package com.dayynime.anikukomu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dayynime.anikukomu.ui.theme.AnikuBorder
import com.dayynime.anikukomu.ui.theme.AnikuSurfaceVar
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary

@Composable
fun UserAvatar(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    borderColor: Color = AnikuBorder,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null
) {
    var customModifier = modifier
        .size(size)
        .clip(CircleShape)
        .background(AnikuSurfaceVar)
    
    if (borderWidth > 0.dp) {
        customModifier = customModifier.border(borderWidth, borderColor, CircleShape)
    }

    if (onClick != null) {
        customModifier = customModifier.clickable { onClick() }
    }

    Box(
        modifier = customModifier,
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar Placeholder",
                tint = AnikuTextSecondary,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}

// Utility extension
private fun Modifier.fillMaxSize(): Modifier = this.then(Modifier.size(Dp.Infinity))
