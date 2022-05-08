package io.eugenethedev.taigamobile.ui.screens.commontask.components

import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CustomField
import io.eugenethedev.taigamobile.domain.entities.CustomFieldType
import io.eugenethedev.taigamobile.domain.entities.CustomFieldValue
import io.eugenethedev.taigamobile.ui.components.DropdownSelector
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.components.pickers.DatePicker
import io.eugenethedev.taigamobile.ui.components.texts.MarkdownText
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.activity
import java.time.LocalDate
import kotlin.math.floor

@Composable
fun CustomField(
    customField: CustomField,
    value: CustomFieldValue?,
    onValueChange: (CustomFieldValue?) -> Unit,
    onSaveClick: () -> Unit
) = Column {
    Text(
       text = customField.name,
       style = MaterialTheme.typography.titleMedium
    )

    customField.description?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }

    Spacer(Modifier.height(4.dp))

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var showEditButton = false
    var buttonsAlignment = Alignment.CenterVertically

    var fieldState by remember { mutableStateOf(FieldState.Default) }
    val indicationColor = if (value == customField.value) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
    val borderColor = when (fieldState) {
        FieldState.Focused -> MaterialTheme.colorScheme.primary
        FieldState.Error -> MaterialTheme.colorScheme.error
        FieldState.Default -> indicationColor
    }

    Row {
        Box(
            Modifier
                .weight(1f)
                .border(
                    width = 1.5.dp,
                    color = if (customField.type == CustomFieldType.Checkbox) Color.Transparent else borderColor,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .clip(MaterialTheme.shapes.extraSmall)
                .padding(6.dp)
        ) {

            when (customField.type) {
                CustomFieldType.Text -> CustomFieldText(
                    value = value,
                    onValueChange = onValueChange,
                    changeFieldState = { fieldState = it }
                )

                CustomFieldType.Multiline -> {
                    buttonsAlignment = Alignment.Top

                    CustomFieldMultiline(
                        value = value,
                        onValueChange = onValueChange,
                        changeFieldState = { fieldState = it }
                    )
                }

                CustomFieldType.RichText -> {
                    buttonsAlignment = Alignment.Top
                    showEditButton = true

                    CustomFieldRichText(
                        value = value,
                        onValueChange = onValueChange,
                        fieldState = fieldState,
                        changeFieldState = { fieldState = it },
                        focusRequester = focusRequester
                    )
                }

                CustomFieldType.Number -> CustomFieldNumber(
                    value = value,
                    onValueChange = onValueChange,
                    changeFieldState = { fieldState = it }
                )

                CustomFieldType.Url -> CustomFieldUrl(
                    value = value,
                    onValueChange = onValueChange,
                    changeFieldState = { fieldState = it }
                )

                CustomFieldType.Date -> CustomFieldDate(
                    value = value,
                    onValueChange = onValueChange,
                    changeFieldState = { fieldState = it }
                )

                CustomFieldType.Dropdown -> CustomFieldDropdown(
                    options = customField.options ?: throw IllegalStateException("Dropdown custom field without options"),
                    borderColor = borderColor,
                    value = value,
                    onValueChange = onValueChange,
                    changeFieldState = { fieldState = it }
                )

                CustomFieldType.Checkbox -> CustomFieldCheckbox(
                    value = value,
                    onValueChange = onValueChange
                )
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
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                Spacer(Modifier.width(4.dp))
            }

            IconButton(
                onClick = {
                    if (fieldState != FieldState.Error && value != customField.value) {
                        focusManager.clearFocus()
                        onSaveClick()
                    }
                },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_save),
                    contentDescription = null,
                    tint = indicationColor
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
    textColor: Color = MaterialTheme.colorScheme.onSurface
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

@Composable
private fun CustomFieldText(
    value: CustomFieldValue?,
    onValueChange: (CustomFieldValue?) -> Unit,
    changeFieldState: (FieldState) -> Unit
) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(value?.stringValue.orEmpty())) }

    TextValue(
        hintId = R.string.custom_field_text,
        text = text,
        onTextChange = {
            text = it
            onValueChange(CustomFieldValue(it.text))
        },
        onFocusChange = { changeFieldState(if (it) FieldState.Focused else FieldState.Default) },
        singleLine = true
    )
}

