package io.eugenethedev.taigamobile.ui.screens.wiki.page

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.imePadding
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Attachment
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.editors.TaskEditor
import io.eugenethedev.taigamobile.ui.components.lists.UserItem
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.screens.commontask.EditAction
import io.eugenethedev.taigamobile.ui.screens.commontask.components.CommonTaskAttachments
import io.eugenethedev.taigamobile.ui.screens.commontask.components.CommonTaskDescription
import io.eugenethedev.taigamobile.ui.screens.wiki.AdvancedSpacer
import io.eugenethedev.taigamobile.ui.theme.dialogTonalElevation
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.LoadingResult
import io.eugenethedev.taigamobile.ui.utils.navigateToProfileScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
import io.eugenethedev.taigamobile.ui.utils.surfaceColorAtElevation
import java.io.InputStream
import java.time.LocalDateTime

@Composable
fun WikiPageScreen(
    slug: String,
    navController: NavController,
    showMessage: (message: Int) -> Unit = {},
) {
    val viewModel: WikiPageViewModel = viewModel()

    val page by viewModel.pageResult.collectAsState()
    page.subscribeOnError(showMessage)

    val link by viewModel.linkResult.collectAsState()
    link.subscribeOnError(showMessage)

    val editWikiPageResult by viewModel.editWikiPageResult.collectAsState()
    editWikiPageResult.subscribeOnError(showMessage)

    val deleteWikiPageResult by viewModel.deleteWikiPageResult.collectAsState()
    deleteWikiPageResult.subscribeOnError(showMessage)

    val attachments by viewModel.attachments.collectAsState()
    attachments.subscribeOnError(showMessage)

    val lastModifierUser by viewModel.lastModifierUser.collectAsState()

    val isLoading = page is LoadingResult || link is LoadingResult ||
        editWikiPageResult is LoadingResult || deleteWikiPageResult is LoadingResult ||
        attachments is LoadingResult

    LaunchedEffect(Unit) {
        viewModel.onOpen(slug)
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularLoader()
        }
    } else {
        WikiPageScreenContent(
            pageName = link.data?.title ?: slug,
            content = page.data?.content ?: "",
            lastModifierUser = lastModifierUser,
            lastModifierDate = page.data?.modifiedDate ?: LocalDateTime.now(),
            attachments = attachments.data.orEmpty(),
            navigateBack = { navController.popBackStack() },
            editWikiPage = viewModel::editWikiPage,
            deleteWikiPage = {
                viewModel.deleteWikiPage()
                navController.popBackStack()
            },
            onUserItemClick = { userId ->
                navController.navigateToProfileScreen(userId)
            },
            editAttachments = EditAction(
                select = { (file, stream) -> viewModel.addPageAttachment(file, stream) },
                remove = viewModel::deletePageAttachment,
                isLoading = attachments is LoadingResult
            ),
        )
    }
}

@Composable
fun WikiPageScreenContent(
    pageName: String,
    content: String,
    lastModifierUser: User?,
    lastModifierDate: LocalDateTime,
    attachments: List<Attachment> = emptyList(),
    navigateBack: () -> Unit = {},
    editWikiPage: (content: String) -> Unit = { _ -> },
    deleteWikiPage: () -> Unit = {},
    onUserItemClick: (userId: Long) -> Unit = { _ -> },
    editAttachments: EditAction<Pair<String, InputStream>, Attachment> = EditAction(),
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    val sectionsPadding = 24.dp
    var isEditPageVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .imePadding()
    ) {

        WikiAppBar(
            pageName = pageName,
            navigateBack = navigateBack,
            editWikiPage = { isEditPageVisible = true },
            deleteWikiPage = deleteWikiPage,
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
                editAttachments = editAttachments
            )

            AdvancedSpacer(sectionsPadding)
        }

    }
    if (isEditPageVisible) {
        TaskEditor(
            toolbarText = stringResource(R.string.edit),
            title = pageName,
            description = content,
            showTitle = false,
            onSaveClick = { title, description ->
                editWikiPage(description)
                isEditPageVisible = false
            },
            navigateBack = {
                isEditPageVisible = false
            }
        )
    }
}

@Composable
fun WikiAppBar(
    pageName: String = "",
    editWikiPage: () -> Unit = {},
    deleteWikiPage: () -> Unit = {},
    navigateBack: () -> Unit = {},
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    AppBarWithBackButton(
        title = {
            Text(
                text = pageName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
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

                //Edit
                DropdownMenuItem(
                    onClick = {
                        isMenuExpanded = false
                        editWikiPage()
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
        navigateBack = navigateBack
    )
}

@Preview
@Composable
fun WikiPagePreview() {
    val creator = User(
        _id = 0,
        fullName = "Some cool fullname",
        photo = null,
        bigPhoto = null,
        username = "Some cool username"
    )

    WikiPageScreenContent(
        pageName = "Some page",
        content = "* Content *",
        lastModifierUser = creator,
        lastModifierDate = LocalDateTime.now()
    )
}