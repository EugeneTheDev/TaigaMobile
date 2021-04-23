package io.eugenethedev.taigamobile.ui.components.buttons

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R

@Composable
fun PlusButton(
    onClick: () -> Unit = {}
) = IconButton(
    onClick = onClick,
    modifier = Modifier.padding(top = 2.dp)
        .size(32.dp)
        .clip(CircleShape)
) {
    Icon(
        painter = painterResource(R.drawable.ic_add),
        contentDescription = null,
        tint = MaterialTheme.colors.primary,
        modifier = Modifier.size(26.dp)
    )
}