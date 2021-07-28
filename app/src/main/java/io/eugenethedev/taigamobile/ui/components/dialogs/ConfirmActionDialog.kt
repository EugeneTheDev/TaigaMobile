package io.eugenethedev.taigamobile.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R

/**
 * Standard confirmation alert with "yes" "no" buttons, title and text
 */
@Composable
fun ConfirmActionDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) = AlertDialog(
    onDismissRequest = onDismiss,
    buttons = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.no),
                    style = MaterialTheme.typography.subtitle1
                )
            }

            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.yes),
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
    },
    title = {
        Text(
            text = title,
            style = MaterialTheme.typography.h6
        )
    },
    text = { Text(text) }
)
