package io.eugenethedev.taigamobile.ui.components.buttons

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R

/**
 * Text button just with text
 */
@Composable
fun TextButton(
    text: String,
    icon: Int? = null,
    onClick: () -> Unit
) = FilledTonalButton(onClick = onClick) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}