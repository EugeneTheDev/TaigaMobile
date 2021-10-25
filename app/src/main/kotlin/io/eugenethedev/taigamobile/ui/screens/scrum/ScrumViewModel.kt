package io.eugenethedev.taigamobile.ui.screens.scrum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.domain.paging.CommonPagingSource
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.utils.MutableResultFlow
import io.eugenethedev.taigamobile.ui.utils.NothingResult
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

    val projectName by lazy { session.currentProjectName }

    private var shouldReload = true

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun onOpen() {
        if (!shouldReload) return
        viewModelScope.launch {
            filters.loadOrError { tasksRepository.getFiltersData(CommonTaskType.UserStory) }
        }
        shouldReload = false
    }

    // stories

    val filters = MutableResultFlow<FiltersData>()
    val activeFilters = MutableStateFlow(FiltersData())
    @OptIn(ExperimentalCoroutinesApi::class)
    val stories by lazy {
        activeFilters.flatMapLatest { filters ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE, enablePlaceholders = false)) {
                CommonPagingSource { tasksRepository.getBacklogUserStories(it, filters) }
            }.flow
        }.asLazyPagingItems(viewModelScope)
    }
    
    fun selectFilters(filters: FiltersData) {
        activeFilters.value = filters
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

    init {
        session.currentProjectId.onEach {
            activeFilters.value = FiltersData()
            createSprintResult.value = NothingResult()
            stories.refresh()
            sprints.refresh()
            shouldReload = true
        }.launchIn(viewModelScope)

        session.taskEdit.onEach {
            stories.refresh()
        }.launchIn(viewModelScope)

        session.sprintEdit.onEach {
            sprints.refresh()
        }.launchIn(viewModelScope)
    }
}
