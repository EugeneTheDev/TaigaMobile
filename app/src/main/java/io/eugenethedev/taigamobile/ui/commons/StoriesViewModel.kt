package io.eugenethedev.taigamobile.ui.commons

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

abstract class StoriesViewModel : ViewModel() {

    @Inject lateinit var tasksRepository: ITasksRepository

    val statuses = MutableLiveResult<List<Status>>()
    val stories = MutableLiveResult<List<CommonTask>>()

    val loadingStatusIds = MutableLiveData(emptyList<Long>())
    val visibleStatusIds = MutableLiveData(emptyList<Long>())

    private val statusesStates = mutableMapOf<Status, StatusState>()

    private class StatusState {
        var currentPage = 0
        var maxPage = Int.MAX_VALUE
    }

    protected var sprintId: Long? = null

    protected suspend fun loadStatuses() {
        statuses.value = Result(ResultStatus.Loading)
        stories.value = Result(ResultStatus.Success)

        statuses.value = try {
             Result(
                resultStatus = ResultStatus.Success,
                tasksRepository.getStatuses(CommonTaskType.UserStory).onEach {
                    statusesStates[it] = StatusState()
                }
            )

        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    fun loadStories(status: Status) = viewModelScope.launch {
        statusesStates[status]?.apply {
            if (currentPage == maxPage) return@launch

            loadingStatusIds.value = loadingStatusIds.value.orEmpty() + status.id

//            try {
//                tasksRepository.getBacklogUserStories(status.id, ++currentPage, "")
//                    .also { stories.value = Result(ResultStatus.Success, stories.value?.data.orEmpty() + it) }
//                    .takeIf { it.isEmpty() }
//                    ?.run { maxPage = currentPage /* reached maximum page */ }
//            } catch (e: Exception) {
//                Timber.w(e)
//                stories.value = Result(ResultStatus.Error, stories.value?.data.orEmpty(), message = R.string.common_error_message)
//            }
//            loadingStatusIds.value = loadingStatusIds.value.orEmpty() - status.id
        }
    }

    fun statusClick(statusId: Long) {
        visibleStatusIds.value = if (statusId in visibleStatusIds.value.orEmpty()) {
            visibleStatusIds.value.orEmpty() - statusId
        } else {
            visibleStatusIds.value.orEmpty() + statusId
        }
    }

    open fun reset() {
        sprintId = null
        statuses.value = null
        stories.value = null
        statusesStates.clear()
        loadingStatusIds.value = null
        visibleStatusIds.value = null
    }
}
