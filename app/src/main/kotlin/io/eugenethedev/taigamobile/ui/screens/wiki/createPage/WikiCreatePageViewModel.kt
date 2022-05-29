package io.eugenethedev.taigamobile.ui.screens.wiki.createPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.domain.entities.WikiPage
import io.eugenethedev.taigamobile.domain.repositories.IWikiRepository
import io.eugenethedev.taigamobile.ui.utils.MutableResultFlow
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class WikiCreatePageViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {

    @Inject
    lateinit var wikiRepository: IWikiRepository

    val creationResult = MutableResultFlow<WikiPage>()

    init {
        appComponent.inject(this)
    }

    fun createWikiPage(title: String, content: String) = viewModelScope.launch {
        creationResult.loadOrError {
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

            wikiPage
        }
    }
}