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
val taigaOrange = Color(0xFFFF9900)
val taigaGreenPositive = Color(0xFF9DCE0A)
val taigaRed = Color(0xFFE44057)
val taigaLightGrayStatic = Color(0xFFF9F9FB)
val taigaGrayStatic = Color(0xFFECEFF4)
val taigaDarkGrayStatic = Color(0xFFD8DEE9)

// adjusted for dark/light theme
var taigaLightGrayDynamic = Color(0xFFF9F9FB)
    private set
var taigaGrayDynamic = Color(0xFFECEFF4)
    private set
var taigaDarkGrayDynamic = Color(0xFFD8DEE9)
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
        taigaLightGrayDynamic = taigaLightGrayStatic.copy(alpha = 0.04f)
        taigaGrayDynamic = taigaGrayStatic.copy(alpha = 0.08f)
        taigaDarkGrayDynamic = taigaDarkGrayStatic.copy(alpha = 0.12f)

        DarkColorPalette
    } else {
        // restore original colors for light theme
        taigaLightGrayDynamic = taigaLightGrayStatic
        taigaGrayDynamic = taigaGrayStatic
        taigaDarkGrayDynamic = taigaDarkGrayStatic

        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}