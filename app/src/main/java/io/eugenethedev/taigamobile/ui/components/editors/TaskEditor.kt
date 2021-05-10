package io.eugenethedev.taigamobile.ui.components.editors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import com.google.accompanist.insets.navigationBarsHeight
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
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
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.surface)
        .imePadding()
) {
    onBackPressed(navigateBack)

    var titleInput by remember { mutableStateOf(TextFieldValue(title)) }
    var descriptionInput by remember { mutableStateOf(TextFieldValue(description)) }

    AppBarWithBackButton(
        title = { Text(toolbarText) },
        actions = {
            IconButton(
                onClick = {
                    titleInput.text.trim().takeIf { it.isNotEmpty() }?.let {
                        onSaveClick(it, descriptionInput.text.trim())
                    }
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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = mainHorizontalScreenPadding)
    ) {

        Spacer(Modifier.height(8.dp))

        TextFieldWithHint(
            hintId = R.string.title_hint,
            value = titleInput,
            onValueChange = { titleInput = it },
            style = MaterialTheme.typography.h5
        )

        Spacer(Modifier.height(16.dp))

        TextFieldWithHint(
            hintId = R.string.description_hint,
            value = descriptionInput,
            onValueChange = { descriptionInput = it },
        )

        Spacer(Modifier.navigationBarsHeight(8.dp))
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TaskEditorPreview() = TaigaMobileTheme {
    TaskEditor("Edit")
}
