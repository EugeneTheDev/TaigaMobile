package io.eugenethedev.taigamobile.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.eugenethedev.taigamobile.R

/**
 * Alert with loader and text
 */
@Composable
fun LoadingDialog() = Dialog(onDismissRequest = { /* cannot dismiss */ }) {
    Surface(shape = MaterialTheme.shapes.small) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(16.dp))

            Text(stringResource(R.string.loading))
        }
    }
}
