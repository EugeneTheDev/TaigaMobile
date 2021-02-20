package io.eugenethedev.taigamobile.ui.screens.projectselector

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.domain.repositories.ISearchRepository
import io.eugenethedev.taigamobile.ui.utils.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.Result
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ProjectSelectorViewModel : ViewModel() {

    @Inject lateinit var searchRepository: ISearchRepository
    @Inject lateinit var session: Session

    val projectsResult = MutableLiveResult<Unit>()
    val projects = MutableLiveData<Set<Project>>()
    val isProjectSelected = MutableLiveData(false)

    init {
        TaigaApp.appComponent.inject(this)
    }

    private var currentPage = 0
    private var maxPage = Int.MAX_VALUE
    private var currentQuery = ""

    fun onScreenOpen() {
        projects.value = mutableSetOf()
        isProjectSelected.value = false
        currentPage = 0
        maxPage = Int.MAX_VALUE
        loadData()
    }

    fun onProjectSelected(project: Project) {
        session.apply {
            currentProjectId = project.id
            currentProjectName = project.name
        }
        isProjectSelected.value = true
    }

    fun loadData(query: String = "") = viewModelScope.launch {
        query.toLowerCase(Locale.getDefault()).takeIf { it != currentQuery }?.let {
            currentQuery = it
            currentPage = 0
            maxPage = Int.MAX_VALUE
            projects.value = setOf()
        }

        if (currentPage == maxPage) return@launch

        projectsResult.value = Result(ResultStatus.LOADING)
        try {
            searchRepository.searchProjects(query, ++currentPage).takeIf { it.isNotEmpty() }?.let {
                projects.value = projects.value!! + it
            } ?: run {
                maxPage = currentPage // reached maximum page
            }
            projectsResult.value = Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            projectsResult.value = Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }
    }
}