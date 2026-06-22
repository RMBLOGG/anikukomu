package com.dayynime.anikukomu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.dayynime.anikukomu.ui.navigation.AppNavigation
import com.dayynime.anikukomu.ui.theme.AnikuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnikuTheme {
                AppNavigation()
            }
        }
    }
}
