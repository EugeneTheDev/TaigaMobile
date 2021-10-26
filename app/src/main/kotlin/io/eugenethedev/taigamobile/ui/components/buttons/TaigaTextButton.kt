package io.eugenethedev.taigamobile.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.ui.theme.taigaLightGray

/**
 * Text button with proper style
 */
@Composable
fun TaigaTextButton(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) = TextButton(
    onClick = onClick,
    colors = ButtonDefaults.textButtonColors(
        backgroundColor = taigaLightGray
    ),
    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    content = content
)
