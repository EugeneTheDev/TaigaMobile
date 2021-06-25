package io.eugenethedev.taigamobile.ui.screens.commontask

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.buttons
import com.vanpra.composematerialdialogs.datetime.datepicker.datepicker
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CustomField
import io.eugenethedev.taigamobile.domain.entities.CustomFieldType
import io.eugenethedev.taigamobile.domain.entities.CustomFieldValue
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.components.texts.MarkdownText
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun CustomField(
    customField: CustomField,
    onSaveClick: (CustomFieldValue?) -> Unit
) = Column {
    Text(
       text = customField.name,
       style = MaterialTheme.typography.subtitle1
    )

    customField.description?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.caption,
            color = Color.Gray
        )
    }

    Spacer(Modifier.height(4.dp))

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var showEditButton by remember { mutableStateOf(false) }
    var buttonsAlignment by remember { mutableStateOf(Alignment.CenterVertically) }

    var editedCustomFieldValue by remember { mutableStateOf(customField.value) }

    var fieldState by remember { mutableStateOf(FieldState.Default) }
    val borderColor by mutableStateOf(
            when (fieldState) {
            FieldState.Focused -> MaterialTheme.colors.primary
            FieldState.Error -> MaterialTheme.colors.error
            FieldState.Default -> if (editedCustomFieldValue == customField.value) Color.Gray else MaterialTheme.colors.primary
        }
    )

    fun CustomFieldValue?.orShowError() = this ?: run {
        if (fieldState != FieldState.Focused) fieldState = FieldState.Error
        customField.value
    }

    Row {
        Box(
            Modifier
                .weight(1f)
                .border(
                    width = if (customField.type == CustomFieldType.Checkbox) 0.dp else 2.dp,
                    color = borderColor,
                    shape = MaterialTheme.shapes.small
                )
                .clip(MaterialTheme.shapes.small)
                .padding(6.dp)
        ) {

            when (customField.type) {
                CustomFieldType.Text -> {
                    var text by remember { mutableStateOf(TextFieldValue(customField.value?.stringValue.orEmpty())) }
                    editedCustomFieldValue = CustomFieldValue(text.text)

                    TextValue(
                        hintId = R.string.custom_field_text,
                        text = text,
                        onTextChange = { text = it },
                        onFocusChange = { fieldState = if (it) FieldState.Focused else FieldState.Default },
                        singleLine = true
                    )
                }

                CustomFieldType.Multiline -> {
                    buttonsAlignment = Alignment.Top
                    var text by remember { mutableStateOf(TextFieldValue(customField.value?.stringValue.orEmpty())) }
                    editedCustomFieldValue = CustomFieldValue(text.text)

                    TextValue(
                        hintId = R.string.custom_field_multiline,
                        text = text,
                        onTextChange = { text = it },
                        onFocusChange = { fieldState = if (it) FieldState.Focused else FieldState.Default },
                    )
                }

                CustomFieldType.RichText -> {
                    buttonsAlignment = Alignment.Top
                    showEditButton = true
                    var text by remember { mutableStateOf(TextFieldValue(customField.value?.stringValue.orEmpty())) }
                    editedCustomFieldValue = CustomFieldValue(text.text)

                    if (fieldState == FieldState.Focused) {
                        TextValue(
                            hintId = R.string.custom_field_rich_text,
                            text = text,
                            onTextChange = { text = it },
                            onFocusChange = { fieldState = if (it) FieldState.Focused else FieldState.Default },
                            focusRequester = focusRequester
                        )
                        SideEffect {
                            focusRequester.requestFocus()
                        }
                    } else {
                         MarkdownText(text.text)
                    }
                }

                CustomFieldType.Number -> {
                    var text by remember { mutableStateOf(TextFieldValue(customField.value?.intValue?.toString().orEmpty())) }
                    editedCustomFieldValue = text.text.takeIf { it.isNotEmpty() }
                        ?.toIntOrNull()
                        ?.let { CustomFieldValue(it) }
                        .orShowError()

                    TextValue(
                        hintId = R.string.custom_field_number,
                        text = text,
                        onTextChange = {
                            text = it
                            fieldState = FieldState.Focused
                         },
                        onFocusChange = { fieldState = if (it) FieldState.Focused else FieldState.Default },
                        keyboardType = KeyboardType.Number,
                        singleLine = true
                    )
                }

                CustomFieldType.Url -> {
                    var text by remember { mutableStateOf(TextFieldValue(customField.value?.stringValue.orEmpty())) }
                    editedCustomFieldValue = text.text.takeIf { it.isEmpty() || Patterns.WEB_URL.matcher(it).matches() }
                        ?.let { CustomFieldValue(it) }
                        .orShowError()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(Modifier.weight(1f)) {
                            TextValue(
                                hintId = R.string.custom_field_url,
                                text = text,
                                onTextChange = {
                                    text = it
                                    fieldState = FieldState.Focused
                                },
                                onFocusChange = {
                                    fieldState = if (it) FieldState.Focused else FieldState.Default
                                },
                                keyboardType = KeyboardType.Uri,
                                singleLine = true,
                                textColor = MaterialTheme.colors.primary
                            )
                        }

                        Spacer(Modifier.width(2.dp))

                        val activity = LocalContext.current as Activity
                        IconButton(
                            onClick = {
                                customField.value?.stringValue?.takeIf { it.isNotEmpty() }?.let {
                                    activity.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(it)
                                        )
                                    )
                                }
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_open),
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }
                }

                CustomFieldType.Date -> {
                    var date by remember { mutableStateOf(customField.value?.dateValue) }
                    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

                    editedCustomFieldValue = date?.let { CustomFieldValue(it) }

                    val dialog = remember {
                        MaterialDialog(
                            autoDismiss = true,
                            onCloseRequest = { fieldState = FieldState.Default }
                        )
                    }
                    dialog.build {
                        datepicker(title = stringResource(R.string.select_date).uppercase()) {
                            date = it
                        }

                        buttons {
                            positiveButton(
                                res = R.string.ok,
                                onClick = { fieldState = FieldState.Default }
                            )
                            negativeButton(
                                res = R.string.cancel,
                                onClick = { fieldState = FieldState.Default }
                            )
                            button(
                                res = R.string.clear,
                                onClick = {
                                    date = null
                                    dialog.hide()
                                    fieldState = FieldState.Default
                                }
                            )
                        }

                    }


                    Text(
                        text = date?.format(dateFormatter) ?: stringResource(R.string.custom_field_date),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickableUnindicated {
                                fieldState = FieldState.Focused
                                dialog.show()
                            },
                        color = date?.let { MaterialTheme.colors.onSurface } ?: Color.Gray
                    )
                }

                CustomFieldType.Dropdown -> {
                    var option by remember { mutableStateOf(customField.value?.stringValue.orEmpty()) }

                    editedCustomFieldValue = CustomFieldValue(option)

                    val transitionState = remember { MutableTransitionState(fieldState == FieldState.Focused) }
                    transitionState.targetState = fieldState == FieldState.Focused
                    val arrowRotation: Float by updateTransition(
                        transitionState,
                        label = "arrow"
                    ).animateFloat { if (it) -180f else 0f }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickableUnindicated {
                                fieldState = FieldState.Focused
                            }
                    ) {
                        Text(
                            text = option,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_down),
                            contentDescription = null,
                            tint = borderColor,
                            modifier = Modifier.rotate(arrowRotation)
                        )
                    }

                    DropdownMenu(
                        expanded = fieldState == FieldState.Focused,
                        onDismissRequest = { fieldState = FieldState.Default }
                    ) {
                        customField.options?.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    option = it
                                    fieldState = FieldState.Default
                                }
                            ) {
                                if (it.isNotEmpty()) {
                                    Text(it)
                                } else {
                                    Text(
                                        text = stringResource(R.string.empty),
                                        color = Color.Gray
                                    )
                                }
                            }
                        } ?: throw IllegalStateException("Dropdown custom field without options")
                    }
                }

                CustomFieldType.Checkbox -> {
                    var state by remember { mutableStateOf(customField.value?.booleanValue ?: false) }

                    editedCustomFieldValue = CustomFieldValue(state)

                    Checkbox(
                        checked = state,
                        onCheckedChange = { state = it }
                    )
                }
            }
        }

        Row(Modifier.align(buttonsAlignment)) {
            if (showEditButton) {
                Spacer(Modifier.width(4.dp))

                IconButton(
                    onClick = {
                        fieldState = FieldState.Focused
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            } else {
                Spacer(Modifier.width(4.dp))
            }

            IconButton(
                onClick = {
                    if (fieldState != FieldState.Error) {
                        focusManager.clearFocus()
                        onSaveClick(editedCustomFieldValue)
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
    }
}

private enum class FieldState {
    Focused,
    Error,
    Default
}

@Composable
private fun TextValue(
    @StringRes hintId: Int,
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester = remember { FocusRequester() },
    singleLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    textColor: Color = MaterialTheme.colors.onSurface
) = TextFieldWithHint(
    hintId = hintId,
    value = text,
    onValueChange = onTextChange,
    onFocusChange = onFocusChange,
    focusRequester = focusRequester,
    singleLine = singleLine,
    keyboardType = keyboardType,
    textColor = textColor
)

@Preview(showBackground = true)
@Composable
fun CustomFieldsPreview() = TaigaMobileTheme {
    Column {
        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Text,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("Sample value"),

            ),
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Multiline,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("Sample value"),

            ),
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.RichText,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("__Sample__ `value`"),

            ),
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Number,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue(42)
            ),
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Url,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("https://x.com")
            ),
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Date,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue(LocalDate.now())
            ),
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Dropdown,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("Something 0"),
                options = listOf("", "Something 0", "Something 1", "Something 2")
            ),
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Checkbox,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue(true)
            ),
            onSaveClick = { }
        )
    }
}
