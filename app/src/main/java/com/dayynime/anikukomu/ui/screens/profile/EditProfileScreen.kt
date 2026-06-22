package com.dayynime.anikukomu.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dayynime.anikukomu.ui.components.LoadingIndicator
import com.dayynime.anikukomu.ui.components.UserAvatar
import com.dayynime.anikukomu.ui.theme.AnikuBackground
import com.dayynime.anikukomu.ui.theme.AnikuBorder
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuSurface
import com.dayynime.anikukomu.ui.theme.AnikuSurfaceVar
import com.dayynime.anikukomu.ui.theme.AnikuTextPrimary
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary
import com.dayynime.anikukomu.ui.theme.AnikuTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onAvatarLocalSelected(uri)
    }

    LaunchedEffect(Unit) {
        viewModel.loadInitialProfile()
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profil 🌸",
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
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = AnikuPink,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(24.dp)
                        )
                    } else {
                        IconButton(onClick = { viewModel.saveProfile(context) }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save changes",
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
        if (state.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Error display
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

                // Changing avatar box selection
                Spacer(modifier = Modifier.height(16.dp))
                UserAvatar(
                    avatarUrl = state.selectedLocalAvatarUri?.toString() ?: state.avatarUrl,
                    size = 100.dp,
                    borderWidth = 2.dp,
                    borderColor = AnikuPink,
                    onClick = { galleryLauncher.launch("image/*") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ubah Foto Profil 📸",
                    style = AnikuTypography.labelSmall.copy(fontSize = 13.sp),
                    color = AnikuPink,
                    modifier = Modifier
                        .clickable { galleryLauncher.launch("image/*") }
                        .padding(6.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Display Name field
                OutlinedTextField(
                    value = state.displayName,
                    onValueChange = { viewModel.onDisplayNameChanged(it) },
                    label = { Text("Display Name", color = AnikuTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AnikuTextPrimary,
                        unfocusedTextColor = AnikuTextPrimary,
                        focusedBorderColor = AnikuPink,
                        unfocusedBorderColor = AnikuBorder,
                        cursorColor = AnikuPink,
                        focusedContainerColor = AnikuSurfaceVar,
                        unfocusedContainerColor = AnikuSurfaceVar
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bio editor field (with count limitation helper)
                OutlinedTextField(
                    value = state.bio,
                    onValueChange = { viewModel.onBioChanged(it) },
                    label = { Text("Bio (max 150 huruf)", color = AnikuTextSecondary) },
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
                        .height(100.dp)
                )
                Text(
                    text = "${state.bio.length}/150",
                    style = AnikuTypography.labelSmall,
                    color = AnikuTextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, end = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom website link
                OutlinedTextField(
                    value = state.websiteUrl,
                    onValueChange = { viewModel.onWebsiteChanged(it) },
                    label = { Text("Website URL Link", color = AnikuTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AnikuTextPrimary,
                        unfocusedTextColor = AnikuTextPrimary,
                        focusedBorderColor = AnikuPink,
                        unfocusedBorderColor = AnikuBorder,
                        cursorColor = AnikuPink,
                        focusedContainerColor = AnikuSurfaceVar,
                        unfocusedContainerColor = AnikuSurfaceVar
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Twitter handle
                OutlinedTextField(
                    value = state.twitterUrl,
                    onValueChange = { viewModel.onTwitterChanged(it) },
                    label = { Text("Twitter username", color = AnikuTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AnikuTextPrimary,
                        unfocusedTextColor = AnikuTextPrimary,
                        focusedBorderColor = AnikuPink,
                        unfocusedBorderColor = AnikuBorder,
                        cursorColor = AnikuPink,
                        focusedContainerColor = AnikuSurfaceVar,
                        unfocusedContainerColor = AnikuSurfaceVar
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Instagram handle
                OutlinedTextField(
                    value = state.instagramUrl,
                    onValueChange = { viewModel.onInstagramChanged(it) },
                    label = { Text("Instagram username", color = AnikuTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AnikuTextPrimary,
                        unfocusedTextColor = AnikuTextPrimary,
                        focusedBorderColor = AnikuPink,
                        unfocusedBorderColor = AnikuBorder,
                        cursorColor = AnikuPink,
                        focusedContainerColor = AnikuSurfaceVar,
                        unfocusedContainerColor = AnikuSurfaceVar
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
