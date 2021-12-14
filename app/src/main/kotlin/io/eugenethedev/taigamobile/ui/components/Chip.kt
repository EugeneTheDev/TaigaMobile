package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.taigaGrayStatic
import io.eugenethedev.taigamobile.ui.utils.textColor

/**
 * Material chip component (rounded rectangle)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chip(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    color: Color = taigaGrayStatic,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement.provides(onClick != null)
    ) {
        Surface(
            onClick = onClick ?: {},
            enabled = onClick != null,
            shape = RoundedCornerShape(50),
            color = color,
            contentColor = color.textColor(),
            shadowElevation = 1.dp,
            modifier = modifier,
            indication = rememberRipple()
        ) {
            Box(modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp)) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun ChipPreview() = TaigaMobileTheme {
    Box(modifier = Modifier.padding(10.dp)) {
        Chip {
            Text("Testing chip")
        }
    }
}