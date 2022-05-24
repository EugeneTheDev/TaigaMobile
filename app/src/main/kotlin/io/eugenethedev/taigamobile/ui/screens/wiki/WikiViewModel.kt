package io.eugenethedev.taigamobile.ui.screens.wiki

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.entities.WikiLink
import io.eugenethedev.taigamobile.domain.entities.WikiPage
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.domain.repositories.IWikiRepository
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.ui.utils.MutableResultFlow
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class WikiViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {

    @Inject
    lateinit var session: Session
    @Inject
    lateinit var wikiRepository: IWikiRepository

    @Inject
    lateinit var userRepository: IUsersRepository

    private val wikiPages = MutableStateFlow<List<WikiPage>>(emptyList())
    private val wikiLinks = MutableStateFlow<List<WikiLink>>(emptyList())

    var currentWikiLink = MutableStateFlow<WikiLink?>(null)
    var currentWikiPage = MutableStateFlow<WikiPage?>(null)
    var lastModifierUser = MutableStateFlow<User?>(null)

    val onOpenResult = MutableResultFlow<Unit>()
    val createWikiPageResult = MutableResultFlow<Unit>()
    val editWikiPageResult = MutableResultFlow<Unit>()
    val deleteWikiPageResult = MutableResultFlow<Unit>()

    init {
        appComponent.inject(this)
    }

    fun onOpen() = viewModelScope.launch {
        onOpenResult.loadOrError {
            wikiPages.value = wikiRepository.getProjectWikiPages()
            currentWikiPage.value = wikiPages.value.firstOrNull()

            wikiLinks.value = wikiRepository.getWikiLink()
            currentWikiLink.value = wikiLinks.value.find { it.ref == currentWikiPage.value?.slug }

            lastModifierUser.value =
                if (currentWikiPage.value == null) null else userRepository.getUser(currentWikiPage.value?.lastModifier!!)
        }
    }

    fun deleteWikiPage() = viewModelScope.launch {
        deleteWikiPageResult.loadOrError {
            val linkId = currentWikiLink.value?.id
            val pageId = currentWikiPage.value?.id

            if (pageId != null) {
                wikiRepository.deleteWikiPage(pageId)
                onOpen().join()
            }

            if (linkId != null) {
                wikiRepository.deleteWikiLink(linkId)
                onOpen().join()
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

                onOpen().join()
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

            onOpen().join()
        }
    }
}