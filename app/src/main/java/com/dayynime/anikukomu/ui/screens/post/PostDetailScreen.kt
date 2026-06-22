package com.dayynime.anikukomu.ui.screens.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
// No SubdirectoryArrowRight import needed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dayynime.anikukomu.data.model.Comment
import com.dayynime.anikukomu.ui.components.GuestLoginDialog
import com.dayynime.anikukomu.ui.components.LoadingIndicator
import com.dayynime.anikukomu.ui.components.PostCard
import com.dayynime.anikukomu.ui.components.UserAvatar
import com.dayynime.anikukomu.ui.theme.AnikuAccent
import com.dayynime.anikukomu.ui.theme.AnikuBackground
import com.dayynime.anikukomu.ui.theme.AnikuBorder
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuSurface
import com.dayynime.anikukomu.ui.theme.AnikuSurfaceVar
import com.dayynime.anikukomu.ui.theme.AnikuTextPrimary
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary
import com.dayynime.anikukomu.ui.theme.AnikuTypography
import com.dayynime.anikukomu.ui.theme.frostedGlassBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    onNavigateToExploreWithSearch: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(postId) {
        viewModel.loadPostDetails(postId)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .frostedGlassBackground()
            .imePadding(), // automatically adjusts content for keyboard heights
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Diskusi Momen 🌸",
                        style = AnikuTypography.displayLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AnikuSurface)
            )
        },
        containerColor = AnikuBackground,
        bottomBar = {
            // Discussion comment typing field at the bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AnikuSurface)
                    .navigationBarsPadding()
                    .padding(8.dp)
            ) {
                // Reply header indicator
                AnimatedVisibility(
                    visible = state.replyingToComment != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AnikuSurfaceVar, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "↳",
                                style = AnikuTypography.labelSmall.copy(fontSize = 18.sp),
                                color = AnikuPink
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Membalas @${state.replyingToComment?.profiles?.username ?: "anon"}",
                                style = AnikuTypography.labelSmall,
                                color = AnikuTextPrimary
                            )
                        }
                        IconButton(
                            onClick = { viewModel.setReplyingTo(null) },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel reply",
                                tint = AnikuTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Comment input box row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = state.commentInputText,
                        onValueChange = { viewModel.onCommentInputChanged(it) },
                        placeholder = {
                            Text(
                                text = if (state.replyingToComment != null) "Tulis balasan..." else "Tulis komentar diskusimu...",
                                style = AnikuTypography.bodyMedium,
                                color = AnikuTextSecondary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AnikuTextPrimary,
                            unfocusedTextColor = AnikuTextPrimary,
                            focusedBorderColor = AnikuPink,
                            unfocusedBorderColor = AnikuBorder,
                            cursorColor = AnikuPink,
                            focusedContainerColor = AnikuSurfaceVar,
                            unfocusedContainerColor = AnikuSurfaceVar
                        ),
                        singleLine = false,
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    IconButton(
                        onClick = {
                            viewModel.submitComment()
                            keyboardController?.hide()
                        },
                        enabled = state.commentInputText.isNotBlank() && !state.isActionLoading,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (state.commentInputText.isNotBlank()) AnikuAccent else AnikuSurfaceVar,
                                CircleShape
                            )
                    ) {
                        if (state.isActionLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Comment",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.isLoading && state.post == null) {
            LoadingIndicator()
        } else {
            val post = state.post
            if (post == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Postingan diskusi tidak ditemukan.",
                        color = AnikuTextSecondary,
                        style = AnikuTypography.bodyLarge
                    )
                }
            } else {
                // Compile comments into hierarchies
                val rootComments = remember(state.comments) {
                    state.comments.filter { it.parentId == null }.sortedByDescending { it.createdAt }
                }
                val repliesMap = remember(state.comments) {
                    state.comments.filter { it.parentId != null }.groupBy { it.parentId ?: "" }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // standalone parent post details card
                    item {
                        PostCard(
                            post = post,
                            isLiked = state.isLiked,
                            onLikeClick = { viewModel.toggleLikePost() },
                            onCommentClick = { /* already here */ },
                            onUserClick = { onNavigateToUserProfile(post.userId ?: "") },
                            onAnimeTagClick = onNavigateToExploreWithSearch,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    // Separation label
                    item {
                        Text(
                            text = "Komentar Diskusis (${state.comments.size})",
                            style = AnikuTypography.headlineSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (rootComments.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Belum ada diskusi weeb! 💬\nTulis komentar pertamamu di bawah",
                                    style = AnikuTypography.bodyMedium,
                                    color = AnikuTextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // Render nested comment trees recursively
                        rootComments.forEach { rootComment ->
                            item(key = rootComment.id ?: "") {
                                CommentCard(
                                    comment = rootComment,
                                    isLiked = state.likedCommentIds.contains(rootComment.id),
                                    onLikeClick = { viewModel.toggleLikeComment(rootComment) },
                                    onReplyClick = { viewModel.setReplyingTo(rootComment) },
                                    onUserClick = { onNavigateToUserProfile(rootComment.userId ?: "") }
                                )
                            }

                            // Sub replies (indented nested)
                            val subReplies = repliesMap[rootComment.id ?: ""]
                            if (!subReplies.isNullOrEmpty()) {
                                items(subReplies, key = { it.id ?: "" }) { reply ->
                                    CommentCard(
                                        comment = reply,
                                        isLiked = state.likedCommentIds.contains(reply.id),
                                        onLikeClick = { viewModel.toggleLikeComment(reply) },
                                        onReplyClick = { viewModel.setReplyingTo(rootComment) }, // replies to parent directly
                                        onUserClick = { onNavigateToUserProfile(reply.userId ?: "") },
                                        modifier = Modifier.padding(start = 36.dp) // Indented
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Guest Dialogues
    GuestLoginDialog(
        show = state.showGuestDialog,
        onDismiss = { viewModel.dismissGuestDialog() },
        onLoginClick = onNavigateToLogin,
        onRegisterClick = onNavigateToRegister
    )
}

@Composable
fun CommentCard(
    comment: Comment,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            UserAvatar(
                avatarUrl = comment.profiles?.avatarUrl,
                size = 32.dp,
                onClick = onUserClick
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.profiles?.displayName ?: comment.profiles?.username ?: "Weeb",
                        style = AnikuTypography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.clickable { onUserClick() }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "@${comment.profiles?.username ?: "anon"}",
                        style = AnikuTypography.labelSmall,
                        color = AnikuPink
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = comment.content ?: "",
                    style = AnikuTypography.bodyMedium,
                    color = AnikuTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Reply prompt trigger
                Text(
                    text = "Balas 🌸",
                    style = AnikuTypography.labelSmall,
                    color = AnikuTextSecondary,
                    modifier = Modifier
                        .clickable { onReplyClick() }
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                )
            }
        }

        // Comment like button column details
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            IconButton(
                onClick = onLikeClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like Comment",
                    tint = if (isLiked) AnikuPink else AnikuTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "${comment.likesCount}",
                style = AnikuTypography.labelSmall,
                color = AnikuTextSecondary
            )
        }
    }
}
