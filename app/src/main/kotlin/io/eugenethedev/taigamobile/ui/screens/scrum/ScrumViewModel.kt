package io.eugenethedev.taigamobile.ui.screens.scrum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.domain.paging.CommonPagingSource
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.state.Settings
import io.eugenethedev.taigamobile.ui.utils.MutableResultFlow
import io.eugenethedev.taigamobile.ui.utils.NothingResult
import io.eugenethedev.taigamobile.ui.utils.asLazyPagingItems
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class ScrumViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var sprintsRepository: ISprintsRepository
    @Inject lateinit var session: Session
    @Inject lateinit var settings: Settings

    val projectName by lazy { session.currentProjectName }

    private var shouldReload = true

    init {
        appComponent.inject(this)
    }

    fun onOpen() {
        if (!shouldReload) return
        viewModelScope.launch {
            filters.loadOrError {
                tasksRepository.getFiltersData(
                    commonTaskType = CommonTaskType.UserStory,
                    isCommonTaskFromBacklog = true
                )
            }
        }
        shouldReload = false
    }

    // stories

    val filters = MutableResultFlow<FiltersData>()
//    todo not here but need to figure out how to modify activeFilters when we get filters data
//    todo make sure we filter the activeFilters using the ids we have in settings
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
        val idArray = filters.statuses.map{(id) -> id.toString()} + filters.assignees.map{(id) -> id.toString()} + filters.roles.map{(id) -> id.toString()} + filters.createdBy.map{(id) -> id.toString()} + filters.epics.map{(id) -> id.toString()}
        settings.changeScrumFilters(idArray.toSet())
    }

    // sprints

    val openSprints by sprints(isClosed = false)
    val closedSprints by sprints(isClosed = true)

    private fun sprints(isClosed: Boolean) = lazy {
        Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
            CommonPagingSource { sprintsRepository.getSprints(it, isClosed) }
        }.flow.asLazyPagingItems(viewModelScope)
    }

    val createSprintResult = MutableResultFlow<Unit>(NothingResult())

    fun createSprint(name: String, start: LocalDate, end: LocalDate) = viewModelScope.launch {
        createSprintResult.loadOrError(R.string.permission_error) {
            sprintsRepository.createSprint(name, start, end)
            openSprints.refresh()
        }
    }

    init {
        session.currentProjectId.onEach {
            activeFilters.value = FiltersData()
            createSprintResult.value = NothingResult()
            stories.refresh()
            openSprints.refresh()
            closedSprints.refresh()
            shouldReload = true
        }.launchIn(viewModelScope)

        session.taskEdit.onEach {
            stories.refresh()
            openSprints.refresh()
            closedSprints.refresh()
        }.launchIn(viewModelScope)

        session.sprintEdit.onEach {
            openSprints.refresh()
            closedSprints.refresh()
        }.launchIn(viewModelScope)
    }
}
