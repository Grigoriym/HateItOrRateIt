package com.grappim.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LightColorPalette = lightColors(
    primary = Color(0xFFE8B98E), // Primary color
    primaryVariant = Color(0xFFCA9E7A), // Variant of the primary color
    secondary = Color(0xFFA48775), // Secondary color
    background = Color(0xFFF2E9DF), // Background color
    surface = Color(0xFFCEC7BF), // Surface color
    onPrimary = Color.Black, // Color for text on primary background
    onSecondary = Color.White, // Color for text on secondary background
    onBackground = Color.Black, // Color for text on background
    onSurface = Color.Black // Color for text on surface
)

val DarkColorPalette = darkColors(
    primary = Color(0xFFE8B98E), // Primary color
    primaryVariant = Color(0xFFCA9E7A), // Variant of the primary color
    secondary = Color(0xFFA48775), // Secondary color
    background = Color(0xFF333333), // Background color
    surface = Color(0xFF1C1C1C), // Surface color
    onPrimary = Color.White, // Color for text on primary background
    onSecondary = Color.White, // Color for text on secondary background
    onBackground = Color.White, // Color for text on background
    onSurface = Color.White // Color for text on surface
)

@Composable
fun HateItOrRateItTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colors = colorScheme,
        typography = Typography,
        content = content
    )
}