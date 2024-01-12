package com.android.maxclub.firenotes.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xfff1c028),
    onPrimary = Color(0xff3d2e00),
    primaryContainer = Color(0xff594400),
    onPrimaryContainer = Color(0xffffdf91),
    secondary = Color(0xffd6c5a0),
    onSecondary = Color(0xff392f15),
    secondaryContainer = Color(0xff51462a),
    onSecondaryContainer = Color(0xfff2e1bb),
    tertiary = Color(0xffecc248),
    onTertiary = Color(0xff3d2e00),
    tertiaryContainer = Color(0xff584400),
    onTertiaryContainer = Color(0xffffdf90),
    error = Color(0xffffb4ab),
    onError = Color(0xff690005),
    errorContainer = Color(0xff93000a),
    onErrorContainer = Color(0xffffdad6),
    background = Color(0xff1e1b16),
    onBackground = Color(0xffe8e1d9),
    surface = Color(0xff1e1b16),
    onSurface = Color(0xffe8e1d9),
    outline = Color(0xff989080),
    surfaceVariant = Color(0xff4c4639),
    onSurfaceVariant = Color(0xffcfc5b4),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff755b00),
    onPrimary = Color(0xffffffff),
    primaryContainer = Color(0xffffdf91),
    onPrimaryContainer = Color(0xff241a00),
    secondary = Color(0xff6a5d3f),
    onSecondary = Color(0xffffffff),
    secondaryContainer = Color(0xfff2e1bb),
    onSecondaryContainer = Color(0xff231b04),
    tertiary = Color(0xff755b00),
    onTertiary = Color(0xffffffff),
    tertiaryContainer = Color(0xffffdf90),
    onTertiaryContainer = Color(0xff241a00),
    error = Color(0xffba1a1a),
    onError = Color(0xffffffff),
    errorContainer = Color(0xffffdad6),
    onErrorContainer = Color(0xff410002),
    background = Color(0xfffffbff),
    onBackground = Color(0xff1e1b16),
    surface = Color(0xfffffbff),
    onSurface = Color(0xff1e1b16),
    outline = Color(0xff7e7667),
    surfaceVariant = Color(0xffece1cf),
    onSurfaceVariant = Color(0xff4c4639),

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun FireNotesTheme(
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

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}