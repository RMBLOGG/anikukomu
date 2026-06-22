package com.dayynime.anikukomu.ui.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
// withFrameNanos is imported from runtime
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dayynime.anikukomu.data.model.Post
import com.dayynime.anikukomu.data.model.Story
import com.dayynime.anikukomu.data.repository.AuthRepository
import com.dayynime.anikukomu.ui.components.GuestLoginDialog
import com.dayynime.anikukomu.ui.components.LoadingIndicator
import com.dayynime.anikukomu.ui.components.PostCard
import com.dayynime.anikukomu.ui.components.StoryRing
import com.dayynime.anikukomu.ui.components.UserAvatar
import com.dayynime.anikukomu.ui.theme.AnikuBackground
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuSurface
import com.dayynime.anikukomu.ui.theme.AnikuTextPrimary
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary
import com.dayynime.anikukomu.ui.theme.AnikuTypography
import com.dayynime.anikukomu.ui.theme.frostedGlassBackground
import kotlinx.coroutines.delay
import kotlin.random.Random

// 2D Sakura Petal Model
data class SakuraPetal(
    var x: Float,
    var y: Float,
    val speedY: Float,
    val speedX: Float,
    val size: Float,
    val rotation: Float,
    val rotationSpeed: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    onNavigateToExploreWithSearch: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val currentUserId = remember { AuthRepository.getCurrentUserId() }

    // Media Picker for Story selection
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.uploadStory(context, uri)
        }
    }

    // Active full story viewer dialog state
    var selectedStoryUserGroup by remember { mutableStateOf<List<Story>?>(null) }

    // Falling Sakura Particle Physics
    val petals = remember { mutableStateListOf<SakuraPetal>() }
    val timeStep by produceState(0L) {
        while (true) {
            withFrameNanos { value -> value }
            // Move petals
            petals.forEachIndexed { index, petal ->
                petal.y += petal.speedY
                petal.x += petal.speedX
                if (petal.y > 1500f) {
                    petals[index] = petal.copy(
                        y = -20f,
                        x = Random.nextFloat() * 1000f,
                        speedY = Random.nextFloat() * 2f + 1f,
                        speedX = Random.nextFloat() * 1.5f - 0.7f
                    )
                }
            }
        }
    }

    // Initialize petals once
    LaunchedEffect(Unit) {
        if (petals.isEmpty()) {
            repeat(20) {
                petals.add(
                    SakuraPetal(
                        x = Random.nextFloat() * 1000f,
                        y = Random.nextFloat() * 1500f,
                        speedY = Random.nextFloat() * 2f + 1f,
                        speedX = Random.nextFloat() * 1.5f - 0.7f,
                        size = Random.nextFloat() * 10f + 6f,
                        rotation = Random.nextFloat() * 360f,
                        rotationSpeed = Random.nextFloat() * 2f - 1f
                    )
                )
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .frostedGlassBackground(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AnikuKomu 🌸",
                        style = AnikuTypography.displayLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold),
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AnikuSurface)
            )
        },
        containerColor = AnikuBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sakura particle layer
            Canvas(modifier = Modifier.fillMaxSize()) {
                petals.forEach { petal ->
                    drawContext.canvas.save()
                    drawContext.canvas.translate(petal.x, petal.y)
                    drawContext.canvas.rotate(petal.rotation)
                    
                    // Simple path to draw beautiful sakura leaf petal shape
                    val path = Path().apply {
                        moveTo(0f, -petal.size / 2)
                        cubicTo(petal.size / 2, -petal.size / 2, petal.size, petal.size / 2, 0f, petal.size)
                        cubicTo(-petal.size, petal.size / 2, -petal.size / 2, -petal.size / 2, 0f, -petal.size / 2)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = AnikuPink.copy(alpha = 0.45f)
                    )
                    drawContext.canvas.restore()
                }
            }

            // Main feed scroll lists
            if (state.isLoading && state.posts.isEmpty()) {
                LoadingIndicator()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Stories section
                    item {
                        StoriesHeaderSection(
                            stories = state.stories,
                            currentUserId = currentUserId,
                            isUploading = state.isUploadingStory,
                            onAddStoryClick = {
                                if (currentUserId == null) {
                                    viewModel.toggleLike(Post()) // trigger guest dialog
                                } else {
                                    galleryLauncher.launch("image/*")
                                }
                            },
                            onStoryClick = { userId ->
                                val userStories = state.stories.filter { it.userId == userId }
                                if (userStories.isNotEmpty()) {
                                    selectedStoryUserGroup = userStories
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Feed posts
                    if (state.posts.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 64.dp, horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Belum ada postingan weeb! 🌸\nJadilah yang pertama berkontribusi!",
                                    style = AnikuTypography.bodyLarge,
                                    color = AnikuTextSecondary,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(state.posts, key = { it.id ?: "" }) { post ->
                            PostCard(
                                post = post,
                                isLiked = state.likedPostIds.contains(post.id),
                                onLikeClick = { viewModel.toggleLike(post) },
                                onCommentClick = { onNavigateToPostDetail(post.id ?: "") },
                                onUserClick = { onNavigateToUserProfile(post.userId ?: "") },
                                onAnimeTagClick = { onNavigateToExploreWithSearch(it) },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Floating Loading Overlay for story creation
            AnimatedVisibility(
                visible = state.isUploadingStory,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AnikuPink)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mengupload story barumu... 🌸",
                            color = Color.White,
                            style = AnikuTypography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Fullscreen storytelling dialogue overlay
    selectedStoryUserGroup?.let { storyGroup ->
        StoryViewerDialog(
            stories = storyGroup,
            onDismiss = { selectedStoryUserGroup = null }
        )
    }

    // Restricted action bottom Sheet trigger for guest accounts
    GuestLoginDialog(
        show = state.showGuestDialog,
        onDismiss = { viewModel.dismissGuestDialog() },
        onLoginClick = onNavigateToLogin,
        onRegisterClick = onNavigateToRegister
    )
}

@Suppress("UPPER_BOUND_VIOLATED_WARNING")
@Composable
fun StoriesHeaderSection(
    stories: List<Story>,
    currentUserId: String?,
    isUploading: Boolean,
    onAddStoryClick: () -> Unit,
    onStoryClick: (String) -> Unit
) {
    // Group active stories by their creator user ID
    val grouped = remember(stories) { stories.groupBy { it.userId ?: "" } }
    
    // Check if own user has stories
    val hasOwnStory = currentUserId != null && grouped.containsKey(currentUserId)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AnikuSurface)
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Momen Seru Wibu 🌸",
            style = AnikuTypography.headlineMedium.copy(fontSize = 15.sp),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Your own story bubble trigger
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        if (hasOwnStory) onStoryClick(currentUserId!!) else onAddStoryClick()
                    }
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        StoryRing(
                            hasActiveStory = hasOwnStory,
                            onClick = {
                                if (hasOwnStory) onStoryClick(currentUserId!!) else onAddStoryClick()
                            }
                        ) {
                            val ownProfile = if (hasOwnStory) stories.firstOrNull { it.userId == currentUserId }?.profiles else null
                            UserAvatar(
                                avatarUrl = ownProfile?.avatarUrl,
                                size = 52.dp,
                                borderWidth = 0.dp
                            )
                        }

                        // Pink addition sign tag overlay
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(AnikuPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Story",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Story Baru",
                        style = AnikuTypography.labelSmall,
                        color = AnikuTextSecondary
                    )
                }
            }

            // Active stories bubbles of other creators
            val otherStoryGroups = grouped.filter { it.key != currentUserId }
            otherStoryGroups.forEach { (userId, storiesListOfUser) ->
                val profile = storiesListOfUser.firstOrNull()?.profiles
                item(key = userId) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onStoryClick(userId) }
                    ) {
                        StoryRing(
                            hasActiveStory = true,
                            onClick = { onStoryClick(userId) }
                        ) {
                            UserAvatar(
                                avatarUrl = profile?.avatarUrl,
                                size = 52.dp,
                                borderWidth = 0.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = profile?.username ?: "Wibu",
                            style = AnikuTypography.labelSmall,
                            color = AnikuTextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// Fullscreen StoryViewer with countdown loading indicators
@Composable
fun StoryViewerDialog(
    stories: List<Story>,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    val currentStory = stories.getOrNull(currentIndex)
    var storyProgress by remember(currentIndex) { mutableStateOf(0f) }

    LaunchedEffect(currentIndex) {
        storyProgress = 0f
        val ticks = 100
        for (i in 1..ticks) {
            delay(40) // 4 seconds total active duration (40ms * 100 = 4000)
            storyProgress = i / ticks.toFloat()
        }
        if (currentIndex < stories.lastIndex) {
            currentIndex++
        } else {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Scaffold(
            containerColor = Color.Black,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (currentStory != null) {
                    // Fullscreen content image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentStory.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Fullscreen Story",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                if (currentIndex < stories.lastIndex) {
                                    currentIndex++
                                } else {
                                    onDismiss()
                                }
                            }
                    )

                    // Header row: linear progression, creator avatar, and exit option
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.40f))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        // Progress bar row indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            stories.forEachIndexed { idx, _ ->
                                when {
                                    idx < currentIndex -> LinearProgressIndicator(
                                        progress = { 1f },
                                        color = AnikuPink,
                                        trackColor = Color.White.copy(alpha = 0.3f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                    idx == currentIndex -> LinearProgressIndicator(
                                        progress = { storyProgress },
                                        color = AnikuPink,
                                        trackColor = Color.White.copy(alpha = 0.3f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                    else -> LinearProgressIndicator(
                                        progress = { 0f },
                                        color = AnikuPink,
                                        trackColor = Color.White.copy(alpha = 0.3f),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Creator metadata row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            UserAvatar(
                                avatarUrl = currentStory.profiles?.avatarUrl,
                                size = 32.dp,
                                borderWidth = 0.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = currentStory.profiles?.username ?: "Wibu",
                                color = Color.White,
                                style = AnikuTypography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close story",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
