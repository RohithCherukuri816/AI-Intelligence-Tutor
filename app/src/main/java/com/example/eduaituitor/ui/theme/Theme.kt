package com.example.eduaituitor.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = SurfaceLight,
    primaryContainer = PurpleGrey40,
    onPrimaryContainer = SurfaceLight,
    secondary = PurpleGrey40,
    onSecondary = SurfaceLight,
    secondaryContainer = PurpleGrey40.copy(alpha = 0.1f),
    onSecondaryContainer = PurpleGrey40,
    tertiary = Pink40,
    onTertiary = SurfaceLight,
    tertiaryContainer = Pink40.copy(alpha = 0.1f),
    onTertiaryContainer = Pink40,
    error = ErrorLight,
    onError = SurfaceLight,
    errorContainer = ErrorLight.copy(alpha = 0.1f),
    onErrorContainer = ErrorLight,
    background = BackgroundLight,
    onBackground = Purple40,
    surface = SurfaceLight,
    onSurface = Purple40,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = PurpleGrey40,
    outline = Purple40.copy(alpha = 0.12f),
    outlineVariant = Purple40.copy(alpha = 0.08f),
    scrim = Purple40.copy(alpha = 0.32f)
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = BackgroundDark,
    primaryContainer = PurpleGrey80,
    onPrimaryContainer = BackgroundDark,
    secondary = PurpleGrey80,
    onSecondary = BackgroundDark,
    secondaryContainer = PurpleGrey80.copy(alpha = 0.1f),
    onSecondaryContainer = PurpleGrey80,
    tertiary = Pink80,
    onTertiary = BackgroundDark,
    tertiaryContainer = Pink80.copy(alpha = 0.1f),
    onTertiaryContainer = Pink80,
    error = ErrorDark,
    onError = BackgroundDark,
    errorContainer = ErrorDark.copy(alpha = 0.1f),
    onErrorContainer = ErrorDark,
    background = BackgroundDark,
    onBackground = Purple80,
    surface = SurfaceDark,
    onSurface = Purple80,
    surfaceVariant = BackgroundDark,
    onSurfaceVariant = PurpleGrey80,
    outline = Purple80.copy(alpha = 0.12f),
    outlineVariant = Purple80.copy(alpha = 0.08f),
    scrim = Purple80.copy(alpha = 0.32f)
)

@Composable
fun EduAITuitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
