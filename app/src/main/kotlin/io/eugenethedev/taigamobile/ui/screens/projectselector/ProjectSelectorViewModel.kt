package io.eugenethedev.taigamobile.ui.screens.projectselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.domain.repositories.ISearchRepository
import io.eugenethedev.taigamobile.ui.commons.MutableResultFlow
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.fixAnimation
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ProjectSelectorViewModel : ViewModel() {

    @Inject lateinit var searchRepository: ISearchRepository
    @Inject lateinit var session: Session

    val projects = MutableResultFlow<List<Project>>()
    val currentProjectId get() = session.currentProjectId

    init {
        TaigaApp.appComponent.inject(this)
    }

    private var currentPage = 0
    private var maxPage = Int.MAX_VALUE
    private var currentQuery = ""

    fun start() {
        currentPage = 0
        maxPage = Int.MAX_VALUE
        loadData()
    }

    fun selectProject(project: Project) {
        session.apply {
            currentProjectId = project.id
            currentProjectName = project.name
        }
    }

    fun loadData(query: String = "") = viewModelScope.launch {
        query.takeIf { it != currentQuery }?.let {
            currentQuery = it
            currentPage = 0
            maxPage = Int.MAX_VALUE
            projects.value = Result(ResultStatus.Success, emptyList())
        }

        if (currentPage == maxPage) return@launch

        projects.loadOrError {
            fixAnimation()

            searchRepository.searchProjects(query, ++currentPage).also {
                if (it.isEmpty()) maxPage = currentPage
            }.let {
                projects.value.data.orEmpty() + it
            }
        }
    }
}
