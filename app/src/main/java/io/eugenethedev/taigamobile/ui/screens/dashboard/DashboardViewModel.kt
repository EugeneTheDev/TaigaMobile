package io.eugenethedev.taigamobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DashboardViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState

    val workingOn = MutableLiveResult<List<CommonTask>?>()
    val watching = MutableLiveResult<List<CommonTask>?>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() = viewModelScope.launch {
        if (screensState.shouldReloadDashboardScreen) {
            reset()
        }

        if (workingOn.value == null || watching.value == null) {
            joinAll(
                launch { loadWorkingOn() },
                launch { loadWatching() }
            )
        }
    }

    fun changeCurrentProject(commonTask: CommonTask) {
        commonTask.projectInfo.apply {
            session.currentProjectName = name
            session.currentProjectId = id
        }
    }

    private suspend fun loadWorkingOn() {
        workingOn.value = Result(ResultStatus.Loading)

        workingOn.value = try {
            Result(ResultStatus.Success, tasksRepository.getWorkingOn())
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    private suspend fun loadWatching() {
        watching.value = Result(ResultStatus.Loading)

        watching.value = try {
            Result(ResultStatus.Success, tasksRepository.getWatching())
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    fun reset() {
        workingOn.value = null
        watching.value = null
    }
}