package io.eugenethedev.taigamobile.ui.components.editors

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding

/**
 * Outlined text field with hint
 */
@Composable
fun SearchField(
    @StringRes hintId: Int,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    horizontalPadding: Dp = mainHorizontalScreenPadding,
    verticalPadding: Dp = 8.dp,
    onSearchClick: () -> Unit = {}
) = Box(
    modifier = Modifier.fillMaxWidth()
        .padding(horizontal = horizontalPadding, vertical = verticalPadding),
    contentAlignment = Alignment.CenterStart
) {
    val textPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)

    if (value.text.isEmpty()) {
        Text(
            text = stringResource(hintId),
            style = MaterialTheme.typography.body1,
            color = Color.Gray,
            modifier = Modifier.padding(textPadding)
        )
    }

    val primaryColor = MaterialTheme.colors.primary
    var outlineColor by remember { mutableStateOf(Color.Gray) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth()
            .onFocusChanged { outlineColor = if (it.isFocused) primaryColor else Color.Gray }
            .border(width = 2.dp, color = outlineColor, shape = MaterialTheme.shapes.medium )
            .padding(textPadding),
        textStyle = MaterialTheme.typography.body1.merge(TextStyle(color = MaterialTheme.colors.onSurface)),
        cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchClick() })
    )
}
