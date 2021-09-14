package io.eugenethedev.taigamobile.ui.screens.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.*
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class IssuesViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var tasksRepository: ITasksRepository

    val projectName get() = session.currentProjectName
    val issues = MutableResultFlow<List<CommonTask>>()

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

        if (issues.value.resultStatus == ResultStatus.Nothing) {
            loadIssues()
        }
    }

    fun loadIssues(query: String = "") = viewModelScope.launch {
        query.takeIf { it != currentIssuesQuery }?.let {
            currentIssuesQuery = it
            currentIssuesPage = 0
            maxIssuesPage = Int.MAX_VALUE
            issues.value = Result(ResultStatus.Success, emptyList())
        }

        if (currentIssuesPage == maxIssuesPage) return@launch

        issues.loadOrError {
            tasksRepository.getIssues(++currentIssuesPage, query).also {
                if (it.isEmpty()) maxIssuesPage = currentIssuesPage
            }.let {
                issues.value.data.orEmpty() + it
            }
        }
    }
    
    fun reset() {
        issues.value = Result(ResultStatus.Nothing)
        currentIssuesQuery = ""
        currentIssuesPage = 0
        maxIssuesPage = Int.MAX_VALUE
    }
}
