package com.dayynime.anikukomu.ui.screens.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreatePostScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatePostViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImageSelected(uri)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateToHome()
            viewModel.resetState()
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
                        text = "Bagikan Postingan 🌸",
                        style = AnikuTypography.displayLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error overlay
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    style = AnikuTypography.bodyMedium,
                    color = AnikuPink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AnikuPink.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Image picker container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AnikuSurfaceVar)
                    .border(1.dp, AnikuBorder, RoundedCornerShape(16.dp))
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (state.imageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(state.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Content ImageDraft",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Change photo overlay icon
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Text(
                            text = "Ubah Foto 🖼️",
                            color = Color.White,
                            style = AnikuTypography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Picker placeholder icon",
                            tint = AnikuTextSecondary,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Pilih Gambar Kerenmu 🌸",
                            style = AnikuTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = AnikuTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Caption Text field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Caption Momen",
                    style = AnikuTypography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = state.caption,
                    onValueChange = { viewModel.onCaptionChanged(it) },
                    placeholder = {
                        Text(
                            "Tulis keseruan anime ini... (max 500 huruf weeb!)",
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Text(
                    text = "${state.caption.length}/500",
                    style = AnikuTypography.labelSmall,
                    color = AnikuTextSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, end = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Anime tag block
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tag Anime Favorit (Maks 5)",
                    style = AnikuTypography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Currently chosen tag chips (removable)
                if (state.selectedAnimes.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.selectedAnimes.forEach { anime ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(AnikuPink)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = anime.title,
                                    style = AnikuTypography.labelSmall,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove tag",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { viewModel.removeAnimeTag(anime) }
                                )
                            }
                        }
                    }
                }

                // Autocomplete text input search bar
                OutlinedTextField(
                    value = state.animeSearchQuery,
                    onValueChange = { viewModel.onAnimeSearchQueryChanged(it) },
                    placeholder = {
                        Text(
                            "Ketik nama anime untuk men-tag...",
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Autocomplete dropdown suggestions list
                if (state.isSearchingAnime) {
                    CircularProgressIndicator(
                        color = AnikuPink,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                    )
                } else {
                    if (state.searchResults.isNotEmpty() || state.showAddCustomAnimeOption) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .background(AnikuSurfaceVar, RoundedCornerShape(12.dp))
                                .border(1.dp, AnikuBorder, RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        ) {
                            state.searchResults.forEach { anime ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.addAnimeTag(anime) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add tag",
                                        tint = AnikuPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = anime.title,
                                        style = AnikuTypography.bodyMedium,
                                        color = AnikuTextPrimary
                                    )
                                }
                            }

                            // Dynamic on-the-fly custom anime creation option display
                            if (state.showAddCustomAnimeOption) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.createCustomAnimeAndTag(state.animeSearchQuery) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add new",
                                        tint = AnikuAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Buat \"${state.animeSearchQuery}\" sebagai anime baru 🌸",
                                        style = AnikuTypography.bodyMedium,
                                        color = AnikuPink,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Action submit post button
            Button(
                onClick = { viewModel.createPost(context) },
                colors = ButtonDefaults.buttonColors(containerColor = AnikuAccent),
                shape = RoundedCornerShape(50.dp),
                enabled = !state.isUploading && state.imageUri != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (state.isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.height(20.dp))
                } else {
                    Text(
                        text = "Bagikan Postingan 🚀",
                        style = AnikuTypography.labelLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
