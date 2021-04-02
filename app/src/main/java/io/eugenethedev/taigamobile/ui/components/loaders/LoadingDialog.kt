package io.eugenethedev.taigamobile.ui.components.loaders

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
    Surface(shape = MaterialTheme.shapes.medium) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp
            )

            Spacer(Modifier.width(16.dp))

            Text(stringResource(R.string.loading))
        }
    }
}