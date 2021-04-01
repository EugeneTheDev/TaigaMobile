package io.eugenethedev.taigamobile.ui.screens.scrum

import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.ui.commons.StoriesViewModel
import io.eugenethedev.taigamobile.ui.utils.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.Result
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ScrumViewModel : StoriesViewModel() {

    @Inject lateinit var session: Session

    val projectName: String get() = session.currentProjectName

    val sprints = MutableLiveResult<List<Sprint>>()

    private var currentSprintPage = 0
    private var maxSprintPage = Int.MAX_VALUE

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() {
        if (
            statuses.value == null &&
            stories.value == null &&
            sprints.value == null &&
            session.currentProjectId > 0
        ) {
            viewModelScope.launch { loadStatuses() }
            loadSprints()
        }
    }

    fun loadSprints() = viewModelScope.launch {
        if (currentSprintPage == maxSprintPage) return@launch

        sprints.value = Result(ResultStatus.LOADING, sprints.value?.data)

        try {
            tasksRepository.getSprints(++currentSprintPage)
                .also { sprints.value = Result(ResultStatus.SUCCESS, sprints.value?.data.orEmpty() + it) }
                .takeIf { it.isEmpty() }
                ?.run { maxSprintPage = currentSprintPage }
        } catch (e: Exception) {
            Timber.w(e)
            sprints.value = Result(ResultStatus.ERROR, sprints.value?.data, message = R.string.common_error_message)
        }
    }

    override fun reset() {
        super.reset()
        sprints.value = null
        currentSprintPage = 0
        maxSprintPage = Int.MAX_VALUE
    }

}