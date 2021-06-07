package io.eugenethedev.taigamobile.ui.screens.commontask

import io.eugenethedev.taigamobile.domain.entities.Attachment
import io.eugenethedev.taigamobile.domain.entities.Comment
import java.io.InputStream


/**
 * Common fields when performing editing (choosing something from list)
 */
class EditAction<T>(
    val items: List<T> = emptyList(),
    val loadItems: (query: String?) -> Unit = {},
    val isItemsLoading: Boolean = false,
    val selectItem: (item: T) -> Unit = {},
    val isResultLoading: Boolean = false,
    val removeItem: (item: T) -> Unit = {}
)

class EditCommentsAction(
    val createComment: (String) -> Unit = {},
    val deleteComment: (Comment) -> Unit = {},
    val isResultLoading: Boolean = false
)

class EditAttachmentsAction(
    val deleteAttachment: (Attachment) -> Unit = {},
    val addAttachment: (name: String, stream: InputStream) -> Unit = { _, _ -> },
    val isResultLoading: Boolean = false
)
