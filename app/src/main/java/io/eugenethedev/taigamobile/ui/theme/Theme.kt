package io.eugenethedev.taigamobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

private val DarkColorPalette = darkColors(
        primary = taigaGreen,
        primaryVariant = taigaGreenDark,
)

private val LightColorPalette = lightColors(
        primary = taigaGreen,
        primaryVariant = taigaGreenDark
)

val LocalIsDarkMode = compositionLocalOf<Boolean> { error("No LocalIsDarkMode mode provided") }

@Composable
fun TaigaMobileTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = shapes,
            content = content
    )
}