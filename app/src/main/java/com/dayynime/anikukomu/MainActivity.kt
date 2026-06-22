package com.dayynime.anikukomu

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.dayynime.anikukomu.ui.navigation.AppNavigation
import com.dayynime.anikukomu.ui.theme.AnikuTheme
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Global crash handler — tulis log ke file
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            val logFile = File(getExternalFilesDir(null), "crash_log.txt")
            logFile.writeText(sw.toString())
        }

        enableEdgeToEdge()
        setContent {
            AnikuTheme {
                AppNavigation()
            }
        }
    }
}
