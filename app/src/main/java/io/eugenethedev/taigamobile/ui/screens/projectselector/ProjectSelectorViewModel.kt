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
import io.eugenethedev.taigamobile.ui.utils.Status
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ProjectSelectorViewModel : ViewModel() {

    @Inject lateinit var searchRepository: ISearchRepository
    @Inject lateinit var session: Session

    val projectsResult = MutableLiveResult<Unit>(Result(Status.LOADING))
    val projects = MutableLiveData(mutableSetOf<Project>())

    init {
        TaigaApp.appComponent.inject(this)
    }

    private var currentPage = 0
    private var maxPage = Int.MAX_VALUE

    fun onScreenOpen() {
        currentPage = 0
        maxPage = Int.MAX_VALUE
        loadData()
    }

    fun loadData(query: String = "") = viewModelScope.launch {
        if (currentPage == maxPage) return@launch

        projectsResult.value = Result(Status.LOADING)
        try {
            searchRepository.searchProjects(query, ++currentPage).takeIf { it.isNotEmpty() }?.let {
                projects.value?.addAll(it)
            } ?: run {
                maxPage = currentPage // reached maximum page
            }
            projectsResult.value = Result(Status.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            projectsResult.value = Result(Status.ERROR, message = R.string.common_error_message)
        }
    }
}