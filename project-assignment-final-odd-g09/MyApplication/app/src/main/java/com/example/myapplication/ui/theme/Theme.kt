package com.example.myapplication.ui.theme

import android.app.Activity
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
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = MyYellow,
    onPrimary = Color.Black,
    primaryContainer = MyYellow30,
    onPrimaryContainer = Color.Black,

    secondary = MyGrey,
    onSecondary = Color.Black,
    secondaryContainer = MyYellow30,
    onSecondaryContainer = Color.Black,

    tertiary = MyYellow50,
    onTertiary = Color.Black,
    tertiaryContainer = MyDarkWhite,
    onTertiaryContainer = Color.Black,

    background = Color.White,
    onBackground = Color.Black,
    surface = MyDarkGrey2,
    onSurface = Color.Black,
    surfaceVariant = MyLightGrey,
    onSurfaceVariant = MyDarkGrey,
    outline = MyDarkGrey,

    error = MyRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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