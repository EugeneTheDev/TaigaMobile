package io.eugenethedev.taigamobile.ui.theme

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object TaigaMobileRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color = MaterialTheme.colorScheme.secondary

    @Composable
    override fun rippleAlpha() = RippleAlpha(
        pressedAlpha = 0.10f,
        focusedAlpha = 0.12f,
        draggedAlpha = 0.08f,
        hoveredAlpha = 0.04f
    )
}