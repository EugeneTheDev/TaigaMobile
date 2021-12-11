package io.eugenethedev.taigamobile.ui.components.containers


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding

/**
 * Common for app view which is used as container for different items (for example list items).
 * It is clickable, has padding inside and ripple effect
 */

// TODO check that everything is alright now
@Composable
fun ContainerBox(
    horizontalPadding: Dp = mainHorizontalScreenPadding,
    verticalPadding: Dp = 8.dp,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {}
) = Surface(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontalPadding, verticalPadding),
    content = {
        Box(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    },
    onClick = onClick
)