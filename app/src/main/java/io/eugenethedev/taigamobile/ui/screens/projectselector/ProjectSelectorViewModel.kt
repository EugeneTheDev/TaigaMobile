package io.eugenethedev.taigamobile.ui.screens.projectselector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.domain.repositories.ISearchRepository
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.fixAnimation
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ProjectSelectorViewModel : ViewModel() {

    @Inject lateinit var searchRepository: ISearchRepository
    @Inject lateinit var session: Session

    val projects = MutableLiveResult<List<Project>>()
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
            projects.value = Result(ResultStatus.SUCCESS, emptyList())
        }

        if (currentPage == maxPage) return@launch

        projects.value = Result(ResultStatus.LOADING, projects.value?.data)
        fixAnimation()

        try {
            searchRepository.searchProjects(query, ++currentPage)
                .also { projects.value = Result(ResultStatus.SUCCESS, projects.value?.data.orEmpty() + it) }
                .takeIf { it.isEmpty() }
                ?.run { maxPage = currentPage /* reached maximum page */ }
        } catch (e: Exception) {
            Timber.w(e)
            projects.value = Result(ResultStatus.ERROR, projects.value?.data, message = R.string.common_error_message)
        }
    }
}