package com.example.applionscuts.ui.theme

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
import com.example.applionscuts.ui.theme.theme.LionsCutsBlack
import com.example.applionscuts.ui.theme.theme.LionsCutsDarkGray
import com.example.applionscuts.ui.theme.theme.LionsCutsLightGray
import com.example.applionscuts.ui.theme.theme.LionsCutsWhite
import com.example.applionscuts.ui.theme.theme.LionsCutsYellow

private val DarkColorScheme = darkColorScheme(
    primary = LionsCutsYellow,
    onPrimary = LionsCutsBlack,
    secondary = LionsCutsLightGray,
    background = LionsCutsBlack,
    surface = LionsCutsDarkGray,
    onBackground = LionsCutsWhite,
    onSurface = LionsCutsWhite
)

private val LightColorScheme = lightColorScheme(
    primary = LionsCutsYellow,
    onPrimary = LionsCutsBlack,
    secondary = LionsCutsDarkGray,
    background = LionsCutsWhite,
    surface = LionsCutsWhite,
    onBackground = LionsCutsBlack,
    onSurface = LionsCutsBlack
)

@Composable
fun AppLionsCutsTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme: Boolean = isSystemInDarkTheme()

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

            window.statusBarColor = LionsCutsBlack.toArgb()

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}