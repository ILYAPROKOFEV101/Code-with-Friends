package com.ilya.codewithfriends.Activity.CreatyActivity.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = com.ilya.codewithfriends.findroom.ui.theme.CustomColor1,  // Replace md_theme_light_primary with your custom color
    onPrimary = com.ilya.codewithfriends.findroom.ui.theme.light_onCustomColor1,
    primaryContainer = com.ilya.codewithfriends.findroom.ui.theme.light_CustomColor1Container,
    onPrimaryContainer = com.ilya.codewithfriends.findroom.ui.theme.light_onCustomColor1Container,
    secondary = com.ilya.codewithfriends.findroom.ui.theme.CustomColor2,
    onSecondary = com.ilya.codewithfriends.findroom.ui.theme.light_onCustomColor2,
    secondaryContainer = com.ilya.codewithfriends.findroom.ui.theme.light_CustomColor2Container,
    onSecondaryContainer = com.ilya.codewithfriends.findroom.ui.theme.light_onCustomColor2Container,



    tertiary = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_tertiary,
    onTertiary = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_onTertiary,
    tertiaryContainer = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_tertiaryContainer,
    onTertiaryContainer = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_onTertiaryContainer,
    error = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_error,
    errorContainer = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_errorContainer,
    onError = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_onError,
    onErrorContainer = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_onErrorContainer,
    background = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_background,
    onBackground = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_onBackground,
    surface = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_surface,
    onSurface = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_onSurface,
    surfaceVariant = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_surfaceVariant,
    onSurfaceVariant = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_onSurfaceVariant,
    outline = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_outline,
    inverseOnSurface = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_inverseOnSurface,
    inverseSurface = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_inverseSurface,
    inversePrimary = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_inversePrimary,
    surfaceTint = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_surfaceTint,
    outlineVariant = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_outlineVariant,
    scrim = com.ilya.codewithfriends.findroom.ui.theme.md_theme_light_scrim,

    )


private val DarkColors = darkColorScheme(
    primary = com.ilya.codewithfriends.findroom.ui.theme.dark_CustomColor1,  // Replace md_theme_dark_primary with your custom color
    onPrimary = com.ilya.codewithfriends.findroom.ui.theme.dark_onCustomColor1,
    primaryContainer = com.ilya.codewithfriends.findroom.ui.theme.dark_CustomColor1Container,
    onPrimaryContainer = com.ilya.codewithfriends.findroom.ui.theme.dark_onCustomColor1Container,
    secondary = com.ilya.codewithfriends.findroom.ui.theme.dark_CustomColor2,
    onSecondary = com.ilya.codewithfriends.findroom.ui.theme.dark_onCustomColor2,
    secondaryContainer = com.ilya.codewithfriends.findroom.ui.theme.dark_CustomColor2Container,
    onSecondaryContainer = com.ilya.codewithfriends.findroom.ui.theme.dark_onCustomColor2Container,

    tertiary = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_tertiary,
    onTertiary = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_onTertiary,
    tertiaryContainer = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_onTertiaryContainer,
    error = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_error,
    errorContainer = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_errorContainer,
    onError = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_onError,
    onErrorContainer = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_onErrorContainer,
    background = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_background,
    onBackground = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_onBackground,
    surface = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_surface,
    onSurface = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_onSurface,
    surfaceVariant = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_surfaceVariant,
    onSurfaceVariant = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_onSurfaceVariant,
    outline = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_outline,
    inverseOnSurface = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_inverseOnSurface,
    inverseSurface = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_inverseSurface,
    inversePrimary = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_inversePrimary,
    surfaceTint = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_surfaceTint,
    outlineVariant = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_outlineVariant,
    scrim = com.ilya.codewithfriends.findroom.ui.theme.md_theme_dark_scrim,


    )
@Composable
fun CodeWithFriendsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
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
        colorScheme = colorScheme,
        typography = com.ilya.codewithfriends.findroom.ui.theme.Typography,
        content = content
    )
}