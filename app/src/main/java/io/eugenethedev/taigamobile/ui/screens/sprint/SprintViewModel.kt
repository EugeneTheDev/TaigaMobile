package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SprintViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var screensState: ScreensState

    private var sprintId: Long? = null

    val statuses = MutableLiveResult<List<Status>>()
    val storiesWithTasks = MutableLiveResult<Map<CommonTask, List<CommonTask>>>()
    val storylessTasks = MutableLiveResult<List<CommonTask>>()
    val issues = MutableLiveResult<List<CommonTask>>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start(sprintId: Long) {
        if (screensState.shouldReloadSprintScreen) {
            reset()
        }

        this@SprintViewModel.sprintId = sprintId

        if (statuses.value == null) {
            loadStatuses()
            loadStoriesWithTasks()
            loadStorylessTasks()
            loadIssues()
        }
    }

    private fun loadStatuses() = viewModelScope.launch {
        statuses.value = Result(ResultStatus.Loading)

        statuses.value = try {
            Result(ResultStatus.Success, tasksRepository.getStatuses(CommonTaskType.Task))
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    private fun loadStoriesWithTasks() = viewModelScope.launch {
        storiesWithTasks.value = Result(ResultStatus.Loading)

        storiesWithTasks.value = try {
            Result(
                resultStatus = ResultStatus.Success,
                data = tasksRepository.getSprintUserStories(this@SprintViewModel.sprintId!!)
                    .map { it to async { tasksRepository.getUserStoryTasks(it.id) } }
                    .map { (story, tasks) -> story to tasks.await() }
                    .toMap()
            )
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    private fun loadStorylessTasks() = viewModelScope.launch {
        storylessTasks.value = Result(ResultStatus.Loading)

        storylessTasks.value = try {
            Result(ResultStatus.Success, tasksRepository.getSprintTasks(this@SprintViewModel.sprintId!!))
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    private fun loadIssues() = viewModelScope.launch {
        issues.value = Result(ResultStatus.Loading)

        issues.value = try {
            Result(ResultStatus.Success, tasksRepository.getSprintIssues(this@SprintViewModel.sprintId!!))
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    fun reset() {
        sprintId = null
        statuses.value = null
        storiesWithTasks.value = null
        storylessTasks.value = null
        issues.value = null
    }
}