@Composable
private fun CustomFieldMultiline(
    value: CustomFieldValue?,
    onValueChange: (CustomFieldValue?) -> Unit,
    changeFieldState: (FieldState) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(value?.stringValue.orEmpty())) }

    TextValue(
        hintId = R.string.custom_field_multiline,
        text = text,
        onTextChange = {
            text = it
            onValueChange(CustomFieldValue(it.text))
        },
        onFocusChange = { changeFieldState(if (it) FieldState.Focused else FieldState.Default) },
    )
}

@Composable
private fun CustomFieldRichText(
    value: CustomFieldValue?,
    onValueChange: (CustomFieldValue?) -> Unit,
    fieldState: FieldState,
    changeFieldState: (FieldState) -> Unit,
    focusRequester: FocusRequester
) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(value?.stringValue.orEmpty())) }

    if (fieldState == FieldState.Focused) {
        TextValue(
            hintId = R.string.custom_field_rich_text,
            text = text,
            onTextChange = {
                text = it
                onValueChange(CustomFieldValue(it.text))
            },
            onFocusChange = { changeFieldState(if (it) FieldState.Focused else FieldState.Default) },
            focusRequester = focusRequester
        )
        SideEffect {
            focusRequester.requestFocus()
        }
    } else {
        MarkdownText(text.text)
    }
}

@Composable
private fun CustomFieldNumber(
    value: CustomFieldValue?,
    onValueChange: (CustomFieldValue?) -> Unit,
    changeFieldState: (FieldState) -> Unit
) {
    // do not display trailing zeros, like 1.0
    fun Double?.prettyDisplay() = this?.let { if (floor(it) != it) toString() else "%.0f".format(it) }.orEmpty()

    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(value?.doubleValue.prettyDisplay())) }

    TextValue(
        hintId = R.string.custom_field_number,
        text = text,
        onTextChange = {
            text = it

            if (it.text.isEmpty()) {
                onValueChange(null)
                changeFieldState(FieldState.Focused)
            } else {
                it.text.toDoubleOrNull()?.let {
                    onValueChange(CustomFieldValue(it))
                    changeFieldState(FieldState.Focused)
                } ?: run {
                    changeFieldState(FieldState.Error)
                }
            }
        },
        onFocusChange = {
            text = TextFieldValue(value?.doubleValue.prettyDisplay())
            changeFieldState(if (it) FieldState.Focused else FieldState.Default)
        },
        keyboardType = KeyboardType.Number,
        singleLine = true
    )
}

