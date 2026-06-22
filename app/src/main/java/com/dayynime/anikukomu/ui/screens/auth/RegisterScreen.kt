package com.dayynime.anikukomu.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
// No Visibility imports needed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dayynime.anikukomu.ui.theme.AnikuBackground
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuSurfaceVar
import com.dayynime.anikukomu.ui.theme.AnikuTextPrimary
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary
import com.dayynime.anikukomu.ui.theme.AnikuTypography

@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateToHome()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AnikuBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gabung Komunitas! 🌸",
            style = AnikuTypography.displayLarge.copy(fontSize = 30.sp, fontWeight = FontWeight.Bold),
            color = AnikuPink,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Bagikan keseruan anime favoritmu dan berteman dengan sesama wibu!",
            style = AnikuTypography.bodyLarge,
            color = AnikuTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Error message box
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

        // Username Field (Cleaned inline)
        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onUsernameChanged(it) },
            label = { Text("Username (unik)", color = AnikuTextSecondary) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AnikuTextPrimary,
                unfocusedTextColor = AnikuTextPrimary,
                focusedBorderColor = AnikuPink,
                unfocusedBorderColor = AnikuSurfaceVar,
                cursorColor = AnikuPink
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = { Text("Email Address", color = AnikuTextSecondary) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AnikuTextPrimary,
                unfocusedTextColor = AnikuTextPrimary,
                focusedBorderColor = AnikuPink,
                unfocusedBorderColor = AnikuSurfaceVar,
                cursorColor = AnikuPink
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = { Text("Password (min 6 karakter)", color = AnikuTextSecondary) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Text(
                    text = if (passwordVisible) "TUTUP" else "LIHAT",
                    color = AnikuPink,
                    style = AnikuTypography.labelSmall,
                    modifier = Modifier
                        .clickable { passwordVisible = !passwordVisible }
                        .padding(12.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AnikuTextPrimary,
                unfocusedTextColor = AnikuTextPrimary,
                focusedBorderColor = AnikuPink,
                unfocusedBorderColor = AnikuSurfaceVar,
                cursorColor = AnikuPink
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.register() }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Register Button
        Button(
            onClick = { viewModel.register() },
            colors = ButtonDefaults.buttonColors(containerColor = AnikuPink),
            shape = RoundedCornerShape(50.dp),
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.height(20.dp))
            } else {
                Text(
                    text = "Daftar Akun",
                    style = AnikuTypography.labelLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign In Direct
        Text(
            text = "Sudah punya akun? Masuk aja weeb",
            style = AnikuTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = AnikuPink,
            modifier = Modifier
                .clickable { onNavigateToLogin() }
                .padding(8.dp)
        )
    }
}
