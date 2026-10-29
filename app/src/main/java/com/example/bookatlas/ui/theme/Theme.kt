package com.example.bookatlas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF216153),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2EBDD),
    onPrimaryContainer = Color(0xFF123D33),
    secondary = Color(0xFF9A643D),
    background = Color(0xFFF8F8F4),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE4ECE6),
    onSurface = Color(0xFF1C2D29),
    onSurfaceVariant = Color(0xFF55665E)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9DD7BF),
    primaryContainer = Color(0xFF245648),
    secondary = Color(0xFFF0BB91),
    background = Color(0xFF101D1A),
    surface = Color(0xFF192923),
    surfaceVariant = Color(0xFF2A4037)
)

@Composable
fun BookAtlasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
