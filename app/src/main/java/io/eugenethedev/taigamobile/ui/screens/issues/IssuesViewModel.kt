package io.eugenethedev.taigamobile.ui.screens.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class IssuesViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var tasksRepository: ITasksRepository

    val projectName get() = session.currentProjectName
    val issues = MutableLiveResult<List<CommonTask>>()

    private var currentIssuesQuery = ""
    private var currentIssuesPage = 0
    private var maxIssuesPage = Int.MAX_VALUE
    
    init {
        TaigaApp.appComponent.inject(this)
    }
    
    fun start() {
        if (screensState.shouldReloadIssuesScreen) {
            reset()
        }

        if (issues.value == null) {
            loadIssues("")
        }
    }

    fun loadIssues(query: String) = viewModelScope.launch {
        query.takeIf { it != currentIssuesQuery }?.let {
            currentIssuesQuery = it
            currentIssuesPage = 0
            maxIssuesPage = Int.MAX_VALUE
            issues.value = Result(ResultStatus.SUCCESS, emptyList())
        }

        if (currentIssuesPage == maxIssuesPage) return@launch

        issues.value = Result(ResultStatus.LOADING, issues.value?.data)

        try {
            tasksRepository.getIssues(++currentIssuesPage, query)
                .also { issues.value = Result(ResultStatus.SUCCESS, issues.value?.data.orEmpty() + it) }
                .takeIf { it.isEmpty() }
                ?.run { maxIssuesPage = currentIssuesPage }
        } catch (e: Exception) {
            Timber.w(e)
            issues.value = Result(ResultStatus.ERROR, issues.value?.data, message = R.string.common_error_message)
        }
    }
    
    fun reset() {
        issues.value = null
        currentIssuesQuery = ""
        currentIssuesPage = 0
        maxIssuesPage = Int.MAX_VALUE
    }
}
