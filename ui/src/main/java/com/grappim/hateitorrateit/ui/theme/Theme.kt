@file:Suppress("MagicNumber")

package com.grappim.hateitorrateit.ui.theme

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
    primary = Color(0xFFE8B98E),
    primaryVariant = Color(0xFFCA9E7A),
    secondary = Color(0xFFA48775),
    background = Color(0xFFF2E9DF),
    surface = Color(0xFFCEC7BF),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

val DarkColorPalette = darkColors(
    primary = Color(0xFFE8B98E),
    primaryVariant = Color(0xFFCA9E7A),
    secondary = Color(0xFFA48775),
    background = Color(0xFF333333),
    surface = Color(0xFF1C1C1C),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
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
