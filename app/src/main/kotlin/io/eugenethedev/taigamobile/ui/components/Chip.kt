package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    color: Color = taigaGrayStatic,
    content: @Composable () -> Unit
) = Surface(
    onClick = onClick,
    shape = RoundedCornerShape(50),
    color = color,
    contentColor = color.textColor(),
    tonalElevation = 1.dp, // TODO probably shadow elevation?
    modifier = modifier
) {
    Box(modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp)) {
        content()
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