package io.eugenethedev.taigamobile.ui.screens.scrum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ScrumViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState

    val projectName: String get() = session.currentProjectName

    val stories = MutableLiveResult<List<CommonTask>>()
    private var currentStoriesQuery = ""
    private var currentStoriesPage = 0
    private var maxStoriesPage = Int.MAX_VALUE

    val sprints = MutableLiveResult<List<Sprint>>()
    private var currentSprintPage = 0
    private var maxSprintPage = Int.MAX_VALUE

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() {
        if (screensState.shouldReloadScrumScreen) {
            reset()
        }

        if (stories.value == null || sprints.value == null) {
            
            loadSprints()
        }
    }
    
    fun loadStories(query: String) = viewModelScope.launch {
        query.takeIf { it != currentStoriesQuery }?.let {
            currentStoriesQuery = it
            currentStoriesPage = 0
            maxStoriesPage = Int.MAX_VALUE
            stories.value = Result(ResultStatus.Success, emptyList())
        }

        if (currentStoriesPage == maxStoriesPage) return@launch

        stories.value = Result(ResultStatus.Loading, stories.value?.data)

        try {
            tasksRepository.getBacklogUserStories(++currentStoriesPage, query)
                .also { stories.value = Result(ResultStatus.Success, stories.value?.data.orEmpty() + it) }
                .takeIf { it.isEmpty() }
                ?.run { maxStoriesPage = currentStoriesPage }
        } catch (e: Exception) {
            Timber.w(e)
            stories.value = Result(ResultStatus.Error, stories.value?.data, message = R.string.common_error_message)
        }
    }

    fun loadSprints() = viewModelScope.launch {
        if (currentSprintPage == maxSprintPage) return@launch

        sprints.value = Result(ResultStatus.Loading, sprints.value?.data)

        try {
            tasksRepository.getSprints(++currentSprintPage)
                .also { sprints.value = Result(ResultStatus.Success, sprints.value?.data.orEmpty() + it) }
                .takeIf { it.isEmpty() }
                ?.run { maxSprintPage = currentSprintPage }
        } catch (e: Exception) {
            Timber.w(e)
            sprints.value = Result(ResultStatus.Error, sprints.value?.data, message = R.string.common_error_message)
        }
    }

    fun reset() {
        stories.value = null
        currentStoriesQuery = ""
        currentStoriesPage = 0
        maxStoriesPage = Int.MAX_VALUE

        sprints.value = null
        currentSprintPage = 0
        maxSprintPage = Int.MAX_VALUE
    }

}