package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.Comment
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.repositories.IStoriesRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.utils.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.Result
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CommonTaskViewModel : ViewModel() {

    @Inject lateinit var storiesRepository: IStoriesRepository
    @Inject lateinit var usersRepository: IUsersRepository

    val story = MutableLiveResult<CommonTaskExtended>()
    val creator = MutableLiveResult<User>()
    val assignees = MutableLiveResult<List<User>>()
    val watchers = MutableLiveResult<List<User>>()
    val tasks = MutableLiveResult<List<CommonTask>>()
    val comments = MutableLiveResult<List<Comment>>()

    val isLoading = MutableLiveData(true)

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start(commonTaskId: Long) = viewModelScope.launch {
        if (story.value == null) isLoading.value = true
        story.value = Result(ResultStatus.LOADING)

        story.value = try {
            storiesRepository.getUserStory(commonTaskId).let {
                val creatorAsync = async { loadUser(it.creatorId) }
                val assigneesAsyncs = it.assignedIds.map { async { loadUser(it) } }
                val watchersAsyncs = it.watcherIds.map { async { loadUser(it) } }
                val tasksAsync = async { storiesRepository.getUserStoryTasks(commonTaskId) }
                val commentsAsync = async { storiesRepository.getComments(commonTaskId) }

                creator.value = creatorAsync.await()
                assignees.value = Result(ResultStatus.SUCCESS, assigneesAsyncs.mapNotNull { it.await().data })
                watchers.value = Result(ResultStatus.SUCCESS, watchersAsyncs.mapNotNull { it.await().data })
                tasks.value = Result(ResultStatus.SUCCESS, tasksAsync.await())
                comments.value = Result(ResultStatus.SUCCESS, commentsAsync.await())
                Result(ResultStatus.SUCCESS, it)
            }
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }

        isLoading.value = false
    }

    private suspend fun loadUser(userId: Long) = try {
        Result(ResultStatus.SUCCESS, usersRepository.getUser(userId))
    } catch (e: Exception) {
        Timber.w(e)
        Result(ResultStatus.ERROR, message = R.string.common_error_message)
    }
}