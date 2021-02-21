package io.eugenethedev.taigamobile.ui.screens.stories

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

    val statuses = MutableLiveResult<List<Status>>()
    val stories = MutableLiveResult<List<Story>>()

    val loadingStatusIds = MutableLiveData(emptyList<Long>())

    private val statusesStates = mutableMapOf<Status, StatusState>()

    private class StatusState {
        var currentPage = 0
        var maxPage = Int.MAX_VALUE
    }

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun onScreenOpen() = viewModelScope.launch {
        statuses.value = Result(ResultStatus.LOADING)
        stories.value = Result(ResultStatus.SUCCESS)
        statusesStates.clear()
        loadingStatusIds.value = emptyList()

        try {
            session.currentProjectId.takeIf { it >= 0 }?.let {
                statuses.value = Result(
                    resultStatus = ResultStatus.SUCCESS,
                    storiesRepository.getStatuses(it).onEach {
                        statusesStates[it] = StatusState()
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
        statusesStates[status]?.apply {
            if (currentPage == maxPage) return@launch

            loadingStatusIds.value = loadingStatusIds.value.orEmpty() + status.id

            try {
                storiesRepository.getStories(session.currentProjectId, status.id, ++currentPage).takeIf { it.isNotEmpty() }?.let {
                    stories.value = Result(ResultStatus.SUCCESS, stories.value?.data.orEmpty() + it)
                } ?: run {
                    maxPage = currentPage // reached maximum page
                }
            } catch (e: Exception) {
                Timber.w(e)
                statuses.value = Result(ResultStatus.ERROR, message = R.string.common_error_message)
            }

            loadingStatusIds.value = loadingStatusIds.value.orEmpty() - status.id
        }
    }
}