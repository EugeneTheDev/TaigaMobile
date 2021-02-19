package io.eugenethedev.taigamobile.ui.screens.stories

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.Story
import io.eugenethedev.taigamobile.domain.repositories.IStoriesRepository
import io.eugenethedev.taigamobile.ui.utils.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.Result
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class StoriesViewModel : ViewModel() {

    @Inject lateinit var session: Session
    @Inject lateinit var storiesRepository: IStoriesRepository

    val projectName: String get() = session.currentProjectName

    val statuses = MutableLiveResult<Set<Status>>()
    val stories = MutableLiveData<MutableSet<Story>>()

    // manage number of pages and loading state
    private val statusesStates = MutableLiveData(mutableMapOf<Status, StatusState>())
    val loadingStatusIds = MediatorLiveData<List<Long>>().also { ids ->
        ids.value = emptyList()
        ids.addSource(statusesStates) {
            ids.value = it.filter { (_, state) -> state.isLoading }.map { (status, _) -> status.id }
        }
    }

    private class StatusState {
        var currentPage = 0
        var maxPage = Int.MAX_VALUE
        var isLoading = false
    }

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun onScreenOpen() = viewModelScope.launch {
        statuses.value = Result(ResultStatus.LOADING)
        stories.value = mutableSetOf()

        try {
            session.currentProjectId.takeIf { it >= 0 }?.let {
                statuses.value = Result(
                    resultStatus = ResultStatus.SUCCESS,
                    storiesRepository.getStatuses(it).toSet().onEach {
                        statusesStates.value?.set(it, StatusState())
                        loadData(it)
                    }
                )
            }
        } catch (e: Exception) {
            Timber.w(e)
            statuses.value = Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }
    }

    fun loadData(status: Status) = viewModelScope.launch {
        statusesStates.value?.get(status)?.apply {
            if (currentPage == maxPage) return@launch

            isLoading = true

            delay(5000)
            storiesRepository.getStories(session.currentProjectId, status.id, ++currentPage).takeIf { it.isNotEmpty() }?.let {
                stories.value?.addAll(it)
            } ?: run {
                maxPage = currentPage // reached maximum page
            }

            isLoading = false
        }
    }
}