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
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithSearch
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.editors.TaskEditor
import io.eugenethedev.taigamobile.ui.components.lists.UserItem
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
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

    val onOpenResult by viewModel.onOpenResult.collectAsState()
    onOpenResult.subscribeOnError(showMessage)

    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    when {
        onOpenResult is LoadingResult -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }
        else -> {
            lastModifierUser?.let {
                WikiContentScreen(
                    pageName = currentLink?.title ?: "Some title",
                    content = currentPage?.content ?: "",
                    lastModifierUser = it,
                    lastModifierDate = currentPage?.modifiedDate ?: LocalDateTime.now(),
                    navigateBack = navController::popBackStack,
                    deleteWikiPage = viewModel::deleteWikiPage,
                    editWikiPage = viewModel::editWikiPage,
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
}

@Composable
fun WikiContentScreen(
    pageName: String,
    content: String,
    lastModifierUser: User,
    lastModifierDate: LocalDateTime,
    navigateBack: () -> Unit = {},
    deleteWikiPage: () -> Unit = {},
    editWikiPage: (content: String) -> Unit = { _ -> },
    onTitleClick: () -> Unit = {},
    onUserItemClick: (userId: Long) -> Unit = { _ -> }
) = Box(Modifier.fillMaxSize()) {
    var isDescriptionEditorVisible by remember { mutableStateOf(false) }
    val sectionsPadding = 16.dp

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        WikiAppBar(
            pageName = pageName,
            onTitleClick = onTitleClick,
            showDescriptionEditor = { isDescriptionEditorVisible = true },
            navigateBack = navigateBack,
            deleteWikiPage = deleteWikiPage
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

                UserItem(
                    user = lastModifierUser,
                    dateTime = lastModifierDate,
                    onUserItemClick = {
                        onUserItemClick(lastModifierUser.id)
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
            title = pageName,
            description = content,
            showTitle = false,
            onSaveClick = { _, description ->
                isDescriptionEditorVisible = false
                editWikiPage(description)
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

