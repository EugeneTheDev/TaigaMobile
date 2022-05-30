package io.eugenethedev.taigamobile.ui.components.lists

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import io.eugenethedev.taigamobile.ui.components.texts.MarkdownText
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText

@Suppress("FunctionName")
fun LazyListScope.Description(
    description: String
) {
    item {
        if (description.isNotEmpty()) {
            MarkdownText(
                text = description,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            NothingToSeeHereText()
        }
    }
}
