package io.eugenethedev.taigamobile.ui.screens.wiki

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.domain.entities.WikiLink
import io.eugenethedev.taigamobile.domain.entities.WikiPage
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

    private val wikiPages = MutableStateFlow<List<WikiPage>>(emptyList())
    var currentWikiLink = MutableStateFlow<WikiLink?>(null)
    var currentWikiPage = MutableStateFlow<WikiPage?>(null)
    val onOpenResult = MutableResultFlow<Unit>()

    init {
        appComponent.inject(this)
    }

    fun onOpen() = viewModelScope.launch {
        onOpenResult.loadOrError {
            wikiPages.value = wikiRepository.getProjectWikiPages()

            currentWikiPage.value = wikiPages.value.firstOrNull()

            currentWikiPage.value?.id?.let {
                currentWikiLink.value = wikiRepository.getWikiLink(it)
            }
        }
    }

}