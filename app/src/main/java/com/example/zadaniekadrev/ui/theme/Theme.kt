package com.example.zadaniekadrev.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = AccentDark,
    background = BgDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onBackground = Color(0xFFF3F4F6),
    onSurface = Color(0xFFF3F4F6),
    tertiary = SuccessGreen
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = AccentLight,
    background = BgLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onBackground = Color(0xFF1F2937),
    onSurface = Color(0xFF1F2937),
    tertiary = SuccessGreen
)

@Composable
fun ZadanieKadrevTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
