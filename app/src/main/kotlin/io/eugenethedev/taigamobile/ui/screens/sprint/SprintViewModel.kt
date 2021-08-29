package io.eugenethedev.taigamobile.ui.screens.sprint

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class SprintViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var sprintsRepository: ISprintsRepository
    @Inject lateinit var screensState: ScreensState

    private var sprintId: Long = -1

    val sprint = MutableLiveResult<Sprint>()
    val statuses = MutableLiveResult<List<Status>?>()
    val storiesWithTasks = MutableLiveResult<Map<CommonTask, List<CommonTask>>?>()
    val storylessTasks = MutableLiveResult<List<CommonTask>?>()
    val issues = MutableLiveResult<List<CommonTask>?>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start(sprintId: Long) {
        if (screensState.shouldReloadSprintScreen) {
            reset()
        }

        this.sprintId = sprintId

        if (sprint.value == null) {
            loadData()
        }
    }

    private fun loadData() = viewModelScope.launch {
        sprint.loadOrError {
            sprintsRepository.getSprint(sprintId).also {
                joinAll(
                    launch {
                        statuses.loadOrError(preserveValue = false) { tasksRepository.getStatuses(CommonTaskType.Task) }
                    },
                    launch {
                        storiesWithTasks.loadOrError(preserveValue = false) {
                            coroutineScope {
                                sprintsRepository.getSprintUserStories(sprintId)
                                    .map { it to async { tasksRepository.getUserStoryTasks(it.id) } }
                                    .map { (story, tasks) -> story to tasks.await() }
                                    .toMap()
                            }
                        }
                    },
                    launch {
                        issues.loadOrError(preserveValue = false) { sprintsRepository.getSprintIssues(sprintId) }
                    },
                    launch {
                        storylessTasks.loadOrError(preserveValue = false) { sprintsRepository.getSprintTasks(sprintId) }
                    }
                )
            }
        }
    }

    val editResult = MutableLiveResult<Unit>()
    fun editSprint(name: String, start: LocalDate, end: LocalDate) = viewModelScope.launch {
        editResult.loadOrError(R.string.permission_error) {
            sprintsRepository.editSprint(sprintId, name, start, end)
            screensState.modify()
            loadData().join()
        }
    }

    val deleteResult = MutableLiveResult<Unit>()
    fun deleteSprint() = viewModelScope.launch {
        deleteResult.loadOrError(R.string.permission_error) {
            sprintsRepository.deleteSprint(sprintId)
            screensState.modify()
            loadData().join()
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun reset() {
        sprintId = -1
        sprint.value = null
        statuses.value = null
        storiesWithTasks.value = null
        storylessTasks.value = null
        issues.value = null
    }
}
