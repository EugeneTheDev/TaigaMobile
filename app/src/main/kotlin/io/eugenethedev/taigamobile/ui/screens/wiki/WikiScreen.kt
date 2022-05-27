package io.eugenethedev.taigamobile.ui.screens.wiki

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Attachment
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.entities.WikiLink
import io.eugenethedev.taigamobile.domain.entities.WikiPage
import io.eugenethedev.taigamobile.ui.components.dialogs.EmptyWikiDialog
import io.eugenethedev.taigamobile.ui.components.editors.TaskEditor
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.screens.commontask.EditAction
import io.eugenethedev.taigamobile.ui.screens.wiki.components.WikiPage
import io.eugenethedev.taigamobile.ui.screens.wiki.components.WikiPageSelector
import io.eugenethedev.taigamobile.ui.utils.LoadingResult
import io.eugenethedev.taigamobile.ui.utils.navigateToProfileScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
import java.io.InputStream
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

    val wikiLinks by viewModel.wikiLinks.collectAsState()
    wikiLinks.subscribeOnError(showMessage)

    val wikiPages by viewModel.wikiPages.collectAsState()
    wikiPages.subscribeOnError(showMessage)

    val createWikiPageResult by viewModel.createWikiPageResult.collectAsState()
    createWikiPageResult.subscribeOnError(showMessage)

    val editWikiPageResult by viewModel.editWikiPageResult.collectAsState()
    editWikiPageResult.subscribeOnError(showMessage)

    val deleteWikiPageResult by viewModel.deleteWikiPageResult.collectAsState()
    deleteWikiPageResult.subscribeOnError(showMessage)

    val attachments by viewModel.attachments.collectAsState()
    attachments.subscribeOnError(showMessage)

    val currentWikiState by viewModel.currentWikiState.collectAsState()

    val isLoading = onOpenResult is LoadingResult || createWikiPageResult is LoadingResult ||
        editWikiPageResult is LoadingResult || deleteWikiPageResult is LoadingResult ||
        attachments is LoadingResult

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
                currentPageSlug = currentPage?.slug ?: currentLink?.ref ?: "",
                currentWikiState = currentWikiState,
                lastModifierUser = lastModifierUser,
                lastModifierDate = currentPage?.modifiedDate ?: LocalDateTime.now(),
                attachments = attachments.data.orEmpty(),
                links = wikiLinks.data.orEmpty(),
                pages = wikiPages.data.orEmpty(),
                editAttachments = EditAction(
                    select = { (file, stream) -> viewModel.addPageAttachment(file, stream) },
                    remove = viewModel::deletePageAttachment,
                    isLoading = attachments is LoadingResult
                ),
                selectPage = viewModel::selectPage,
                navigateBack = navController::popBackStack,
                deleteWikiPage = viewModel::deleteWikiPage,
                editWikiPage = viewModel::editWikiPage,
                createWikiPage = viewModel::createWikiPage,
                onUserItemClick = { userId ->
                    navController.navigateToProfileScreen(userId)
                },
                setCurrentWikiState = viewModel::setCurrentWikiState
            )
        }
    }
}

@Composable
fun WikiContentScreen(
    pageName: String,
    content: String,
    currentPageSlug: String,
    currentWikiState: WikiState,
    lastModifierUser: User?,
    lastModifierDate: LocalDateTime,
    attachments: List<Attachment> = emptyList(),
    links: List<WikiLink> = emptyList(),
    pages: List<WikiPage> = emptyList(),
    editAttachments: EditAction<Pair<String, InputStream>, Attachment> = EditAction(),
    selectPage: (content: String, isBySlug: Boolean) -> Unit = { _, _ -> },
    navigateBack: () -> Unit = {},
    deleteWikiPage: () -> Unit = {},
    editWikiPage: (content: String) -> Unit = { _ -> },
    createWikiPage: (title: String, content: String) -> Unit = { _, _ -> },
    onUserItemClick: (userId: Long) -> Unit = { _ -> },
    setCurrentWikiState: (state: WikiState) -> Unit = { _ -> }
) = Box(Modifier.fillMaxSize()) {
    var wikiState by remember { mutableStateOf(currentWikiState) }

    LaunchedEffect(currentWikiState) {
        wikiState = if (links.isEmpty() && pages.isEmpty() && currentWikiState != WikiState.CREATE_NEW_PAGE_VISIBLE)
            WikiState.EMPTY_DIALOG_VISIBLE
        else
            currentWikiState
    }

    when (wikiState) {
        WikiState.PAGE_VISIBLE -> {
            WikiPage(
                pageName = pageName,
                content = content,
                lastModifierUser = lastModifierUser,
                lastModifierDate = lastModifierDate,
                attachments = attachments,
                navigateBack = {
                    setCurrentWikiState(WikiState.PAGE_SELECTOR_VISIBLE)
                },
                editWikiPage = {
                    setCurrentWikiState(WikiState.EDITOR_VISIBLE)
                },
                deleteWikiPage = {
                    deleteWikiPage()
                    setCurrentWikiState(
                        if (links.isEmpty() && pages.isEmpty())
                            WikiState.EMPTY_DIALOG_VISIBLE
                        else
                            WikiState.PAGE_SELECTOR_VISIBLE
                    )
                },
                onUserItemClick = onUserItemClick,
                editAttachments = editAttachments
            )
        }
        WikiState.EMPTY_DIALOG_VISIBLE -> {
            EmptyWikiDialog(
                createNewPage = {
                    setCurrentWikiState(WikiState.CREATE_NEW_PAGE_VISIBLE)
                },
                navigateBack = navigateBack
            )
        }
        WikiState.PAGE_SELECTOR_VISIBLE -> {
            WikiPageSelector(
                links = links,
                pages = pages,
                currentPageTitle = pageName,
                currentPageSlug = currentPageSlug,
                selectPage = { content, isBySlug ->
                    selectPage(content, isBySlug)
                    setCurrentWikiState(WikiState.PAGE_VISIBLE)
                },
                createWikiPage = {
                    setCurrentWikiState(WikiState.CREATE_NEW_PAGE_VISIBLE)
                },
                navigateBack = navigateBack
            )
        }
        else -> {
            val toolbarText: String
            val title: String
            val description: String
            val showTitle: Boolean

            if (wikiState == WikiState.CREATE_NEW_PAGE_VISIBLE) {
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
                    if (wikiState == WikiState.CREATE_NEW_PAGE_VISIBLE)
                        createWikiPage(title, description)
                    else
                        editWikiPage(description)

                    setCurrentWikiState(WikiState.PAGE_VISIBLE)
                },
                navigateBack = {
                    setCurrentWikiState(WikiState.PAGE_SELECTOR_VISIBLE)
                }
            )
        }
    }
}

enum class WikiState {
    PAGE_VISIBLE,
    EDITOR_VISIBLE,
    PAGE_SELECTOR_VISIBLE,
    CREATE_NEW_PAGE_VISIBLE,
    EMPTY_DIALOG_VISIBLE
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
        currentPageSlug = "slug",
        currentWikiState = WikiState.PAGE_VISIBLE,
        lastModifierUser = creator,
        lastModifierDate = LocalDateTime.now()
    )
}

