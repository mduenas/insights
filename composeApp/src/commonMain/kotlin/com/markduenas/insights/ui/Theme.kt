package com.markduenas.insights.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary            = BrandBlue,
    onPrimary          = Color.White,
    primaryContainer   = BlueLightContainer,
    onPrimaryContainer = Color(0xFF001257),

    secondary            = BrandPurple,
    onSecondary          = Color.White,
    secondaryContainer   = PurpleLightContainer,
    onSecondaryContainer = Color(0xFF22005D),

    tertiary            = Color(0xFF006685),
    onTertiary          = Color.White,
    tertiaryContainer   = CyanLightContainer,
    onTertiaryContainer = Color(0xFF001F2A),

    background         = LightBackground,
    onBackground       = Color(0xFF1A1C2C),
    surface            = LightSurface,
    onSurface          = Color(0xFF1A1C2C),
    surfaceVariant     = LightSurfaceVariant,
    onSurfaceVariant   = Color(0xFF44475E),
    outline            = Color(0xFF757891),
)

private val DarkColorScheme = darkColorScheme(
    primary            = BrandBlue,
    onPrimary          = Color.White,
    primaryContainer   = BlueDarkContainer,
    onPrimaryContainer = BlueLightContainer,

    secondary            = Color(0xFFCFBDFF),
    onSecondary          = Color(0xFF360083),
    secondaryContainer   = PurpleDarkContainer,
    onSecondaryContainer = PurpleLightContainer,

    tertiary            = BrandCyan,
    onTertiary          = Color(0xFF003547),
    tertiaryContainer   = Color(0xFF004D65),
    onTertiaryContainer = CyanLightContainer,

    background         = BrandDarkBg,
    onBackground       = Color(0xFFE4E1FF),
    surface            = BrandDarkSurface,
    onSurface          = Color(0xFFE4E1FF),
    surfaceVariant     = BrandDarkSurfaceVariant,
    onSurfaceVariant   = Color(0xFFC5C6E0),
    outline            = Color(0xFF8F90A9),
)

@Composable
fun InsightsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
