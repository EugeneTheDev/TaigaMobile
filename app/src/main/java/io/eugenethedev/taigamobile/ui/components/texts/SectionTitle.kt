package io.eugenethedev.taigamobile.ui.components.texts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.theme.veryLightGray

/**
 * Title with optional add button
 */

@Composable
fun SectionTitle(
    text: String,
    horizontalPadding: Dp = 0.dp,
    bottomPadding: Dp = 6.dp,
    onAddClick: (() -> Unit)? = null
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
        .height(IntrinsicSize.Min)
        .fillMaxWidth()
        .padding(horizontal = horizontalPadding)
        .padding(bottom = bottomPadding)
        .background(veryLightGray, MaterialTheme.shapes.medium)
) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(6.dp)
    )

    onAddClick?.let {
        Box(
            modifier = Modifier.fillMaxHeight()
                .aspectRatio(1f)
                .background(MaterialTheme.colors.primary, MaterialTheme.shapes.small)
                .clickable(
                    onClick = it,
                    role = Role.Button,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                )
                .padding(6.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
