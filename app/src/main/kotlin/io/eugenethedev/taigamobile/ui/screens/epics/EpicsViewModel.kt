package io.eugenethedev.taigamobile.ui.screens.epics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.paging.CommonPagingSource
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.state.subscribeToAll
import io.eugenethedev.taigamobile.ui.utils.asLazyPagingItems
import javax.inject.Inject

class EpicsViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var tasksRepository: ITasksRepository

    val projectName by lazy { session.currentProjectName }

    init {
        TaigaApp.appComponent.inject(this)
    }

    val epics by lazy {
        Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
            CommonPagingSource { tasksRepository.getEpics(it) }
        }.flow.asLazyPagingItems(viewModelScope)
    }

    init {
        viewModelScope.subscribeToAll(session.currentProjectId, session.taskEdit) {
            epics.refresh()
        }
    }
}
