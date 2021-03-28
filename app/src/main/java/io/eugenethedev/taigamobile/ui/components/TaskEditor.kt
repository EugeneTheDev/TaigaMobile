package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.onBackPressed

@Composable
fun TaskEditor(
    toolbarText: String,
    title: String = "",
    description: String = "",
    onSaveClick: (title: String, description: String) -> Unit = { _, _ -> },
    navigateBack: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface)
) {
    onBackPressed(navigateBack)

    var titleInput by remember { mutableStateOf(TextFieldValue(title)) }
    var descriptionInput by remember { mutableStateOf(TextFieldValue(description)) }

    AppBarWithBackButton(
        title = { Text(toolbarText) },
        actions = {
            IconButton(
                onClick = {
                    val title = titleInput.text.trim().takeIf { it.isNotEmpty() } ?: return@IconButton
                    val description = descriptionInput.text.trim().takeIf { it.isNotEmpty() } ?: return@IconButton
                    onSaveClick(title, description)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_save),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            }
        },
        navigateBack = navigateBack
    )

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = mainHorizontalScreenPadding)
    ) {

        Spacer(Modifier.height(8.dp))

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (titleInput.text.isEmpty()) {
                Text(
                    text = stringResource(R.string.title_hint),
                    style = MaterialTheme.typography.h5,
                    color = Color.Gray
                )
            }

            BasicTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.h5.merge(TextStyle(color = MaterialTheme.colors.onSurface)),
                cursorBrush = SolidColor(MaterialTheme.colors.onSurface)
            )
        }

        Spacer(Modifier.height(16.dp))

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (descriptionInput.text.isEmpty()) {
                Text(
                    text = stringResource(R.string.description_hint),
                    style = MaterialTheme.typography.body1,
                    color = Color.Gray
                )
            }

            BasicTextField(
                value = descriptionInput,
                onValueChange = { descriptionInput = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.body1.merge(TextStyle(color = MaterialTheme.colors.onSurface)),
                cursorBrush = SolidColor(MaterialTheme.colors.onSurface)
            )
        }

        Spacer(Modifier.height(8.dp))
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TaskEditorPreview() = TaigaMobileTheme {
    TaskEditor("Edit")
}