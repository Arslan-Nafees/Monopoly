package com.monopoly.game.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light mode colors
private val LightColorPalette = lightColors(
    primary = Color(0xFF006E51),
    primaryVariant = Color(0xFF004D38),
    secondary = Color(0xFFFF5722),
    secondaryVariant = Color(0xFFC41C00),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFB00020),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    onError = Color(0xFFFFFFFF)
)

// Dark mode colors
private val DarkColorPalette = darkColors(
    primary = Color(0xFF4C9F86),
    primaryVariant = Color(0xFF006E51),
    secondary = Color(0xFFFF8A50),
    secondaryVariant = Color(0xFFFF5722),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFCF6679),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onError = Color(0xFF000000)
)

@Composable
fun MonopolyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
} 