package io.eugenethedev.taigamobile.ui.screens.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.commons.MutableResultFlow
import io.eugenethedev.taigamobile.ui.commons.NothingResult
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class KanbanViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var usersRepository: IUsersRepository
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var session: Session

    val projectName: String get() = session.currentProjectName

    val statuses = MutableResultFlow<List<Status>>()
    val team = MutableResultFlow<List<User>>()
    val stories = MutableResultFlow<List<CommonTaskExtended>>()
    val swimlanes = MutableResultFlow<List<Swimlane?>>()

    val selectedSwimlane = MutableStateFlow<Swimlane?>(null)

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() = viewModelScope.launch {
        if (screensState.shouldReloadKanbanScreen) {
            reset()
        }

        if (statuses.value is NothingResult) {
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
                    swimlanes.loadOrError {
                        listOf(null) + tasksRepository.getSwimlanes() // prepend null to show "unclassified" swimlane
                    }
                }
            )
        }
    }

    fun selectSwimlane(swimlane: Swimlane?) {
        selectedSwimlane.value = swimlane
    }

    fun reset() {
        statuses.value = NothingResult()
        team.value = NothingResult()
        stories.value = NothingResult()
        swimlanes.value = NothingResult()
    }
}
