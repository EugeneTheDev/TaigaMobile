package io.eugenethedev.taigamobile.ui.screens.epics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.*
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class EpicsViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var tasksRepository: ITasksRepository

    val projectName get() = session.currentProjectName
    val epics = MutableResultFlow<List<CommonTask>>()
    
    private var currentEpicPage = 0
    private var maxEpicPage = Int.MAX_VALUE
    
    init {
        TaigaApp.appComponent.inject(this)
    }
    
    fun start() {
        if (screensState.shouldReloadEpicsScreen) {
            reset()
        }

        if (epics.value is NothingResult) {
            loadEpics()
        }
    }

    fun loadEpics() = viewModelScope.launch {
        if (currentEpicPage == maxEpicPage) return@launch

        epics.loadOrError {
            tasksRepository.getEpics(++currentEpicPage).also {
                if (it.isEmpty()) maxEpicPage = currentEpicPage
            }.let {
                epics.value.data.orEmpty() + it
            }
        }
    }
    
    fun reset() {
        epics.value = NothingResult()
        currentEpicPage = 0
        maxEpicPage = Int.MAX_VALUE
    }
}
