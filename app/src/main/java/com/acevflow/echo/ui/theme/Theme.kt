package com.acevflow.echo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MidnightPrimary,
    secondary = MidnightSecondary,
    tertiary = MidnightTertiary,
    background = MidnightBackground,
    surface = MidnightSurface,
    surfaceVariant = MidnightSurfaceVariant,
    onPrimary = MidnightBackground,
    onSecondary = MidnightBackground,
    onTertiary = MidnightBackground,
    onBackground = SnowBackground,
    onSurface = SnowBackground,
    onSurfaceVariant = SnowSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = SnowPrimary,
    secondary = SnowSecondary,
    tertiary = SnowTertiary,
    background = SnowBackground,
    surface = SnowSurface,
    surfaceVariant = SnowSurfaceVariant,
    onPrimary = SnowBackground,
    onSecondary = SnowBackground,
    onTertiary = SnowBackground,
    onBackground = MidnightBackground,
    onSurface = MidnightBackground,
    onSurfaceVariant = MidnightSurfaceVariant
)

@Composable
fun EchoTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
