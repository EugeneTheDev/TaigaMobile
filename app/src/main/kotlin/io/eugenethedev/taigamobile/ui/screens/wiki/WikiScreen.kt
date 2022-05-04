package io.eugenethedev.taigamobile.ui.screens.wiki

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithSearch
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.editors.TaskEditor
import io.eugenethedev.taigamobile.ui.components.lists.UserItem
import io.eugenethedev.taigamobile.ui.screens.commontask.components.CommonTaskDescription
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.screens.wiki.components.AdvancedSpacer
import io.eugenethedev.taigamobile.ui.theme.dialogTonalElevation
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.surfaceColorAtElevation
import java.time.LocalDateTime

@Composable
fun WikiScreen(
    navController: NavController,
    showMessage: (message: Int) -> Unit = {},
) {
    val viewModel: WikiViewModel = viewModel()

    WikiContentScreen(
        pageName = "Some page",
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        navigateBack = navController::popBackStack
    )
}

@Composable
fun WikiContentScreen(
    pageName: String,
    onTitleClick: () -> Unit = {},
    navigateBack: () -> Unit = {},
) = Box(Modifier.fillMaxSize()) {
    var isDescriptionEditorVisible by remember { mutableStateOf(false) }
    val sectionsPadding = 16.dp

    // TODO Test data
    val title = "Some title"
    val description = "`some descr`"
    val creator = User(
        _id = 0,
        fullName = "Some cool fullname",
        photo = null,
        bigPhoto = null,
        username = "Some cool username"
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        WikiAppBar(
            pageName = pageName,
            onTitleClick = onTitleClick,
            showDescriptionEditor = { isDescriptionEditorVisible = true },
            navigateBack = navigateBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mainHorizontalScreenPadding)
        ) {
            // title
            item {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            AdvancedSpacer(sectionsPadding)

            // description
            CommonTaskDescription(description)

            AdvancedSpacer(sectionsPadding)

            // last modification
            item {
                Text(
                    text = stringResource(R.string.last_modification),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                UserItem(
                    user = creator,
                    dateTime = LocalDateTime.now(),
                    onUserItemClick = {
                        //TODO Move to profile screen
                    }
                )
            }

            AdvancedSpacer(sectionsPadding)

            // TODO Add attachments
            // CommonTaskAttachments(
            //     attachments = attachments,
            //     editActions = editActions
            // )
        }
    }

    if (isDescriptionEditorVisible) {
        TaskEditor(
            toolbarText = stringResource(R.string.edit),
            title = title,
            description = description,
            onSaveClick = { title, description ->
                isDescriptionEditorVisible = false
                //TODO edit action
            },
            navigateBack = { isDescriptionEditorVisible = false }
        )
    }
}

@Composable
fun WikiAppBar(
    pageName: String,
    onTitleClick: () -> Unit,
    showDescriptionEditor: () -> Unit,
    navigateBack: () -> Unit
) {

    var isMenuExpanded by remember { mutableStateOf(false) }
    AppBarWithSearch(
        projectName = pageName,
        actions = {
            IconButton(onClick = { isMenuExpanded = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_options),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            var isDeleteAlertVisible by remember { mutableStateOf(false) }
            if (isDeleteAlertVisible) {
                ConfirmActionDialog(
                    title = stringResource(R.string.delete_wiki_title),
                    text = stringResource(R.string.delete_wiki_text),
                    onConfirm = {
                        isDeleteAlertVisible = false
                        //TODO delete action
                    },
                    onDismiss = { isDeleteAlertVisible = false },
                    iconId = R.drawable.ic_delete
                )
            }
            DropdownMenu(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(dialogTonalElevation)
                ),
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                // Delete
                DropdownMenuItem(
                    onClick = {
                        isMenuExpanded = false
                        isDeleteAlertVisible = true
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.delete),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )

                //Edit
                DropdownMenuItem(
                    onClick = {
                        isMenuExpanded = false
                        showDescriptionEditor()
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.edit),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        },
        onTitleClick = onTitleClick,
        navigateBack = navigateBack
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun WikiScreenPreview() {
    WikiContentScreen(
        pageName = "Some page"
    )
}

