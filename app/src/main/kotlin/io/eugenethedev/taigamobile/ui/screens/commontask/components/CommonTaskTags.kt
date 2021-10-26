package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import io.eugenethedev.taigamobile.ui.components.buttons.AddButton
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.components.pickers.ColorPicker
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
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
            var isAddTagFieldVisible by remember { mutableStateOf(false) }

            commonTask.tags.forEach {
                TagItem(
                    tag = it,
                    onRemoveClick = { editActions.editTags.removeItem(it) }
                )
            }

            when {
                editActions.editTags.isResultLoading -> CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp
                )
                isAddTagFieldVisible -> AddTagField(
                    tags = editActions.editTags.items,
                    onInputChange = editActions.editTags.searchItems,
                    onSaveClick = { editActions.editTags.selectItem(it) }
                )
                else -> AddButton(
                    text = stringResource(R.string.add_tag),
                    onClick = { isAddTagFieldVisible = true }
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color = bgColor, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 4.dp, vertical = 2.dp)
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

@Composable
private fun AddTagField(
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
                                color = it.color.toColor(),
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
                onSaveClick(Tag(it, color.toHex()))
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
