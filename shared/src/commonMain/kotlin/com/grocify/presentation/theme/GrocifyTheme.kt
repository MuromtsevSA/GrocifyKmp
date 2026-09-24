package com.grocify.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PrimaryGreen = Color(0xFF1F9D55)
private val PrimaryGreenDark = Color(0xFF3DDC84)
private val BackgroundLight = Color(0xFFF8FAF9)
private val BackgroundDark = Color(0xFF0F1412)
private val CardLight = Color(0xFFFFFFFF)
private val CardDark = Color(0xFF1A211E)

private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    background = BackgroundLight,
    onBackground = Color(0xFF102018),
    surface = CardLight,
    onSurface = Color(0xFF102018),
    surfaceVariant = Color(0xFFE8F3EC),
    onSurfaceVariant = Color(0xFF5C6B63),
    outline = Color(0xFFD5E3DB),
)

private val DarkColors = darkColorScheme(
    primary = PrimaryGreenDark,
    onPrimary = Color(0xFF0F1412),
    background = BackgroundDark,
    onBackground = Color(0xFFE8F0EB),
    surface = CardDark,
    onSurface = Color(0xFFE8F0EB),
    surfaceVariant = Color(0xFF243029),
    onSurfaceVariant = Color(0xFFA3B5AB),
    outline = Color(0xFF314037),
)

@Composable
fun GrocifyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
