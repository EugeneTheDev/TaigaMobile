package io.eugenethedev.taigamobile.ui.screens.wiki.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.components.buttons.TextButton
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding

@Composable
fun EmptyWikiDialog(
    createNewPage: () -> Unit = {},
    isButtonAvailable: Boolean = true
) = Box (
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surface)
        .imePadding()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = mainHorizontalScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.empty_wiki_dialog_title),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.empty_wiki_dialog_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isButtonAvailable) {
            TextButton(
                text = stringResource(R.string.create_new_page),
                onClick = createNewPage
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyWikiDialogPreview() {
    EmptyWikiDialog()
}