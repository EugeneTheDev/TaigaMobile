package io.eugenethedev.taigamobile.ui.screens.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.repositories.IStoriesRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class StoriesViewModel : ViewModel() {

    @Inject lateinit var session: Session
    @Inject lateinit var storiesRepository: IStoriesRepository

    val projectName: String get() = session.currentProjectName

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun onScreenOpen() = viewModelScope.launch {
        session.currentProjectId.takeIf { it >= 0 }?.let {
            Timber.i(storiesRepository.getStatuses(it).toString())
            Timber.i(storiesRepository.getStories(session.currentProjectId, 1010, 1).toString())
        }
    }
}