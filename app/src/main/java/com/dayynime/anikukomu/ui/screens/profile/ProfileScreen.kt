package com.dayynime.anikukomu.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
// No extended icon imports needed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dayynime.anikukomu.data.repository.AuthRepository
import com.dayynime.anikukomu.ui.components.GuestLoginDialog
import com.dayynime.anikukomu.ui.components.LoadingIndicator
import com.dayynime.anikukomu.ui.components.UserAvatar
import com.dayynime.anikukomu.ui.theme.AnikuAccent
import com.dayynime.anikukomu.ui.theme.AnikuBackground
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuSurface
import com.dayynime.anikukomu.ui.theme.AnikuSurfaceVar
import com.dayynime.anikukomu.ui.theme.AnikuTextPrimary
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary
import com.dayynime.anikukomu.ui.theme.AnikuTypography
import com.dayynime.anikukomu.ui.theme.frostedGlassBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileUserId: String?,
    onNavigateToEditProfile: (String) -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(profileUserId) {
        viewModel.loadProfile(profileUserId)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .frostedGlassBackground(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isMyOwnProfile) "Profil Saya 🌸" else "@${state.profile?.username ?: "Profile"}",
                        style = AnikuTypography.displayLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                actions = {
                    if (state.isMyOwnProfile) {
                        // Logout Action
                        IconButton(onClick = {
                            scope.launch {
                                AuthRepository.logout()
                                onNavigateToLogin()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Log out icon",
                                tint = AnikuPink
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AnikuSurface)
            )
        },
        containerColor = AnikuBackground
    ) { innerPadding ->
        if (state.isLoading && state.profile == null) {
            LoadingIndicator()
        } else {
            val profile = state.profile
            if (profile == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Profil Tidak Ditemukan",
                        color = AnikuTextSecondary,
                        style = AnikuTypography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Header items span all 3 columns
                    item(span = { GridItemSpan(3) }) {
                        Column {
                            // Cover banner (Gradient sweep)
                            val gradient = Brush.horizontalGradient(
                                colors = listOf(AnikuAccent, AnikuPink)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(gradient)
                            )

                            // Avatar and Action buttons row overlap
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                // Double size avatar
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(top = (-40).dp) // partial overlapping cover
                                ) {
                                    UserAvatar(
                                        avatarUrl = profile.avatarUrl,
                                        size = 80.dp,
                                        borderColor = AnikuBackground,
                                        borderWidth = 3.dp
                                    )
                                }

                                // Right-side dynamic buttons (Follow vs Edit)
                                Row(
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    if (state.isMyOwnProfile) {
                                        Button(
                                            onClick = { onNavigateToEditProfile(profile.id ?: "") },
                                            colors = ButtonDefaults.buttonColors(containerColor = AnikuSurfaceVar),
                                            shape = RoundedCornerShape(30.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                        ) {
                                            Text("Edit Profil", color = Color.White, style = AnikuTypography.labelSmall)
                                        }
                                    } else {
                                        val btnColor = if (state.isFollowing) AnikuSurfaceVar else AnikuAccent
                                        Button(
                                            onClick = { viewModel.toggleFollow() },
                                            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                                            shape = RoundedCornerShape(30.dp),
                                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = if (state.isFollowing) "Mengikuti" else "Ikuti 🌸",
                                                color = Color.White,
                                                style = AnikuTypography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Info details column
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = profile.displayName ?: profile.username ?: "Weeb",
                                    style = AnikuTypography.headlineMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "@${profile.username ?: "anon"}",
                                    style = AnikuTypography.bodySmall,
                                    color = AnikuPink
                                )

                                // Bio Text
                                if (!profile.bio.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = profile.bio,
                                        style = AnikuTypography.bodyMedium,
                                        color = AnikuTextPrimary
                                    )
                                }

                                // Social links row
                                if (!profile.websiteUrl.isNullOrBlank() || !profile.twitterUrl.isNullOrBlank() || !profile.instagramUrl.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (!profile.websiteUrl.isNullOrBlank()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "🌐",
                                                    fontSize = 12.sp
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = profile.websiteUrl,
                                                    style = AnikuTypography.labelSmall,
                                                    color = AnikuAccent,
                                                    maxLines = 1
                                                )
                                            }
                                        }

                                        if (!profile.twitterUrl.isNullOrBlank()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "🐦",
                                                    fontSize = 12.sp
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Twitter",
                                                    style = AnikuTypography.labelSmall,
                                                    color = AnikuPink
                                                )
                                            }
                                        }

                                        if (!profile.instagramUrl.isNullOrBlank()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "📸",
                                                    fontSize = 12.sp
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Instagram",
                                                    style = AnikuTypography.labelSmall,
                                                    color = AnikuPink
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Counters Row: posts, followers, following
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "${profile.postsCount}",
                                            style = AnikuTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Postingan",
                                            style = AnikuTypography.labelSmall,
                                            color = AnikuTextSecondary
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "${profile.followersCount}",
                                            style = AnikuTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Pengikut",
                                            style = AnikuTypography.labelSmall,
                                            color = AnikuTextSecondary
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "${profile.followingCount}",
                                            style = AnikuTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Mengikuti",
                                            style = AnikuTypography.labelSmall,
                                            color = AnikuTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Grid label
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AnikuSurface)
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📸",
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Semua Galeri Anda",
                                    style = AnikuTypography.headlineSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // 3-Column Photogrid items
                    if (state.posts.isEmpty()) {
                        item(span = { GridItemSpan(3) }) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Belum mengirim postingan weeb!",
                                    color = AnikuTextSecondary,
                                    style = AnikuTypography.bodyMedium
                                )
                            }
                        }
                    } else {
                        items(state.posts, key = { it.id ?: "" }) { post ->
                            Card(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable { onNavigateToPostDetail(post.id ?: "") },
                                shape = RoundedCornerShape(0.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(post.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Grid photo item",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Guest dialogues
    GuestLoginDialog(
        show = state.showGuestDialog,
        onDismiss = { viewModel.dismissGuestDialog() },
        onLoginClick = onNavigateToLogin,
        onRegisterClick = onNavigateToRegister
    )
}
