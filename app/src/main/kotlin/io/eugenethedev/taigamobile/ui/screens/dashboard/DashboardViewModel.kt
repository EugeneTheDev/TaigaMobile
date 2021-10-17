package io.eugenethedev.taigamobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.utils.MutableResultFlow
import io.eugenethedev.taigamobile.ui.utils.NothingResult
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var session: Session

    val workingOn = MutableResultFlow<List<CommonTask>>()
    val watching = MutableResultFlow<List<CommonTask>>()

    private var shouldReload = true

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun onOpen() = viewModelScope.launch {
        if (!shouldReload) return@launch
        joinAll(
            launch { workingOn.loadOrError(preserveValue = false) { tasksRepository.getWorkingOn() } },
            launch { watching.loadOrError(preserveValue = false) { tasksRepository.getWatching() } }
        )
        shouldReload = false
    }

    fun changeCurrentProject(commonTask: CommonTask) {
        commonTask.projectInfo.apply {
            session.changeCurrentProject(id, name)
        }
    }

    init {
        session.taskEdit.onEach {
            workingOn.value = NothingResult()
            watching.value = NothingResult()
            shouldReload = true
        }.launchIn(viewModelScope)
    }
}
