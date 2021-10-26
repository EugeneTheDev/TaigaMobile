package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
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

@OptIn(ExperimentalMaterialApi::class)
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
    elevation = 1.dp,
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