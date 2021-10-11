package io.eugenethedev.taigamobile.ui.screens.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.domain.paging.CommonPagingSource
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.*
import io.eugenethedev.taigamobile.ui.utils.asLazyPagingItems
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class IssuesViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var tasksRepository: ITasksRepository

    val projectName get() = session.currentProjectName

    init {
        TaigaApp.appComponent.inject(this)
    }
    
    fun start() = viewModelScope.launch {
        if (screensState.shouldReloadIssuesScreen) {
            reset()
        }

        if (filtersData.value is NothingResult) {
            launch { filtersData.loadOrError { tasksRepository.getFiltersData(CommonTaskType.Issue) } }
        }
    }

    val filtersData = MutableResultFlow<FiltersData>()
    private val issuesQuery = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    val issues by lazy {
        issuesQuery.flatMapLatest { query ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE, enablePlaceholders = false)) {
                CommonPagingSource { tasksRepository.getIssues(it, query) }
            }.flow
        }.asLazyPagingItems(viewModelScope)
    }

    fun searchIssues(query: String) {
        issuesQuery.value = query
    }
    
    fun reset() {
        issuesQuery.value = ""
        issues.refresh()
    }
}
