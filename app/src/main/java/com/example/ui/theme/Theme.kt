package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Uber High-Contrast Monochrome Dark Scheme
private val DarkColorScheme = darkColorScheme(
    primary = UberPureWhite,
    onPrimary = UberPureBlack,
    primaryContainer = UberDarkSurfaceVariant,
    onPrimaryContainer = UberPureWhite,
    secondary = UberGray400,
    onSecondary = UberPureBlack,
    secondaryContainer = UberDarkSurfaceElevated,
    onSecondaryContainer = UberPureWhite,
    tertiary = UberGreen,
    onTertiary = UberPureWhite,
    background = UberPureBlack,
    onBackground = UberPureWhite,
    surface = UberDarkSurface,
    onSurface = UberPureWhite,
    surfaceVariant = UberDarkSurfaceVariant,
    onSurfaceVariant = UberGray400,
    outline = UberDarkBorder,
    outlineVariant = UberDarkSurfaceElevated,
    error = UberRed,
    onError = UberPureWhite
)

// Uber High-Contrast Clean Light Scheme
private val LightColorScheme = lightColorScheme(
    primary = UberPureBlack,
    onPrimary = UberPureWhite,
    primaryContainer = UberLightSurfaceVariant,
    onPrimaryContainer = UberPureBlack,
    secondary = UberGray600,
    onSecondary = UberPureWhite,
    secondaryContainer = UberLightSurfaceElevated,
    onSecondaryContainer = UberPureBlack,
    tertiary = UberGreen,
    onTertiary = UberPureWhite,
    background = UberLightBackground,
    onBackground = UberPureBlack,
    surface = UberLightSurface,
    onSurface = UberPureBlack,
    surfaceVariant = UberLightSurfaceVariant,
    onSurfaceVariant = UberGray600,
    outline = UberLightBorder,
    outlineVariant = UberLightSurfaceElevated,
    error = UberRed,
    onError = UberPureWhite
)

@Composable
fun ArtonTheme(
    darkTheme: Boolean = true, // Default dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
