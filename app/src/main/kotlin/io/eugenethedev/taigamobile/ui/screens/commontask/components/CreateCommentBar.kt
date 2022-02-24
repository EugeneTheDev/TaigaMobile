package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsWithImePadding
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.theme.shapes

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateCommentBar(
    createComment: (String) -> Unit
) = Surface(
    modifier = Modifier.fillMaxWidth(),
    tonalElevation = 8.dp,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var commentTextValue by rememberSaveable { mutableStateOf(TextFieldValue()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = mainHorizontalScreenPadding)
            .navigationBarsWithImePadding(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = shapes.large
                )
                .clip(shapes.large)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            TextFieldWithHint(
                hintId = R.string.comment_hint,
                maxLines = 3,
                value = commentTextValue,
                onValueChange = { commentTextValue = it }
            )
        }

        CompositionLocalProvider(
            LocalMinimumTouchTargetEnforcement provides false
        ) {
            IconButton(
                onClick = {
                    commentTextValue.text.trim().takeIf { it.isNotEmpty() }?.let {
                        createComment(it)
                        commentTextValue = TextFieldValue()
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier.size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_send),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
