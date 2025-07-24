@file:Suppress("MagicNumber")

package com.grappim.hateitorrateit.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

@Suppress("TopLevelPropertyNaming")
const val DarkBackgroundColorForPreview = 3355443L

@Composable
fun HateItOrRateItTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }

    MaterialTheme(
        colors = colorScheme,
        typography = Typography,
        content = content
    )
}
