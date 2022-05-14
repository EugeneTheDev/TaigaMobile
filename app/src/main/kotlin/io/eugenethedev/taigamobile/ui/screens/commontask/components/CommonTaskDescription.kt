package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.ui.components.texts.MarkdownText
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText

@Suppress("FunctionName")
fun LazyListScope.CommonTaskDescription(
    commonTask: CommonTaskExtended
) {
    item {
        if (commonTask.description.isNotEmpty()) {
            MarkdownText(
                text = commonTask.description,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            NothingToSeeHereText()
        }
    }
}
