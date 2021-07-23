package io.eugenethedev.taigamobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Colors
 */
val taigaGreen = Color(0xFF25A28C)
val taigaGreenDark = Color(0xFF00796D)

var taigaLightGray = Color(0xFFF9F9FB)
    private set
var taigaGray = Color(0xFFECEFF4)
    private set
var taigaDarkGray = Color(0xFFD8DEE9)
    private set


private val DarkColorPalette = darkColors(
    primary = taigaGreen,
    primaryVariant = taigaGreenDark,
)

private val LightColorPalette = lightColors(
    primary = taigaGreen,
    primaryVariant = taigaGreenDark
)

@Composable
fun TaigaMobileTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        // adjust some colors for dark theme
        taigaLightGray = taigaLightGray.copy(alpha = 0.04f)
        taigaGray = taigaGray.copy(alpha = 0.08f)
        taigaDarkGray = taigaDarkGray.copy(alpha = 0.12f)

        DarkColorPalette
    } else {
        // restore original colors for light theme
        taigaLightGray = taigaLightGray.copy(alpha = 1f)
        taigaGray = taigaGray.copy(alpha = 1f)
        taigaDarkGray = taigaDarkGray.copy(alpha = 1f)

        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = shapes,
            content = content
    )
}