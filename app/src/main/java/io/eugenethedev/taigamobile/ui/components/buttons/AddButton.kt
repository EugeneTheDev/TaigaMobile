package io.eugenethedev.taigamobile.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R

/**
 * Text button with plus icon on the left
 */
@Composable
fun AddButton(
    text: String,
    onClick: () -> Unit
) = TextButton(
    onClick = onClick,
    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )

        Text(
            text = text,
            color = MaterialTheme.colors.primary
        )
    }
}
