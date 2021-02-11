package io.eugenethedev.taigamobile.ui.screens.projectselector

import android.util.Log
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
import retrofit2.HttpException
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
            projects.value?.addAll(searchRepository.searchProjects(query, ++currentPage))
            projectsResult.value = Result(Status.SUCCESS)
        } catch (e: Exception) {
            // we dont want to show error if we cannot find more pages
            (e as? HttpException)?.takeIf { it.code() == 404 }?.let {
                maxPage = currentPage
                projectsResult.value = Result(Status.SUCCESS)
            } ?: run {
                Log.i(javaClass.simpleName, "Error", e)
                projectsResult.value = Result(Status.ERROR, message = R.string.common_error_message)
            }
        }
    }
}