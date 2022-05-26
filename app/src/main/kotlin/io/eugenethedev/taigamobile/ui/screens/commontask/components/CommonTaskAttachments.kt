package io.eugenethedev.taigamobile.ui.screens.commontask.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Attachment
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.SectionTitle
import io.eugenethedev.taigamobile.ui.screens.commontask.EditAction
import io.eugenethedev.taigamobile.ui.screens.main.LocalFilePicker
import io.eugenethedev.taigamobile.ui.utils.activity
import java.io.InputStream

@Suppress("FunctionName")
fun LazyListScope.CommonTaskAttachments(
    attachments: List<Attachment>,
    editAttachments: EditAction<Pair<String, InputStream>, Attachment>
) {
    item {
        val filePicker = LocalFilePicker.current
        SectionTitle(
            text = stringResource(R.string.attachments_template).format(attachments.size),
            onAddClick = {
                filePicker.requestFile { file, stream -> editAttachments.select(file to stream) }
            }
        )
    }

    items(attachments) {
        AttachmentItem(
            attachment = it,
            onRemoveClick = { editAttachments.remove(it) }
        )
    }

    item {
        if (editAttachments.isLoading) {
            DotsLoader()
        }
    }
}

@Composable
private fun AttachmentItem(
    attachment: Attachment,
    onRemoveClick: () -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth()
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionDialog(
            title = stringResource(R.string.remove_attachment_title),
            text = stringResource(R.string.remove_attachment_text),
            onConfirm = {
                isAlertVisible = false
                onRemoveClick()
            },
            onDismiss = { isAlertVisible = false },
            iconId = R.drawable.ic_remove
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .weight(1f, fill = false)
            .padding(end = 4.dp)
    ) {
        val activity = LocalContext.current.activity
        Icon(
            painter = painterResource(R.drawable.ic_attachment),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(end = 2.dp)
        )

        Text(
            text = attachment.name,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(attachment.url)))
            }
        )
    }

    IconButton(
        onClick = { isAlertVisible = true },
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_delete),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
    }

}
