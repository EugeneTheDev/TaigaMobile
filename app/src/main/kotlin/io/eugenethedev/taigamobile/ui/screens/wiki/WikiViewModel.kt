package io.eugenethedev.taigamobile.ui.screens.wiki

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.domain.entities.Attachment
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.entities.WikiLink
import io.eugenethedev.taigamobile.domain.entities.WikiPage
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.domain.repositories.IWikiRepository
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.ui.utils.MutableResultFlow
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

class WikiViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {

    @Inject
    lateinit var session: Session

    @Inject
    lateinit var wikiRepository: IWikiRepository

    @Inject
    lateinit var userRepository: IUsersRepository

    val wikiPages = MutableResultFlow<List<WikiPage>>()
    val wikiLinks = MutableResultFlow<List<WikiLink>>()

    var currentWikiLink = MutableStateFlow<WikiLink?>(null)
    var currentWikiPage = MutableStateFlow<WikiPage?>(null)
    var lastModifierUser = MutableStateFlow<User?>(null)

    val onOpenResult = MutableResultFlow<Unit>()
    val createWikiPageResult = MutableResultFlow<Unit>()
    val editWikiPageResult = MutableResultFlow<Unit>()
    val deleteWikiPageResult = MutableResultFlow<Unit>()

    val attachments = MutableResultFlow<List<Attachment>>()

    init {
        appComponent.inject(this)
    }

    fun onOpen() = viewModelScope.launch {
        onOpenResult.loadOrError {
            loadData().join()
        }
    }

    private fun loadData() = viewModelScope.launch {
        wikiPages.loadOrError {
            wikiRepository.getProjectWikiPages().also {

                currentWikiPage.value = it.firstOrNull()
                lastModifierUser.value =
                    if (currentWikiPage.value == null) null else userRepository.getUser(currentWikiPage.value?.lastModifier!!)

                val jobsToLoad = arrayOf(
                    launch {
                        wikiLinks.loadOrError(showLoading = false) {
                            val result = wikiRepository.getWikiLinks()
                            currentWikiLink.value = result.find { it.ref == currentWikiPage.value?.slug }
                            result
                        }
                    },
                    launch {
                        attachments.loadOrError(showLoading = false) {
                            currentWikiPage.value?.let { page -> wikiRepository.getPageAttachments(page.id) }
                        }
                    }
                )

                joinAll(*jobsToLoad)
            }
        }
    }

    fun deleteWikiPage() = viewModelScope.launch {
        deleteWikiPageResult.loadOrError {
            val linkId = currentWikiLink.value?.id
            val pageId = currentWikiPage.value?.id

            if (pageId != null) {
                wikiRepository.deleteWikiPage(pageId)
                loadData().join()
            }

            if (linkId != null) {
                wikiRepository.deleteWikiLink(linkId)
                loadData().join()
            }
        }
    }

    fun editWikiPage(content: String) = viewModelScope.launch {
        editWikiPageResult.loadOrError {
            currentWikiPage.value?.let {
                wikiRepository.editWikiPage(
                    pageId = it.id,
                    content = content,
                    version = it.version
                )

                loadData().join()
            }
        }
    }

    fun createWikiPage(title: String, content: String) = viewModelScope.launch {
        createWikiPageResult.loadOrError {
            val slug = title.replace(" ", "-").lowercase()

            wikiRepository.createWikiLink(
                href = slug,
                title = title
            )

            // Need it, because we can't put content to page
            // and create link for it at the same time :(
            val wikiPage = wikiRepository.getProjectWikiPageBySlug(slug)

            wikiRepository.editWikiPage(
                pageId = wikiPage.id,
                content = content,
                version = wikiPage.version
            )

            loadData().join()
        }
    }

    fun deletePageAttachment(attachment: Attachment) = viewModelScope.launch {
        attachments.loadOrError(R.string.permission_error) {
            wikiRepository.deletePageAttachment(
                attachmentId = attachment.id
            )

            loadData().join()
            attachments.value.data
        }
    }

    fun addPageAttachment(fileName: String, inputStream: InputStream) = viewModelScope.launch {
        attachments.loadOrError(R.string.permission_error) {
            currentWikiPage.value?.id?.let { pageId ->
                wikiRepository.addPageAttachment(
                    pageId = pageId,
                    fileName = fileName,
                    inputStream = inputStream
                )
                loadData().join()
            }
            attachments.value.data
        }
    }

    fun selectPage(content: String, isBySlug: Boolean) {
        if (isBySlug) {
            currentWikiLink.value = wikiLinks.value.data?.find { it.ref == content }
            currentWikiPage.value = wikiPages.value.data?.find { it.slug == content }
        } else {
            currentWikiLink.value = wikiLinks.value.data?.find { it.title == content }
            currentWikiPage.value = wikiPages.value.data?.find { it.slug == currentWikiLink.value?.ref }
        }
    }
}