package com.dayynime.anikukomu.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dayynime.anikukomu.ui.components.GuestLoginDialog
import com.dayynime.anikukomu.ui.components.LoadingIndicator
import com.dayynime.anikukomu.ui.components.PostCard
import com.dayynime.anikukomu.ui.theme.AnikuBackground
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuSurface
import com.dayynime.anikukomu.ui.theme.AnikuSurfaceVar
import com.dayynime.anikukomu.ui.theme.AnikuTextPrimary
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary
import com.dayynime.anikukomu.ui.theme.AnikuTypography
import com.dayynime.anikukomu.ui.theme.frostedGlassBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    initialSearchQuery: String?,
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Pre-populate search query if passed from anime tags
    LaunchedEffect(initialSearchQuery) {
        if (!initialSearchQuery.isNullOrBlank()) {
            viewModel.onSearchQueryChanged(initialSearchQuery)
        }
    }

    val genres = listOf("Action", "Shounen", "Seinen", "Romance", "Slice of Life", "Fantasy", "Comedy")

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .frostedGlassBackground(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jelajah Komunitas 🌸",
                        style = AnikuTypography.displayLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AnikuSurface)
            )
        },
        containerColor = AnikuBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Block
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = AnikuTextSecondary
                    )
                },
                placeholder = {
                    Text(
                        text = "Cari caption, user, atau anime...",
                        color = AnikuTextSecondary,
                        style = AnikuTypography.bodyMedium
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = AnikuTextPrimary,
                    unfocusedTextColor = AnikuTextPrimary,
                    focusedBorderColor = AnikuPink,
                    unfocusedBorderColor = AnikuSurfaceVar,
                    cursorColor = AnikuPink,
                    focusedContainerColor = AnikuSurfaceVar,
                    unfocusedContainerColor = AnikuSurfaceVar
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Genre Selector Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All genre button
                item {
                    val isSelected = state.activeGenre == null
                    GenreChip(
                        name = "Semua",
                        selected = isSelected,
                        onClick = { viewModel.onGenreSelected(null) }
                    )
                }

                items(genres) { genre ->
                    val isSelected = state.activeGenre == genre
                    GenreChip(
                        name = genre,
                        selected = isSelected,
                        onClick = { viewModel.onGenreSelected(genre) }
                    )
                }
            }

            // Results lists
            if (state.isLoading && state.posts.isEmpty()) {
                LoadingIndicator()
            } else {
                if (state.posts.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tidak ada hasil pencarian weeb! 🌸",
                            style = AnikuTypography.bodyLarge,
                            color = AnikuTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Coba kata kunci lain atau cari genre berbeda.",
                            style = AnikuTypography.bodyMedium,
                            color = AnikuTextSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(state.posts, key = { it.id ?: "" }) { post ->
                            PostCard(
                                post = post,
                                isLiked = state.likedPostIds.contains(post.id),
                                onLikeClick = { viewModel.toggleLike(post) },
                                onCommentClick = { onNavigateToPostDetail(post.id ?: "") },
                                onUserClick = { onNavigateToUserProfile(post.userId ?: "") },
                                onAnimeTagClick = { viewModel.onSearchQueryChanged(it) },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Guest restricted dialogue popup
    GuestLoginDialog(
        show = state.showGuestDialog,
        onDismiss = { viewModel.dismissGuestDialog() },
        onLoginClick = onNavigateToLogin,
        onRegisterClick = onNavigateToRegister
    )
}

@Composable
fun GenreChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerBg = if (selected) AnikuPink else AnikuSurfaceVar
    val textCol = if (selected) Color.White else AnikuTextSecondary

    Column(
        modifier = Modifier
            .background(containerBg, RoundedCornerShape(50.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            style = AnikuTypography.labelSmall,
            color = textCol,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
