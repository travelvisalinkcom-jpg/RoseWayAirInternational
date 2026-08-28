package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HiruPrimary,
    onPrimary = Color.White,
    primaryContainer = HiruCardBgElevated,
    onPrimaryContainer = HiruTextLight,
    secondary = HiruSuccess,
    onSecondary = Color.Black,
    secondaryContainer = HiruSuccessContainer,
    onSecondaryContainer = HiruSuccessLight,
    tertiary = HiruAccent,
    onTertiary = Color.White,
    background = HiruDarkBg,
    onBackground = HiruTextLight,
    surface = HiruCardBg,
    onSurface = HiruTextLight,
    surfaceVariant = HiruCardBgElevated,
    onSurfaceVariant = HiruTextMuted,
    outline = HiruBorder
)

private val LightColorScheme = lightColorScheme(
    primary = HiruPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = HiruPrimaryLight,
    onPrimaryContainer = Color.Black,
    secondary = HiruSuccess,
    onSecondary = Color.White,
    secondaryContainer = HiruSuccessContainer,
    onSecondaryContainer = Color.White,
    tertiary = HiruAccent,
    onTertiary = Color.White,
    background = HiruLightBg,
    onBackground = HiruLightTextPrimary,
    surface = HiruLightCard,
    onSurface = HiruLightTextPrimary,
    surfaceVariant = HiruLightBorder,
    onSurfaceVariant = HiruLightTextSecondary,
    outline = HiruLightBorder
)

@Composable
fun HiruTheme(
    darkTheme: Boolean = true, // Default to stunning modern dark OS theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun RoseWayTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    HiruTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

