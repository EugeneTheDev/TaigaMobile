package io.eugenethedev.taigamobile.ui.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated

/**
 * Dropdown selector with animated arrow
 */

@Composable
fun <T> DropdownSelector(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemContent: @Composable (T) -> Unit,
    selectedItemContent: @Composable (T) -> Unit,
    takeMaxWidth: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    tint: Color = MaterialTheme.colorScheme.primary,
    onExpanded: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    val transitionState = remember { MutableTransitionState(isExpanded) }
    transitionState.targetState = isExpanded

    if (isExpanded) onExpanded()
    
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement,
            modifier = Modifier.let { if (takeMaxWidth) it.fillMaxWidth() else it }
                .clickableUnindicated {
                    isExpanded = !isExpanded
                }
        ) {

            selectedItemContent(selectedItem)

            val arrowRotation by updateTransition(
                transitionState,
                label = "arrow"
            ).animateFloat { if (it) -180f else 0f }

            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.rotate(arrowRotation)
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
                onDismissRequest()
            }
        ) {
            items.forEach {
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        onItemSelected(it)
                    }
                ) {
                    itemContent(it)
                }
            }
        }
    }
}
