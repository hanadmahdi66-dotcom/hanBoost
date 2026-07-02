package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyberPrimary,
    secondary = CyberSecondary,
    tertiary = CyberTertiary,
    background = CyberBackground,
    surface = CyberSurface,
    surfaceVariant = CyberSurfaceVariant,
    onPrimary = CyberBackground,
    onSecondary = CyberOnSurface,
    onTertiary = CyberBackground,
    onBackground = CyberOnBackground,
    onSurface = CyberOnSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for the immersive gaming console experience
    dynamicColor: Boolean = false, // Disable dynamic colors to keep our customized cyber neon palette consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
