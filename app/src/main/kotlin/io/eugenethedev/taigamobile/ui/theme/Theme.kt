package io.eugenethedev.taigamobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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


private val DarkColorPalette = darkColorScheme(
    primary = taigaGreen,
    secondary = taigaGreenDark,
    secondaryContainer = taigaGreen.copy(alpha = 0.5f)
)

private val LightColorPalette = lightColorScheme(
    primary = taigaGreen,
    secondary = taigaGreenDark,
    secondaryContainer = taigaGreen.copy(alpha = 0.3f)
)

@Composable
fun TaigaMobileTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        // adjust some colors for dark theme
        taigaLightGrayDynamic = taigaLightGrayStatic.copy(alpha = 0.04f)
        taigaGrayDynamic = taigaGrayStatic.copy(alpha = 0.08f)
        taigaDarkGrayDynamic = taigaDarkGrayStatic.copy(alpha = 0.12f)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicDarkColorScheme(LocalContext.current)
        else DarkColorPalette
    } else {
        // restore original colors for light theme
        taigaLightGrayDynamic = taigaLightGrayStatic
        taigaGrayDynamic = taigaGrayStatic
        taigaDarkGrayDynamic = taigaDarkGrayStatic

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) dynamicLightColorScheme(LocalContext.current)
        else LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
}