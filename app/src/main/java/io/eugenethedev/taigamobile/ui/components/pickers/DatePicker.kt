package io.eugenethedev.taigamobile.ui.components.pickers

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.buttons
import com.vanpra.composematerialdialogs.datetime.datepicker.datepicker
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Date picker with material dialog. Null passed to onDatePicked() means selection was cleared
 */

@Composable
fun DatePicker(
    date: LocalDate?,
    onDatePicked: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes hintId: Int = R.string.date_hint,
    onClose: () -> Unit = {},
    onOpen: () -> Unit = {}
) = Box {
    var pickedDate by remember { mutableStateOf(date) }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    val dialog = remember {
        MaterialDialog(
            autoDismiss = true,
            onCloseRequest = { onClose() }
        )
    }
    dialog.build {
        datepicker(
            title = stringResource(R.string.select_date).uppercase(),
            onDateChange = { pickedDate = it },
            initialDate = date ?: LocalDate.now()
        )

        buttons {
            positiveButton(
                res = R.string.ok,
                onClick = {
                    onDatePicked(pickedDate)
                    onClose()
                }
            )
            negativeButton(
                res = R.string.cancel,
                onClick = onClose
            )
            button(
                res = R.string.clear,
                onClick = {
                    onDatePicked(null)
                    dialog.hide()
                    onClose()
                }
            )
        }

    }

    Text(
        text = date?.format(dateFormatter) ?: stringResource(hintId),
        modifier = modifier.clickableUnindicated {
                onOpen()
                dialog.show()
            },
        color = date?.let { MaterialTheme.colors.onSurface } ?: Color.Gray
    )
}
