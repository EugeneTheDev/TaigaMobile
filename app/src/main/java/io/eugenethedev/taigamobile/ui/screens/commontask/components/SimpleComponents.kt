package io.eugenethedev.taigamobile.ui.screens.commontask.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.vanpra.composematerialdialogs.color.ColorPalette
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.components.ConfirmActionAlert
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.components.lists.UserItem
import io.eugenethedev.taigamobile.ui.components.pickers.ColorPicker
import io.eugenethedev.taigamobile.ui.components.texts.MarkdownText
import io.eugenethedev.taigamobile.ui.components.texts.TitleWithIndicators
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import io.eugenethedev.taigamobile.ui.utils.safeParseHexColor

/**
 * Set of simple components for CommonTaskScreen
 */

@Composable
fun EpicItemWithAction(
    epic: EpicShortInfo,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionAlert(
            title = stringResource(R.string.unlink_epic_title),
            text = stringResource(R.string.unlink_epic_text),
            onConfirm = {
                isAlertVisible = false
                onRemoveClick()
            },
            onDismiss = { isAlertVisible = false }
        )
    }

    TitleWithIndicators(
        ref = epic.ref,
        title = epic.title,
        textColor = MaterialTheme.colors.primary,
        indicatorColorsHex = listOf(epic.color),
        modifier = Modifier
            .weight(1f)
            .padding(end = 4.dp)
            .clickableUnindicated(onClick = onClick)
    )

    IconButton(
        onClick = { isAlertVisible = true },
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_remove),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
fun TagItem(
    tag: Tag,
    onRemoveClick: () -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .background(color = safeParseHexColor(tag.color), shape = MaterialTheme.shapes.small)
        .padding(horizontal = 4.dp, vertical = 2.dp)
) {
    Text(
        text = tag.name,
        color = Color.White
    )

    Spacer(Modifier.width(2.dp))

    IconButton(
        onClick = onRemoveClick,
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_remove),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun AddTagField(
    tags: List<Tag>,
    onInputChange: (String) -> Unit,
    onSaveClick: (Tag) -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically
) {
    var value by remember { mutableStateOf(TextFieldValue()) }
    var color by remember { mutableStateOf(ColorPalette.Primary.first()) }

    Column {
        TextFieldWithHint(
            hintId = R.string.tag,
            value = value,
            onValueChange = {
                value = it
                onInputChange(it.text)
            },
            width = 180.dp,
            hasBorder = true,
            singleLine = true
        )

        DropdownMenu(
            expanded = tags.isNotEmpty(),
            onDismissRequest = {},
            properties = PopupProperties(clippingEnabled = false),
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            tags.forEach {
                DropdownMenuItem(onClick = { onSaveClick(it) }) {
                    Spacer(
                        Modifier.size(22.dp)
                            .background(
                                color = safeParseHexColor(it.color),
                                shape = MaterialTheme.shapes.small
                            )
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }

    Spacer(Modifier.width(4.dp))

    ColorPicker(
        size = 32.dp,
        color = color,
        onColorPicked = { color = it }
    )

    Spacer(Modifier.width(2.dp))

    IconButton(
        onClick = {
            value.text.takeIf { it.isNotEmpty() }?.let {
                onSaveClick(Tag(it, "#%08X".format(color.toArgb()).replace("#FF", "#")))
                value = TextFieldValue()
            }
        },
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_save),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}


@Composable
fun UserStoryItem(
    story: UserStoryShortInfo,
    onClick: () -> Unit
) = TitleWithIndicators(
    ref = story.ref,
    title = story.title,
    textColor = MaterialTheme.colors.primary,
    indicatorColorsHex = story.epicColors,
    modifier = Modifier.clickableUnindicated(onClick = onClick)
)

@Composable
fun UserItemWithAction(
    user: User,
    onRemoveClick: () -> Unit
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionAlert(
            title = stringResource(R.string.remove_user_title),
            text = stringResource(R.string.remove_user_text),
            onConfirm = {
                isAlertVisible = false
                onRemoveClick()
            },
            onDismiss = { isAlertVisible = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        UserItem(user)

        IconButton(onClick = { isAlertVisible = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_remove),
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun AttachmentItem(
    attachment: Attachment,
    onRemoveClick: () -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth()
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionAlert(
            title = stringResource(R.string.remove_attachment_title),
            text = stringResource(R.string.remove_attachment_text),
            onConfirm = {
                isAlertVisible = false
                onRemoveClick()
            },
            onDismiss = { isAlertVisible = false }
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .weight(1f, fill = false)
            .padding(end = 4.dp)
    ) {
        val activity = LocalContext.current as Activity
        Icon(
            painter = painterResource(R.drawable.ic_attachment),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.padding(end = 2.dp)
        )

        Text(
            text = attachment.name,
            color = MaterialTheme.colors.primary,
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
            tint = Color.Red
        )
    }

}

@Composable
fun CommentItem(
    comment: Comment,
    onDeleteClick: () -> Unit
) = Column {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionAlert(
            title = stringResource(R.string.delete_comment_title),
            text = stringResource(R.string.delete_comment_text),
            onConfirm = {
                isAlertVisible = false
                onDeleteClick()
            },
            onDismiss = { isAlertVisible = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        UserItem(
            user = comment.author,
            dateTime = comment.postDateTime
        )

        if (comment.canDelete == true) {
            IconButton(onClick = { isAlertVisible = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }

    MarkdownText(
        text = comment.text,
        modifier = Modifier.padding(start = 4.dp)
    )
}