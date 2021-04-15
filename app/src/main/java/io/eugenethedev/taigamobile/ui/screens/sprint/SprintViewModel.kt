package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Settings
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.commons.StoriesViewModel
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SprintViewModel : StoriesViewModel() {
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var settings: Settings

    val tasks = MutableLiveResult<List<CommonTask>>()

    val startStatusesExpanded get() = settings.isSprintScreenExpandStatuses

    var currentTasksPage = 0
    var maxTasksPage = Int.MAX_VALUE

    val issues = MutableLiveResult<List<CommonTask>>()

    private var currentIssuesPage = 0
    private var maxIssuesPage = Int.MAX_VALUE

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start(sprintId: Long) {
        if (screensState.shouldReloadSprintScreen) {
            reset()
        }

        this.sprintId = sprintId

        if (statuses.value == null) {
            viewModelScope.launch { loadStatuses() }
            loadTasks()
            loadIssues()
        }
    }

    fun loadTasks() = viewModelScope.launch {
        if (currentTasksPage == maxTasksPage) return@launch

        tasks.value = Result(ResultStatus.LOADING, tasks.value?.data)

        try {
            tasksRepository.getSprintTasks(sprintId!!, ++currentTasksPage)
                .also { tasks.value = Result(ResultStatus.SUCCESS, tasks.value?.data.orEmpty() + it) }
                .takeIf { it.isEmpty() }
                ?.run { maxTasksPage = currentTasksPage }
        } catch (e: Exception) {
            Timber.w(e)
            tasks.value = Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }
    }

    fun loadIssues() = viewModelScope.launch {
        if (currentIssuesPage == maxIssuesPage) return@launch

        issues.value = Result(ResultStatus.LOADING, issues.value?.data)

        try {
            tasksRepository.getSprintIssues(sprintId!!, ++currentIssuesPage)
                .also { issues.value = Result(ResultStatus.SUCCESS, issues.value?.data.orEmpty() + it) }
                .takeIf { it.isEmpty() }
                ?.run { maxIssuesPage = currentIssuesPage }
        } catch (e: Exception) {
            Timber.w(e)
            issues.value = Result(ResultStatus.ERROR, issues.value?.data, message = R.string.common_error_message)
        }
    }

    override fun reset() {
        super.reset()
        tasks.value = null
        issues.value = null
        currentTasksPage = 0
        maxTasksPage = Int.MAX_VALUE
        currentIssuesPage = 0
        maxIssuesPage = Int.MAX_VALUE
    }
}