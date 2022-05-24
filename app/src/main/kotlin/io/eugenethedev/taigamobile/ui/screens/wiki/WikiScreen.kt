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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Attachment
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithSearch
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.dialogs.EmptyWikiDialog
import io.eugenethedev.taigamobile.ui.components.editors.TaskEditor
import io.eugenethedev.taigamobile.ui.components.lists.UserItem
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.screens.commontask.EditAttachmentsAction
import io.eugenethedev.taigamobile.ui.screens.commontask.components.CommonTaskAttachments
import io.eugenethedev.taigamobile.ui.screens.commontask.components.CommonTaskDescription
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.screens.wiki.components.AdvancedSpacer
import io.eugenethedev.taigamobile.ui.theme.dialogTonalElevation
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.LoadingResult
import io.eugenethedev.taigamobile.ui.utils.navigateToProfileScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
import io.eugenethedev.taigamobile.ui.utils.surfaceColorAtElevation
import java.time.LocalDateTime

@Composable
fun WikiScreen(
    navController: NavController,
    showMessage: (message: Int) -> Unit = {},
) {
    val viewModel: WikiViewModel = viewModel()

    val currentPage by viewModel.currentWikiPage.collectAsState()
    val currentLink by viewModel.currentWikiLink.collectAsState()
    val lastModifierUser by viewModel.lastModifierUser.collectAsState()

    val onOpenResult by viewModel.loadDataResult.collectAsState()
    onOpenResult.subscribeOnError(showMessage)

    val createWikiPageResult by viewModel.createWikiPageResult.collectAsState()
    createWikiPageResult.subscribeOnError(showMessage)

    val editWikiPageResult by viewModel.editWikiPageResult.collectAsState()
    editWikiPageResult.subscribeOnError(showMessage)

    val deleteWikiPageResult by viewModel.deleteWikiPageResult.collectAsState()
    deleteWikiPageResult.subscribeOnError(showMessage)

    val attachments by viewModel.attachments.collectAsState()
    attachments.subscribeOnError(showMessage)

    val isLoading = onOpenResult is LoadingResult || createWikiPageResult is LoadingResult ||
        editWikiPageResult is LoadingResult || deleteWikiPageResult is LoadingResult || attachments is LoadingResult

    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }
        else -> {
            WikiContentScreen(
                pageName = currentLink?.title ?: "",
                content = currentPage?.content ?: "",
                lastModifierUser = lastModifierUser,
                lastModifierDate = currentPage?.modifiedDate ?: LocalDateTime.now(),
                attachments = attachments.data.orEmpty(),
                editAttachmentsAction = EditAttachmentsAction(
                    deleteAttachment = viewModel::deletePageAttachment,
                    addAttachment = viewModel::addPageAttachment,
                    isResultLoading = attachments is LoadingResult
                ),
                navigateBack = navController::popBackStack,
                deleteWikiPage = viewModel::deleteWikiPage,
                editWikiPage = viewModel::editWikiPage,
                createWikiPage = viewModel::createWikiPage,
                onTitleClick = {
                    navController.navigate(Routes.projectsSelector)
                },
                onUserItemClick = { userId ->
                    navController.navigateToProfileScreen(userId)
                }
            )
        }
    }
}

@Composable
fun WikiContentScreen(
    pageName: String,
    content: String,
    lastModifierUser: User?,
    lastModifierDate: LocalDateTime,
    attachments: List<Attachment> = emptyList(),
    editAttachmentsAction: EditAttachmentsAction = EditAttachmentsAction(),
    navigateBack: () -> Unit = {},
    deleteWikiPage: () -> Unit = {},
    editWikiPage: (content: String) -> Unit = { _ -> },
    createWikiPage: (title: String, content: String) -> Unit = { _, _ -> },
    onTitleClick: () -> Unit = {},
    onUserItemClick: (userId: Long) -> Unit = { _ -> }
) = Box(Modifier.fillMaxSize()) {
    var isDescriptionEditorVisible by remember { mutableStateOf(false) }
    var isCreatingNewTask by remember { mutableStateOf(false) }
    val sectionsPadding = 24.dp

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        WikiAppBar(
            pageName = pageName,
            onTitleClick = onTitleClick,
            navigateBack = navigateBack,
            deleteWikiPage = deleteWikiPage,
            showDescriptionEditor = { isDescriptionEditorVisible = true },
            showCreatorNewPage = {
                isDescriptionEditorVisible = true
                isCreatingNewTask = true
            },
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mainHorizontalScreenPadding)
        ) {

            // description
            CommonTaskDescription(content)

            AdvancedSpacer(sectionsPadding)

            // last modification
            item {
                Text(
                    text = stringResource(R.string.last_modification),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                if (lastModifierUser != null) {
                    UserItem(
                        user = lastModifierUser,
                        dateTime = lastModifierDate,
                        onUserItemClick = {
                            onUserItemClick(lastModifierUser.id)
                        }
                    )
                }
            }

            AdvancedSpacer(sectionsPadding)

            CommonTaskAttachments(
                attachments = attachments,
                editAttachmentsAction = editAttachmentsAction
            )

            AdvancedSpacer(sectionsPadding)
        }
    }

    if (lastModifierUser == null) {
        EmptyWikiDialog(
            createNewPage = {
                isDescriptionEditorVisible = true
                isCreatingNewTask = true
            },
            navigateBack = navigateBack
        )
    }

    if (isDescriptionEditorVisible) {
        val toolbarText: String
        val title: String
        val description: String
        val showTitle: Boolean

        if (isCreatingNewTask) {
            toolbarText = stringResource(R.string.create_new_page)
            title = ""
            description = ""
            showTitle = true
        } else {
            toolbarText = stringResource(R.string.edit)
            title = pageName
            description = content
            showTitle = false
        }

        TaskEditor(
            toolbarText = toolbarText,
            title = title,
            description = description,
            showTitle = showTitle,
            onSaveClick = { title, description ->
                if (isCreatingNewTask)
                    createWikiPage(title, description)
                else
                    editWikiPage(description)

                isDescriptionEditorVisible = false
                isCreatingNewTask = false
            },
            navigateBack = {
                isDescriptionEditorVisible = false
                isCreatingNewTask = false
            }
        )
    }
}

@Composable
fun WikiAppBar(
    pageName: String,
    onTitleClick: () -> Unit,
    showDescriptionEditor: () -> Unit,
    showCreatorNewPage: () -> Unit,
    navigateBack: () -> Unit,
    deleteWikiPage: () -> Unit
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
                        deleteWikiPage()
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

                //Create new page
                DropdownMenuItem(
                    onClick = {
                        isMenuExpanded = false
                        showCreatorNewPage()
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.create_new_page),
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
            }
        },
        onTitleClick = onTitleClick,
        navigateBack = navigateBack
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun WikiScreenPreview() {
    val creator = User(
        _id = 0,
        fullName = "Some cool fullname",
        photo = null,
        bigPhoto = null,
        username = "Some cool username"
    )

    WikiContentScreen(
        pageName = "Some page",
        content = "* Content *",
        lastModifierUser = creator,
        lastModifierDate = LocalDateTime.now()
    )
}

