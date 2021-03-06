package io.eugenethedev.taigamobile.ui.components.editors

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding

/**
 * You've read it right. Text field. With hint.
 */
@Composable
fun TextFieldWithHint(
    @StringRes hintId: Int,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    horizontalPadding: Dp = 0.dp,
    verticalPadding: Dp = 0.dp,
    width: Dp? = null,
    style: TextStyle = MaterialTheme.typography.body1,
    singleLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onFocusChange: (Boolean) -> Unit = {},
    focusRequester: FocusRequester = remember { FocusRequester() },
    maxLines: Int = Int.MAX_VALUE,
    textColor: Color = MaterialTheme.colors.onSurface,
    onSearchClick: (() -> Unit)? = null,
    hasBorder: Boolean = false
) {
    val primaryColor = MaterialTheme.colors.primary
    var outlineColor by remember { mutableStateOf(Color.Gray) }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.let { m -> width?.let { m.width(it) } ?: m.fillMaxWidth() }
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .let {
                if (hasBorder) {
                    it.border(width = 2.dp, color = outlineColor, shape = MaterialTheme.shapes.medium)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                } else {
                    it
                }
            }
    ) {
        if (value.text.isEmpty()) {
            Text(
                text = stringResource(hintId),
                style = style,
                color = Color.Gray
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    onFocusChange(it.isFocused)
                    outlineColor = if (it.isFocused) primaryColor else Color.Gray
                },
            textStyle = style.merge(TextStyle(color = textColor)),
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                imeAction = onSearchClick?.let { ImeAction.Search } ?: ImeAction.Default,
                keyboardType = keyboardType
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearchClick?.invoke() })
        )
    }
}

val searchFieldHorizontalPadding = mainHorizontalScreenPadding
val searchFieldVerticalPadding = 8.dp
