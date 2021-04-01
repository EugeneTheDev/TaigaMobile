package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.ui.commons.StoriesViewModel
import io.eugenethedev.taigamobile.ui.utils.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.Result
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber

class SprintViewModel : StoriesViewModel() {

    val tasks = MutableLiveResult<List<CommonTask>>()

    var currentStoriesPage = 0
    var maxStoriesPage = Int.MAX_VALUE

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start(sprintId: Long) {
        this.sprintId = sprintId

        if (
            statuses.value == null &&
            stories.value == null
        ) {
            viewModelScope.launch { loadStatuses() }
            loadTasks()
        }
    }

    fun loadTasks() = viewModelScope.launch {
        if (currentStoriesPage == maxStoriesPage) return@launch

        tasks.value = Result(ResultStatus.LOADING, tasks.value?.data)

        try {
            tasksRepository.getSprintTasks(sprintId!!, ++currentStoriesPage)
                .also { tasks.value = Result(ResultStatus.SUCCESS, tasks.value?.data.orEmpty() + it) }
                .takeIf { it.isEmpty() }
                ?.run { maxStoriesPage = currentStoriesPage }
        } catch (e: Exception) {
            Timber.w(e)
            tasks.value = Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }
    }

    override fun reset() {
        super.reset()
        tasks.value = null
        currentStoriesPage = 0
        maxStoriesPage = Int.MAX_VALUE
    }
}