@Composable
private fun CustomFieldUrl(
    value: CustomFieldValue?,
    onValueChange: (CustomFieldValue?) -> Unit,
    changeFieldState: (FieldState) -> Unit
) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(value?.stringValue.orEmpty())) }

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

                    it.text.takeIf { it.isEmpty() || Patterns.WEB_URL.matcher(it).matches() }
                        ?.let {
                            changeFieldState(FieldState.Focused)
                            onValueChange(CustomFieldValue(it))
                        } ?: run {
                            changeFieldState(FieldState.Error)
                        }
                },
                onFocusChange = {
                    text = TextFieldValue(value?.stringValue.orEmpty())
                    changeFieldState(if (it) FieldState.Focused else FieldState.Default)
                },
                keyboardType = KeyboardType.Uri,
                singleLine = true,
                textColor = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(2.dp))

        val activity = LocalContext.current.activity
        IconButton(
            onClick = {
                value?.stringValue?.takeIf { it.isNotEmpty() }?.let {
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
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CustomFieldDate(
    value: CustomFieldValue?,
    onValueChange: (CustomFieldValue?) -> Unit,
    changeFieldState: (FieldState) -> Unit
) {
    val date = value?.dateValue

    DatePicker(
        date = date,
        onDatePicked = { onValueChange(it?.let { CustomFieldValue(it) }) },
        onOpen = { changeFieldState(FieldState.Focused) },
        onClose = { changeFieldState(FieldState.Default) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CustomFieldDropdown(
    options: List<String>,
    borderColor: Color,
    value: CustomFieldValue?,
    onValueChange: (CustomFieldValue?) -> Unit,
    changeFieldState: (FieldState) -> Unit
) {
    val option = value?.stringValue.orEmpty()

    DropdownSelector(
        items = options,
        selectedItem = option,
        onItemSelected = {
            onValueChange(CustomFieldValue(it))
            changeFieldState(FieldState.Default)
        },
        itemContent = {
            if (it.isNotEmpty()) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = stringResource(R.string.empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        selectedItemContent = {
            Text(
                text = option,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        takeMaxWidth = true,
        horizontalArrangement = Arrangement.SpaceBetween,
        tint = borderColor,
        onExpanded = { changeFieldState(FieldState.Focused) },
        onDismissRequest = { changeFieldState(FieldState.Default) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomFieldCheckbox(
    value: CustomFieldValue?,
    onValueChange: (CustomFieldValue?) -> Unit
) {
    val state = value?.booleanValue ?: false

    Checkbox(
        checked = state,
        onCheckedChange = { onValueChange(CustomFieldValue(it)) }
    )
}


@Preview(showBackground = true)
@Composable
fun CustomFieldsPreview() = TaigaMobileTheme {
    Column {
        var value1 by remember { mutableStateOf<CustomFieldValue?>(CustomFieldValue("Sample value")) }

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Text,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("Sample value"),

            ),
            value = value1,
            onValueChange = { value1 = it },
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        var value2 by remember { mutableStateOf<CustomFieldValue?>(CustomFieldValue("Sample value")) }

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Multiline,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("Sample value"),

            ),
            value = value2,
            onValueChange = { value2 = it },
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        var value3 by remember { mutableStateOf<CustomFieldValue?>(CustomFieldValue("__Sample__ `value`")) }

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.RichText,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("__Sample__ `value`"),

            ),
            value = value3,
            onValueChange = { value3 = it },
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))


        var value4 by remember { mutableStateOf<CustomFieldValue?>(CustomFieldValue(42.0)) }

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Number,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue(42.0)
            ),
            value = value4,
            onValueChange = { value4 = it },
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        var value5 by remember { mutableStateOf<CustomFieldValue?>(CustomFieldValue("https://x.com")) }

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Url,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("https://x.com")
            ),
            value = value5,
            onValueChange = { value5 = it },
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        var value6 by remember { mutableStateOf<CustomFieldValue?>(CustomFieldValue(LocalDate.of(1970, 1, 1))) }

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Date,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue(LocalDate.of(1970, 1, 1))
            ),
            value = value6,
            onValueChange = { value6 = it },
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        var value7 by remember { mutableStateOf<CustomFieldValue?>(CustomFieldValue("Something 0")) }

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Dropdown,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue("Something 0"),
                options = listOf("", "Something 0", "Something 1", "Something 2")
            ),
            value = value7,
            onValueChange = { value7 = it },
            onSaveClick = { }
        )

        Spacer(Modifier.height(8.dp))

        var value8 by remember { mutableStateOf<CustomFieldValue?>(CustomFieldValue(true)) }

        CustomField(
            customField = CustomField(
                id = 0L,
                type = CustomFieldType.Checkbox,
                name = "Sample name",
                description = "Description",
                value = CustomFieldValue(true)
            ),
            value = value8,
            onValueChange = { value8 = it },
            onSaveClick = { }
        )
    }
}
