package io.eugenethedev.taigamobile.ui.screens.wiki.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

fun LazyListScope.AdvancedSpacer(sectionsPadding: Dp, isVertical: Boolean = true) {
    item {
        Spacer(
            if (isVertical)
                Modifier.height(sectionsPadding)
            else
                Modifier.width(sectionsPadding)
        )
    }
}