package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.utils.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.Result
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.fixAnimation
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CommonTaskViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var usersRepository: IUsersRepository

    private var commonTaskId: Long = -1
    private lateinit var commonTaskType: CommonTaskType

    val commonTask = MutableLiveResult<CommonTaskExtended>()
    private val commonTaskVersion get() = commonTask.value?.data?.version ?: -1
    
    val creator = MutableLiveResult<User>()
    val assignees = MutableLiveResult<List<User>>()
    val watchers = MutableLiveResult<List<User>>()
    val userStories = MutableLiveResult<List<CommonTask>>()
    val tasks = MutableLiveResult<List<CommonTask>>()
    val comments = MutableLiveResult<List<Comment>>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start(commonTaskId: Long, commonTaskType: CommonTaskType) {
        this.commonTaskId = commonTaskId
        this.commonTaskType = commonTaskType
        loadData()
    }

    private fun loadData()  = viewModelScope.launch {
        if (commonTask.value == null) {
            commonTask.value = Result(ResultStatus.LOADING)
        }

        commonTask.value = try {
            tasksRepository.getCommonTask(commonTaskId, commonTaskType).let {
                val creatorAsync = async { loadUser(it.creatorId) }
                val assigneesAsyncs = it.assignedIds.map { async { loadUser(it) } }
                val watchersAsyncs = it.watcherIds.map { async { loadUser(it) } }
                val userStoriesAsync = async { tasksRepository.getEpicUserStories(commonTaskId) }
                val tasksAsync = async { tasksRepository.getUserStoryTasks(commonTaskId) }
                val commentsAsync = async { tasksRepository.getComments(commonTaskId, commonTaskType) }

                creator.value = creatorAsync.await()
                assignees.value = Result(ResultStatus.SUCCESS, assigneesAsyncs.mapNotNull { it.await().data })
                watchers.value = Result(ResultStatus.SUCCESS, watchersAsyncs.mapNotNull { it.await().data })
                userStories.value = Result(ResultStatus.SUCCESS, userStoriesAsync.await())
                tasks.value = Result(ResultStatus.SUCCESS, tasksAsync.await())
                comments.value = Result(
                    ResultStatus.SUCCESS,
                    commentsAsync.await().filter { it.deleteDate == null }
                        .map { it.also { it.canDelete = it.author.id == session.currentUserId } }
                )
                Result(ResultStatus.SUCCESS, it)
            }
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }
    }

    private suspend fun loadUser(userId: Long) = try {
        Result(ResultStatus.SUCCESS, usersRepository.getUser(userId))
    } catch (e: Exception) {
        Timber.w(e)
        Result(ResultStatus.ERROR, message = R.string.common_error_message)
    }

    /**
     * Edit related stuff
     */

    // Edit status

    val statuses = MutableLiveResult<List<Status>>()
    val statusSelectResult = MutableLiveResult<Unit>()

    fun loadStatuses(query: String?) = viewModelScope.launch {
        if (query == null) { // only handling null. search not supported
            statuses.value = null
        } else {
            return@launch
        }

        statuses.value = Result(ResultStatus.LOADING)
        fixAnimation()

        statuses.value = try {
            Result(ResultStatus.SUCCESS, tasksRepository.getStatuses(commonTaskType))
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }
    }

    fun selectStatus(status: Status) = viewModelScope.launch {
        statusSelectResult.value = Result(ResultStatus.LOADING)

        statusSelectResult.value = try {
            tasksRepository.changeStatus(commonTaskId, commonTaskType, status.id, commonTaskVersion)
            loadData().join()
            screensState.modify()
            Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }

    // Edit sprint

    val sprints = MutableLiveResult<List<Sprint?>>()
    val sprintSelectResult = MutableLiveResult<Unit>()
    private var currentSprintPage = 0
    private var maxSprintPage = Int.MAX_VALUE

    fun loadSprints(query: String?) = viewModelScope.launch {
        if (query == null) { // only handling null. search not supported
            currentSprintPage = 0
            maxSprintPage = Int.MAX_VALUE
            sprints.value = null
        }

        if (currentSprintPage == maxSprintPage) return@launch

        sprints.value = Result(ResultStatus.LOADING, sprints.value?.data)
        fixAnimation()

        try {
            tasksRepository.getSprints(++currentSprintPage)
                .also {
                    sprints.value = Result(
                        ResultStatus.SUCCESS,
                        // prepending null here to always show "remove from sprints" sprint first
                        data = listOf(null) + (sprints.value?.data.orEmpty().filterNotNull() + it)
                    )
                }
                .takeIf { it.isEmpty() }
                ?.run { maxSprintPage = currentSprintPage }
        } catch (e: Exception) {
            Timber.w(e)
            sprints.value = Result(ResultStatus.ERROR, sprints.value?.data, message = R.string.common_error_message)
        }
    }

    fun selectSprint(sprint: Sprint?) = viewModelScope.launch {
        sprintSelectResult.value = Result(ResultStatus.LOADING)

        sprintSelectResult.value = try {
            tasksRepository.changeSprint(commonTaskId, commonTaskType, sprint?.id, commonTaskVersion)
            loadData().join()
            screensState.modify()
            Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }

    // Edit linked epic
    val epics = MutableLiveResult<List<CommonTask>>()
    val epicsSelectResult = MutableLiveResult<Unit>()
    private var currentEpicQuery = ""
    private var currentEpicPage = 0
    private var maxEpicPage = Int.MAX_VALUE

    fun loadEpics(query: String?) = viewModelScope.launch {
        query.takeIf { it != currentEpicQuery }?.let {
            currentEpicQuery = it
            currentEpicPage = 0
            maxEpicPage = Int.MAX_VALUE
            epics.value = Result(ResultStatus.SUCCESS, emptyList())
        }

        if (currentEpicPage == maxEpicPage) return@launch

        epics.value = Result(ResultStatus.LOADING, epics.value?.data)
        fixAnimation()

        try {
            tasksRepository.getEpics(++currentEpicPage, query)
                .also {
                    epics.value = Result(
                        ResultStatus.SUCCESS,
                        data = epics.value?.data.orEmpty() + it
                    )
                }
                .takeIf { it.isEmpty() }
                ?.run { maxEpicPage = currentEpicPage }
        } catch (e: Exception) {
            Timber.w(e)
            epics.value = Result(ResultStatus.ERROR, epics.value?.data, message = R.string.common_error_message)
        }
    }

    fun linkToEpic(epic: CommonTask) = viewModelScope.launch {
        epicsSelectResult.value = Result(ResultStatus.LOADING)

        epicsSelectResult.value = try {
            tasksRepository.linkToEpic(epic.id, commonTaskId)
            loadData().join()
            Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }

    fun unlinkFromEpic(epic: EpicShortInfo) = viewModelScope.launch {
        epicsSelectResult.value = Result(ResultStatus.LOADING)

        epicsSelectResult.value = try {
            tasksRepository.unlinkFromEpic(epic.id, commonTaskId)
            loadData().join()
            Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }


    // use team for both assignees and watchers
    val team = MutableLiveResult<List<User>>()
    private var _team = emptyList<User>()
    private var currentTeamQuery: String = ""

    fun loadTeam(query: String?) = viewModelScope.launch {
        if (query == currentTeamQuery) return@launch
        currentTeamQuery = query.orEmpty()

        team.value = Result(ResultStatus.LOADING)
        team.value = query?.let { q ->
            // FIXME I had to put a small delay here, otherwise results in search were always incorrect.
            // I don't have a fucking clue why this is happening and i don't like it, but i don't know how to fix it.
            // UPD: after some investigations i think this is bug with LazyColumn updating (data is updated, but UI doesn't)
            delay(20)
            Result(
                resultStatus = ResultStatus.SUCCESS,
                data = _team.filter {
                    val regex = Regex("^.*$q.*$", RegexOption.IGNORE_CASE)
                    it.displayName.matches(regex) || it.username.matches(regex)
                }
            )
        } ?: run {
            fixAnimation()
            try {
                _team = usersRepository.getTeam().map { it.toUser() }
                Result(ResultStatus.SUCCESS, _team)
            } catch (e: Exception) {
                Timber.w(e)
                Result(ResultStatus.ERROR, message = R.string.common_error_message)
            }
        }
    }

    // Edit assignees
    val assigneesResult = MutableLiveResult<Unit>()

    private fun changeAssignees(user: User, remove: Boolean) = viewModelScope.launch {
        assigneesResult.value = Result(ResultStatus.LOADING)

        assigneesResult.value = try {
            tasksRepository.changeAssignees(
                commonTaskId, commonTaskType,
                commonTask.value?.data?.assignedIds.orEmpty().let {
                    if (remove) it - user.id
                    else it + user.id
                },
                commonTaskVersion
            )
            loadData().join()
            screensState.modify()
            Result(ResultStatus.SUCCESS)

        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }

    fun addAssignee(user: User) = changeAssignees(user, remove = false)
    fun removeAssignee(user: User) = changeAssignees(user, remove = true)

    // Edit watchers
    val watchersResult = MutableLiveResult<Unit>()

    private fun changeWatchers(user: User, remove: Boolean) = viewModelScope.launch {
        watchersResult.value = Result(ResultStatus.LOADING)

        watchersResult.value = try {
            tasksRepository.changeWatchers(
                commonTaskId, commonTaskType,
                commonTask.value?.data?.watcherIds.orEmpty().let {
                    if (remove) it - user.id
                    else it + user.id
                },
                commonTaskVersion
            )
            loadData().join()
            Result(ResultStatus.SUCCESS)

        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }

    fun addWatcher(user: User) = changeWatchers(user, remove = false)
    fun removeWatcher(user: User) = changeWatchers(user, remove = true)

    // Edit comments
    val commentsResult = MutableLiveResult<Unit>()
    
    fun createComment(comment: String) = viewModelScope.launch { 
        commentsResult.value = Result(ResultStatus.LOADING)
        
        commentsResult.value = try {
            tasksRepository.createComment(commonTaskId, commonTaskType, comment, commonTaskVersion)
            loadData().join()
            Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }

    fun deleteComment(comment: Comment) = viewModelScope.launch {
        commentsResult.value = Result(ResultStatus.LOADING)

        commentsResult.value = try {
            tasksRepository.deleteComment(commonTaskId, commonTaskType, comment.id)
            loadData().join()
            Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }

    // Edit task itself (title & description)
    val editResult = MutableLiveResult<Unit>()

    fun editTask(title: String, description: String) = viewModelScope.launch {
        editResult.value = Result(ResultStatus.LOADING)

        editResult.value = try {
            tasksRepository.editTask(commonTaskId, commonTaskType, title, description, commonTaskVersion)
            loadData().join()
            screensState.modify()
            Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }

    // Delete task
    val deleteResult = MutableLiveResult<Unit>()

    fun deleteTask() = viewModelScope.launch {
        deleteResult.value = Result(ResultStatus.LOADING)

        deleteResult.value = try {
            tasksRepository.deleteCommonTask(commonTaskType, commonTaskId)
            screensState.modify()
            Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }

    val promoteResult = MutableLiveResult<CommonTask>()

    fun promoteTask() = viewModelScope.launch {
        promoteResult.value = Result(ResultStatus.LOADING)

        promoteResult.value = try {
            val result = tasksRepository.promoteTaskToUserStory(commonTaskId)
            screensState.modify()
            Result(ResultStatus.SUCCESS, result)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.permission_error)
        }
    }
}
