package io.eugenethedev.taigamobile.ui.screens.epics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class EpicsViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var tasksRepository: ITasksRepository

    val projectName get() = session.currentProjectName
    val epics = MutableLiveResult<List<CommonTask>>()
    
    private var currentEpicPage = 0
    private var maxEpicPage = Int.MAX_VALUE
    
    init {
        TaigaApp.appComponent.inject(this)
    }
    
    fun start() {
        if (screensState.shouldReloadEpicsScreen) {
            reset()
        }

        if (epics.value == null) {
            loadEpics()
        }
    }

    fun loadEpics() = viewModelScope.launch {
        if (currentEpicPage == maxEpicPage) return@launch

        epics.value = Result(ResultStatus.Loading, epics.value?.data)

        try {
            tasksRepository.getEpics(++currentEpicPage)
                .also { epics.value = Result(ResultStatus.Success, epics.value?.data.orEmpty() + it) }
                .takeIf { it.isEmpty() }
                ?.run { maxEpicPage = currentEpicPage }
        } catch (e: Exception) {
            Timber.w(e)
            epics.value = Result(ResultStatus.Error, epics.value?.data, message = R.string.common_error_message)
        }
    }
    
    fun reset() {
        epics.value = null
        currentEpicPage = 0
        maxEpicPage = Int.MAX_VALUE
    }
}
