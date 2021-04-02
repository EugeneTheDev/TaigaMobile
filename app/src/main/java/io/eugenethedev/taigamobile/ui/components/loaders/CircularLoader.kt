package io.eugenethedev.taigamobile.ui.components.loaders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Centered circular loader for some screens
 */
@Composable
fun CircularLoader() = Box(
    modifier = Modifier.fillMaxWidth().padding(8.dp),
    contentAlignment = Alignment.Center
) {
    CircularProgressIndicator(Modifier.size(40.dp))
}