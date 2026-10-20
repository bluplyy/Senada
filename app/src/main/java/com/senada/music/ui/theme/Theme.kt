package com.senada.music.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val SenadaColorScheme = lightColorScheme(
    primary = Action,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = Sage,
    onSecondary = Ink,
    tertiary = Pink,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink
)

val SenadaShapes = Shapes(
    small = RoundedCornerShape(12.dp),   // radius-sm: 12 dp
    medium = RoundedCornerShape(20.dp),  // radius-md: 20 dp
    large = RoundedCornerShape(24.dp),   // radius-lg: 24 dp
    extraLarge = RoundedCornerShape(32.dp) // tombol 64 dp
)

@Composable
fun SenadaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            // Edge-to-edge transparent system bars
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            val insetsController = WindowCompat.getInsetsController(window, view)
            // Desain bertema krem terang: status bar dan nav bar icon gelap (isAppearanceLight = true)
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = SenadaColorScheme,
        typography = SenadaTypography,
        shapes = SenadaShapes,
        content = content
    )
}
