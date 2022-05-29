package io.eugenethedev.taigamobile.ui.screens.wiki

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

fun LazyListScope.AdvancedSpacer(sectionsPadding: Dp) {
    item {
        Spacer(
            Modifier.height(sectionsPadding)
        )
    }
}