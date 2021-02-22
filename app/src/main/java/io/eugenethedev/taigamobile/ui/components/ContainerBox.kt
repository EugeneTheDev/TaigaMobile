package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding

/**
 * Common for app view which is used as container for different items (for example list items).
 * It is clickable, has padding inside and ripple effect
 */
@Composable
fun ContainerBox(
    horizontalPadding: Dp = mainHorizontalScreenPadding,
    verticalPadding: Dp = 8.dp,
    clickEnabled: Boolean = true,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {}
) = Box(
    contentAlignment = Alignment.CenterStart,
    modifier = Modifier
        .fillMaxWidth()
        .clickable(
            enabled = clickEnabled,
            interactionState = remember { InteractionState() },
            indication = rememberRipple(
                bounded = true,
                color = MaterialTheme.colors.primary
            ),
            onClick = onClick
        )
        .padding(horizontalPadding, verticalPadding),
    content = content
)