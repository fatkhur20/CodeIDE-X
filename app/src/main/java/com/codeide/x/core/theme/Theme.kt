package com.codeide.x.core.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VSCodeDarkColors = darkColorScheme(
    primary = Color(0xFF569CD6),
    onPrimary = Color(White),
    primaryContainer = Color(0xFF264F78),
    onPrimaryContainer = Color(0xFF9ECDFF),
    secondary = Color(0xFF4EC9B0),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1E3A2F),
    onSecondaryContainer = Color(0xFFA6F3E6),
    tertiary = Color(0xFFDCDCAA),
    onTertiary = Color.Black,
    background = Color(0xFF1E1E1E),
    onBackground = Color(0xFFD4D4D4),
    surface = Color(0xFF252526),
    onSurface = Color(0xFFD4D4D4),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFF9D9D9D),
    outline = Color(0xFF404040),
    outlineVariant = Color(0xFF303030),
    error = Color(0xFFF44747),
    onError = Color.White,
    surfaceContainerLow = Color(0xFF1E1E1E),
    surfaceContainer = Color(0xFF252526),
    surfaceContainerHigh = Color(0xFF2D2D2D),
    surfaceContainerHighest = Color(0xFF333333)
)

private val White = Color(0xFFFFFFFF)

@Composable
fun CodeIDEXTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VSCodeDarkColors else VSCodeDarkColors
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF252526).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
