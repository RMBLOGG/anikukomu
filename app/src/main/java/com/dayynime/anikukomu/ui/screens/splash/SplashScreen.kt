package com.dayynime.anikukomu.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dayynime.anikukomu.data.repository.AuthRepository
import com.dayynime.anikukomu.ui.components.LoadingIndicator
import com.dayynime.anikukomu.ui.theme.AnikuBackground
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuTypography
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(1500) // Beautiful splash time
        if (AuthRepository.isSessionActive()) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AnikuBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1000))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "AnikuKomu",
                    style = AnikuTypography.displayLarge.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = AnikuPink
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Wibu Social Haven 🌸",
                    style = AnikuTypography.headlineMedium,
                    color = AnikuPink.copy(alpha = 0.8f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        LoadingIndicator(modifier = Modifier.height(48.dp))
    }
}
