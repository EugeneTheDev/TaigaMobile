package io.eugenethedev.taigamobile.ui.screens.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class KanbanViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var usersRepository: IUsersRepository
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var session: Session

    val projectName: String get() = session.currentProjectName

    val statuses = MutableLiveResult<List<Status>>()
    val team = MutableLiveResult<List<User>>()
    val stories = MutableLiveResult<List<CommonTaskExtended>>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() {
        if (screensState.shouldReloadKanbanScreen) {
            reset()
        }

        if (team.value == null || stories.value == null) {
            loadTeam()
            loadStories()
            loadStatuses()
        }
    }

    private fun loadStatuses() = viewModelScope.launch {
        statuses.value = Result(ResultStatus.Loading)

        statuses.value = try {
            Result(ResultStatus.Success, tasksRepository.getStatuses(CommonTaskType.UserStory))
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    private fun loadTeam() = viewModelScope.launch {
        team.value = Result(ResultStatus.Loading)

        team.value = try {
            Result(ResultStatus.Success, usersRepository.getTeam().map { it.toUser() })
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    private fun loadStories() = viewModelScope.launch {
        stories.value = Result(ResultStatus.Loading)

        stories.value = try {
            Result(ResultStatus.Success, tasksRepository.getAllUserStories())
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    fun reset() {
        statuses.value = null
        team.value = null
        stories.value = null
    }

}
