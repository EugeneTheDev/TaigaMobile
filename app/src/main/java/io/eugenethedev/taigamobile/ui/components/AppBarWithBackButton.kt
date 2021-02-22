package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.Interaction
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R

@Composable
fun AppBarWithBackButton(
    title: @Composable () -> Unit = {},
    navigateBack: () -> Unit = {}
) = TopAppBar(
    title = title,
    navigationIcon = {
        val interactionState = remember { InteractionState() }

        Image(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                if (Interaction.Pressed in interactionState.value) {
                    MaterialTheme.colors.primaryVariant
                } else {
                    MaterialTheme.colors.primary
                }
            ),
            modifier = Modifier
                .size(36.dp)
                .padding(start = 8.dp)
                .clickable(
                    interactionState = interactionState,
                    indication = null,
                    onClick = navigateBack
                )
        )
    },
    backgroundColor = MaterialTheme.colors.surface,
    elevation = 0.dp
)