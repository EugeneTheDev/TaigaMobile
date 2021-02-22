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
            viewModelScope.launch { loadSprints() }
        }
    }

    private suspend fun loadSprints() {
        sprints.value = Result(ResultStatus.LOADING)

        try {
            sprints.value = Result(ResultStatus.SUCCESS, storiesRepository.getSprints())
        } catch (e: Exception) {
            Timber.w(e)
            sprints.value = Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }
    }

    override fun reset() {
        super.reset()
        sprints.value = null
    }

}