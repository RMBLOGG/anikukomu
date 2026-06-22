package com.dayynime.anikukomu.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dayynime.anikukomu.data.model.Post
import com.dayynime.anikukomu.ui.theme.AnikuAccent
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuSurfaceVar
import com.dayynime.anikukomu.ui.theme.AnikuTextPrimary
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary
import com.dayynime.anikukomu.ui.theme.AnikuTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PostCard(
    post: Post,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onUserClick: () -> Unit,
    onAnimeTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var likeTriggered by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val heartScale by animateFloatAsState(
        targetValue = if (likeTriggered) 1.4f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "heartScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x2BFFFFFF), RoundedCornerShape(16.dp))
            .clickable { onCommentClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnikuSurfaceVar),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    // Subtle violet left border (4dp)
                    drawLine(
                        color = AnikuAccent,
                        start = Offset(2.dp.toPx(), 0f),
                        end = Offset(2.dp.toPx(), size.height),
                        strokeWidth = 4.dp.toPx()
                    )
                }
                .padding(start = 16.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
        ) {
            // Author Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserClick() }
            ) {
                UserAvatar(
                    avatarUrl = post.profiles?.avatarUrl,
                    size = 40.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = post.profiles?.displayName ?: post.profiles?.username ?: "Weeb",
                        style = AnikuTypography.headlineMedium.copy(fontSize = 14.sp),
                        color = AnikuTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${post.profiles?.username ?: "anon"}",
                        style = AnikuTypography.bodySmall,
                        color = AnikuTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Caption
            if (!post.caption.isNullOrBlank()) {
                Text(
                    text = post.caption,
                    style = AnikuTypography.bodyLarge,
                    color = AnikuTextPrimary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Post Image (Full Width, Aspect Ratio 1:1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Post Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Anime Tags row
            val tags = post.postAnimeTags
            if (!tags.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tags.forEach { tagJoin ->
                        val anime = tagJoin.animes
                        if (anime != null) {
                            AnimeTagChip(
                                title = anime.title,
                                onClick = { onAnimeTagClick(anime.title) },
                                modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Interactions row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Like Button
                IconButton(
                    onClick = {
                        scope.launch {
                            likeTriggered = true
                            onLikeClick()
                            delay(250)
                            likeTriggered = false
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer(
                            scaleX = heartScale,
                            scaleY = heartScale
                        )
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like Post",
                        tint = if (isLiked) AnikuPink else Color.White
                    )
                }
                Text(
                    text = "${post.likesCount}",
                    style = AnikuTypography.bodySmall,
                    color = AnikuTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(20.dp))

                // Comment Button
                IconButton(
                    onClick = onCommentClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = CommentIcon,
                        contentDescription = "Comment Post",
                        tint = Color.White
                    )
                }
                Text(
                    text = "${post.commentsCount}",
                    style = AnikuTypography.bodySmall,
                    color = AnikuTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

val CommentIcon: ImageVector
    get() = ImageVector.Builder(
        name = "CommentIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.White),
            stroke = null,
            strokeLineWidth = 1.0f
        ) {
            moveTo(21.99f, 4.0f)
            curveTo(21.99f, 2.9f, 21.1f, 2.0f, 20.0f, 2.0f)
            horizontalLineTo(4.0f)
            curveTo(2.9f, 2.0f, 2.0f, 2.9f, 2.0f, 4.0f)
            verticalLineTo(16.0f)
            curveTo(2.0f, 17.1f, 2.9f, 18.0f, 4.0f, 18.0f)
            horizontalLineTo(18.0f)
            lineTo(22.0f, 22.0f)
            lineTo(21.99f, 4.0f)
            close()
            moveTo(18.0f, 14.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(12.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(14.0f)
            close()
            moveTo(18.0f, 11.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(11.0f)
            close()
            moveTo(18.0f, 8.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(6.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(8.0f)
            close()
        }
    }.build()
