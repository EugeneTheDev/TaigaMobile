package io.eugenethedev.taigamobile.ui.screens.kanban

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
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
    val swimlanes = MutableLiveResult<List<Swimlane?>>()

    val selectedSwimlane = MutableLiveData<Swimlane?>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() = viewModelScope.launch {
        if (screensState.shouldReloadKanbanScreen) {
            reset()
        }

        if (statuses.value == null) {
            joinAll(
                launch {
                    statuses.loadOrError(preserveValue = false) { tasksRepository.getStatuses(CommonTaskType.UserStory) }
                },
                launch {
                    team.loadOrError(preserveValue = false) { usersRepository.getTeam().map { it.toUser() } }
                },
                launch {
                    stories.loadOrError(preserveValue = false) { tasksRepository.getAllUserStories() }
                },
                launch {
                    swimlanes.loadOrError() {
                        listOf(null) + tasksRepository.getSwimlanes() // prepend null to show "unclassified" swimlane
                    }
                }
            )
        }
    }

    fun selectSwimlane(swimlane: Swimlane?) {
        selectedSwimlane.value = swimlane
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun reset() {
        statuses.value = null
        team.value = null
        stories.value = null
        swimlanes.value = null
    }

}
