package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsWithImePadding
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding

private val elevation = 1.dp

@ExperimentalComposeUiApi
@Composable
fun CreateCommentBar(
    createComment: (String) -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .fillMaxWidth()
        .background(
            LocalElevationOverlay.current?.apply(
                color = MaterialTheme.colors.surface,
                elevation = elevation * 2
            ) ?: MaterialTheme.colors.surface
        )
        .shadow(if (!isSystemInDarkTheme()) elevation else 0.dp)
        .padding(vertical = 2.dp, horizontal = mainHorizontalScreenPadding)
        .navigationBarsWithImePadding()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var commentTextValue by remember { mutableStateOf(TextFieldValue()) }

    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.CenterStart
    ) {
        if (commentTextValue.text.isEmpty()) {
            Text(
                text = stringResource(R.string.comment_hint),
                style = MaterialTheme.typography.body1,
                color = Color.Gray
            )
        }
        BasicTextField(
            value = commentTextValue,
            onValueChange = { commentTextValue = it },
            maxLines = 3,
            textStyle = MaterialTheme.typography.body1.merge(TextStyle(color = MaterialTheme.colors.onSurface)),
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
            modifier = Modifier.fillMaxWidth()
        )

    }

    IconButton(
        onClick = {
            commentTextValue.text.trim().takeIf { it.isNotEmpty() }?.let {
                createComment(it)
                commentTextValue = TextFieldValue()
                keyboardController?.hideSoftwareKeyboard()
            }
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_send),
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}