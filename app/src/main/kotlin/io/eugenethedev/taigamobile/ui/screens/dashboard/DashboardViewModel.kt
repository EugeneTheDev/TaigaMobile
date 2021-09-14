package io.eugenethedev.taigamobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.MutableResultFlow
import io.eugenethedev.taigamobile.ui.commons.NothingResult
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState

    val workingOn = MutableResultFlow<List<CommonTask>>()
    val watching = MutableResultFlow<List<CommonTask>>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() = viewModelScope.launch {
        if (screensState.shouldReloadDashboardScreen) {
            reset()
        }

        if (listOf(workingOn, watching).any { it.value is NothingResult }) {
            joinAll(
                launch { workingOn.loadOrError(preserveValue = false) { tasksRepository.getWorkingOn() } },
                launch { watching.loadOrError(preserveValue = false) { tasksRepository.getWatching() } }
            )
        }
    }

    fun changeCurrentProject(commonTask: CommonTask) {
        commonTask.projectInfo.apply {
            session.currentProjectName = name
            session.currentProjectId = id
        }
    }

    fun reset() {
        workingOn.value = NothingResult()
        watching.value = NothingResult()
    }
}
