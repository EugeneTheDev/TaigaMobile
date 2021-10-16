package io.eugenethedev.taigamobile.ui.screens.scrum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.paging.CommonPagingSource
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.*
import io.eugenethedev.taigamobile.ui.utils.asLazyPagingItems
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class ScrumViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var sprintsRepository: ISprintsRepository
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState

    val projectName by lazy { session.currentProjectName }

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() {
        if (screensState.shouldReloadScrumScreen) {
            reset()
        }
    }

    // stories

    private val storiesQuery = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    val stories by lazy {
        storiesQuery.flatMapLatest { query ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
                CommonPagingSource { tasksRepository.getBacklogUserStories(it, query) }
            }.flow
        }.asLazyPagingItems(viewModelScope)
    }
    
    fun searchStories(query: String) {
        storiesQuery.value = query
    }

    // sprints

    val sprints by lazy {
        Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
            CommonPagingSource { sprintsRepository.getSprints(it) }
        }.flow.asLazyPagingItems(viewModelScope)
    }

    val createSprintResult = MutableResultFlow<Unit>(NothingResult())

    fun createSprint(name: String, start: LocalDate, end: LocalDate) = viewModelScope.launch {
        createSprintResult.loadOrError(R.string.permission_error) {
            sprintsRepository.createSprint(name, start, end)
            sprints.refresh()
        }
    }

    fun reset() {
        storiesQuery.value = ""
        createSprintResult.value = NothingResult()
        stories.refresh()
        sprints.refresh()
    }
}
