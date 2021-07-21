package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Attachment
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.SectionTitle
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.screens.main.LocalFilePicker

fun LazyListScope.CommonTaskAttachments(
    attachments: List<Attachment>,
    editActions: EditActions
) {
    item {
        val filePicker = LocalFilePicker.current
        SectionTitle(
            text = stringResource(R.string.attachments_template).format(attachments.size),
            onAddClick = {
                filePicker.requestFile(editActions.editAttachments.addAttachment)
            }
        )
    }

    items(attachments) {
        AttachmentItem(
            attachment = it,
            onRemoveClick = { editActions.editAttachments.deleteAttachment(it) }
        )
    }

    item {
        if (editActions.editAttachments.isResultLoading) {
            DotsLoader()
        }
    }
}
