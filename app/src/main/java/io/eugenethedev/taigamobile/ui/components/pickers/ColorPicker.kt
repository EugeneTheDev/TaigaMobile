package io.eugenethedev.taigamobile.ui.components.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.buttons
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.title
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated

@Composable
fun ColorPicker(
    size: Dp,
    color: Color,
    onColorPicked: (Color) -> Unit = {}
) {
    val dialog = remember { MaterialDialog() }

    dialog.build {
        title(stringResource(R.string.select_color))

        colorChooser(
            colors = (listOf(color) + ColorPalette.Primary).toSet().toList(),
            onColorSelected = onColorPicked,
            argbPickerState = ARGBPickerState.WithoutAlphaSelector
        )

        buttons {
            positiveButton(res = R.string.ok)
            negativeButton(res = R.string.cancel)
        }
    }

    Spacer(
        Modifier
            .size(size)
            .background(color = color, shape = MaterialTheme.shapes.medium)
            .clickableUnindicated { dialog.show() }
    )
}