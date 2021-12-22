package io.eugenethedev.taigamobile.ui.components.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.theme.shapes
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated

/**
 * Color picker with material dialog
 */

@Composable
fun ColorPicker(
    size: Dp,
    color: Color,
    onColorPicked: (Color) -> Unit = {}
) {
    val dialogState = rememberMaterialDialogState()

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            // TODO update buttons to comply with material3 color schema?
            positiveButton(res = R.string.ok)
            negativeButton(res = R.string.cancel)
        }
    ) {
        title(stringResource(R.string.select_color))

        colorChooser(
            colors = (listOf(color) + ColorPalette.Primary).toSet().toList(),
            onColorSelected = onColorPicked,
            argbPickerState = ARGBPickerState.WithoutAlphaSelector
        )
    }

    Spacer(
        Modifier.size(size)
            .background(color = color, shape = shapes.medium)
            .clickableUnindicated { dialogState.show() }
    )
}
