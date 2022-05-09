package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.vanpra.composematerialdialogs.color.ColorPalette
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.Tag
import io.eugenethedev.taigamobile.ui.components.Chip
import io.eugenethedev.taigamobile.ui.components.buttons.AddButton
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.components.pickers.ColorPicker
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.theme.dialogTonalElevation
import io.eugenethedev.taigamobile.ui.utils.surfaceColorAtElevation
import io.eugenethedev.taigamobile.ui.utils.textColor
import io.eugenethedev.taigamobile.ui.utils.toColor
import io.eugenethedev.taigamobile.ui.utils.toHex

fun LazyListScope.CommonTaskTags(
    commonTask: CommonTaskExtended,
    editActions: EditActions
) {
    item {
        FlowRow(
            crossAxisAlignment = FlowCrossAxisAlignment.Center,
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            var isAddTagDialogVisible by remember { mutableStateOf(false) }

            commonTask.tags.forEach {
                TagItem(
                    tag = it,
                    onRemoveClick = { editActions.editTags.removeItem(it) }
                )
            }

            if (editActions.editTags.isResultLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                AddButton(
                    text = stringResource(R.string.add_tag),
                    onClick = { isAddTagDialogVisible = true }
                )
            }

            if (isAddTagDialogVisible) {
                AddTagDialog(
                    tags = editActions.editTags.items,
                    onInputChange = editActions.editTags.searchItems,
                    onConfirm = {
                        editActions.editTags.selectItem(it)
                        isAddTagDialogVisible = false
                    },
                    onDismiss = { isAddTagDialogVisible = false }
                )
            }
        }
    }
}

@Composable
private fun TagItem(
    tag: Tag,
    onRemoveClick: () -> Unit
) {
    val bgColor = tag.color.toColor()
    val textColor = bgColor.textColor()

    Chip(
        color = bgColor,
        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = tag.name,
                color = textColor
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
                    tint = textColor
                )
            }
        }
    }
}

@Composable
private fun AddTagDialog(
    tags: List<Tag>,
    onInputChange: (String) -> Unit,
    onConfirm: (Tag) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    var color by remember { mutableStateOf(ColorPalette.Primary.first()) }
    var isDropdownVisible by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.text.isNotBlank()) {
                        onConfirm(Tag(name.text, color.toHex()))
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.ok),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.add_tag),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    TextFieldWithHint(
                        hintId = R.string.tag,
                        value = name,
                        onValueChange = {
                            name = it
                            // if dropdown menu item has been chosen - do not show dropdown again
                            if (tags.none { it.name == name.text}) {
                                isDropdownVisible = true
                                onInputChange(it.text)
                            }
                        },
                        width = 180.dp,
                        hasBorder = true,
                        singleLine = true
                    )

                    if (isDropdownVisible) {
                        DropdownMenu(
                            expanded = tags.isNotEmpty(),
                            onDismissRequest = { isDropdownVisible = false },
                            properties = PopupProperties(clippingEnabled = false),
                            modifier = Modifier
                                .heightIn(max = 200.dp)
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(dialogTonalElevation))
                        ) {
                            tags.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        name = TextFieldValue(it.name)
                                        color = it.color.toColor()
                                        isDropdownVisible = false
                                    },
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Spacer(
                                                Modifier
                                                    .size(22.dp)
                                                    .background(
                                                        color = it.color.toColor(),
                                                        shape = MaterialTheme.shapes.extraSmall
                                                    )
                                            )

                                            Spacer(Modifier.width(4.dp))

                                            Text(
                                                text = it.name,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                ColorPicker(
                    size = 32.dp,
                    color = color,
                    onColorPicked = { color = it }
                )
            }
        }
    )
}
