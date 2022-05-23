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

    @Inject lateinit var session: Session
    @Inject lateinit var wikiRepository: IWikiRepository
    @Inject lateinit var userRepository: IUsersRepository

    private val wikiPages = MutableStateFlow<List<WikiPage>>(emptyList())
    private val wikiLinks = MutableStateFlow<List<WikiLink>>(emptyList())

    var currentWikiLink = MutableStateFlow<WikiLink?>(null)
    var currentWikiPage = MutableStateFlow<WikiPage?>(null)
    var lastModifierUser = MutableStateFlow<User?>(null)

    val onOpenResult = MutableResultFlow<Unit>()

    init {
        appComponent.inject(this)
    }

    fun onOpen() = viewModelScope.launch {
        onOpenResult.loadOrError {
            wikiPages.value = wikiRepository.getProjectWikiPages()
            currentWikiPage.value = wikiPages.value.firstOrNull()

            wikiLinks.value = wikiRepository.getWikiLink()
            currentWikiLink.value = wikiLinks.value.firstOrNull()

            lastModifierUser.value =
                if (currentWikiPage.value == null) null else userRepository.getUser(currentWikiPage.value?.lastModifier!!)
        }
    }

    fun deleteWikiPage() = viewModelScope.launch {
        val linkId = currentWikiLink.value?.id
        val pageId = currentWikiPage.value?.id

        if (linkId != null && pageId != null) {
            wikiRepository.deleteWikiLink(linkId)
            wikiRepository.deleteWikiPage(pageId)
            onOpen().join()
        }
    }

    fun editWikiPage(content: String) = viewModelScope.launch {
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