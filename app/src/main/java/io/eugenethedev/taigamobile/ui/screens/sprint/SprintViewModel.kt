package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
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
import javax.inject.Inject

class SprintViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var sprintsRepository: ISprintsRepository
    @Inject lateinit var screensState: ScreensState

    private var sprintId: Long? = null

    val statuses = MutableLiveResult<List<Status>?>()
    val storiesWithTasks = MutableLiveResult<Map<CommonTask, List<CommonTask>>?>()
    val storylessTasks = MutableLiveResult<List<CommonTask>?>()
    val issues = MutableLiveResult<List<CommonTask>?>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start(sprintId: Long) = viewModelScope.launch {
        if (screensState.shouldReloadSprintScreen) {
            reset()
        }

        this@SprintViewModel.sprintId = sprintId

        if (statuses.value == null) {
            joinAll(
                launch {
                    statuses.loadOrError(preserveValue = false) { tasksRepository.getStatuses(CommonTaskType.Task) }
                },
                launch {
                    storiesWithTasks.loadOrError(preserveValue = false) {
                        coroutineScope {
                            sprintsRepository.getSprintUserStories(this@SprintViewModel.sprintId!!)
                                .map { it to async { tasksRepository.getUserStoryTasks(it.id) } }
                                .map { (story, tasks) -> story to tasks.await() }
                                .toMap()
                        }
                    }
                },
                launch {
                    issues.loadOrError(preserveValue = false) { sprintsRepository.getSprintIssues(this@SprintViewModel.sprintId!!) }
                }
            )
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
