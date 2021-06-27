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
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.fixAnimation
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.InputStream
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
    val customFields = MutableLiveResult<CustomFields>()
    val attachments = MutableLiveResult<List<Attachment>>()
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
            commonTask.value = Result(ResultStatus.Loading)
        }

        commonTask.value = try {
            tasksRepository.getCommonTask(commonTaskId, commonTaskType).let {
                val creatorAsync = async { loadUser(it.creatorId) }
                val customFieldsAsync = async { tasksRepository.getCustomFields(commonTaskId, commonTaskType) }
                val attachmentsAsync = async { tasksRepository.getAttachments(commonTaskId, commonTaskType) }
                val assigneesAsyncs = it.assignedIds.map { async { loadUser(it) } }
                val watchersAsyncs = it.watcherIds.map { async { loadUser(it) } }
                val userStoriesAsync = async { tasksRepository.getEpicUserStories(commonTaskId) }
                val tasksAsync = async { tasksRepository.getUserStoryTasks(commonTaskId) }
                val commentsAsync = async { tasksRepository.getComments(commonTaskId, commonTaskType) }

                creator.value = creatorAsync.await()
                customFields.value = Result(ResultStatus.Success, customFieldsAsync.await())
                attachments.value = Result(ResultStatus.Success, attachmentsAsync.await())
                assignees.value = Result(ResultStatus.Success, assigneesAsyncs.mapNotNull { it.await().data })
                watchers.value = Result(ResultStatus.Success, watchersAsyncs.mapNotNull { it.await().data })
                userStories.value = Result(ResultStatus.Success, userStoriesAsync.await())
                tasks.value = Result(ResultStatus.Success, tasksAsync.await())
                comments.value = Result(
                    ResultStatus.Success,
                    commentsAsync.await().filter { it.deleteDate == null }
                        .map { it.also { it.canDelete = it.author.id == session.currentUserId } }
                )
                Result(ResultStatus.Success, it)
            }
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    private suspend fun loadUser(userId: Long) = try {
        Result(ResultStatus.Success, usersRepository.getUser(userId))
    } catch (e: Exception) {
        Timber.w(e)
        Result(ResultStatus.Error, message = R.string.common_error_message)
    }

    /**
     * Edit related stuff
     */

    // Edit status (and also type, severity, priority)

    val statuses = MutableLiveResult<List<Status>>()
    val statusSelectResult = MutableLiveResult<StatusType>()

    fun loadStatuses(statusType: StatusType) = viewModelScope.launch {
        statuses.value = Result(ResultStatus.Loading)
        fixAnimation()

        statuses.value = try {
            Result(ResultStatus.Success, tasksRepository.getStatusByType(commonTaskType, statusType))
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    fun selectStatus(status: Status) = viewModelScope.launch {
        statusSelectResult.value = Result(ResultStatus.Loading, status.type)

        statusSelectResult.value = try {
            tasksRepository.changeStatus(commonTaskId, commonTaskType, status.id, status.type, commonTaskVersion)
            loadData().join()
            screensState.modify()
            Result(ResultStatus.Success)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.permission_error)
        }
    }

    // Edit sprint

    val sprints = MutableLiveResult<List<Sprint?>?>()
    private var currentSprintPage = 0
    private var maxSprintPage = Int.MAX_VALUE

    fun loadSprints(query: String?) = viewModelScope.launch {
        if (query == null) { // only handling null. search not supported
            currentSprintPage = 0
            maxSprintPage = Int.MAX_VALUE
            sprints.value = null
        }

        if (currentSprintPage == maxSprintPage) return@launch

        sprints.value = Result(ResultStatus.Loading, sprints.value?.data)
        fixAnimation()

        try {
            tasksRepository.getSprints(++currentSprintPage)
                .also {
                    sprints.value = Result(
                        ResultStatus.Success,
                        // prepending null here to always show "remove from sprints" sprint first
                        data = listOf(null) + (sprints.value?.data.orEmpty().filterNotNull() + it)
                    )
                }
                .takeIf { it.isEmpty() }
                ?.run { maxSprintPage = currentSprintPage }
        } catch (e: Exception) {
            Timber.w(e)
            sprints.value = Result(ResultStatus.Error, sprints.value?.data, message = R.string.common_error_message)
        }
    }

    fun selectSprint(sprint: Sprint?) = viewModelScope.launch {
        sprints.value = Result(ResultStatus.Loading, sprints.value?.data)

        sprints.value = try {
            tasksRepository.changeSprint(commonTaskId, commonTaskType, sprint?.id, commonTaskVersion)
            loadData().join()
            screensState.modify()
            Result(ResultStatus.Success, sprints.value?.data)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, sprints.value?.data, message = R.string.permission_error)
        }
    }

    // Edit linked epic
    val epics = MutableLiveResult<List<CommonTask>>()
    private var currentEpicQuery = ""
    private var currentEpicPage = 0
    private var maxEpicPage = Int.MAX_VALUE

    fun loadEpics(query: String?) = viewModelScope.launch {
        query.takeIf { it != currentEpicQuery }?.let {
            currentEpicQuery = it
            currentEpicPage = 0
            maxEpicPage = Int.MAX_VALUE
            epics.value = Result(ResultStatus.Success, emptyList())
        }

        if (currentEpicPage == maxEpicPage) return@launch

        epics.value = Result(ResultStatus.Loading, epics.value?.data)
        fixAnimation()

        try {
            tasksRepository.getEpics(++currentEpicPage, query)
                .also {
                    epics.value = Result(
                        ResultStatus.Success,
                        data = epics.value?.data.orEmpty() + it
                    )
                }
                .takeIf { it.isEmpty() }
                ?.run { maxEpicPage = currentEpicPage }
        } catch (e: Exception) {
            Timber.w(e)
            epics.value = Result(ResultStatus.Error, epics.value?.data, message = R.string.common_error_message)
        }
    }

    fun linkToEpic(epic: CommonTask) = viewModelScope.launch {
        epics.value = Result(ResultStatus.Loading, epics.value?.data)

        epics.value = try {
            tasksRepository.linkToEpic(epic.id, commonTaskId)
            loadData().join()
            screensState.modify()
            Result(ResultStatus.Success, epics.value?.data)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, epics.value?.data, message = R.string.permission_error)
        }
    }

    fun unlinkFromEpic(epic: EpicShortInfo) = viewModelScope.launch {
        epics.value = Result(ResultStatus.Loading, epics.value?.data)

        epics.value = try {
            tasksRepository.unlinkFromEpic(epic.id, commonTaskId)
            loadData().join()
            screensState.modify()
            Result(ResultStatus.Success, epics.value?.data)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, epics.value?.data, message = R.string.permission_error)
        }
    }


    // use team for both assignees and watchers
    val team = MutableLiveResult<List<User>>()
    private var _team = emptyList<User>()
    private var currentTeamQuery: String = ""

    fun loadTeam(query: String?) = viewModelScope.launch {
        if (query == currentTeamQuery) return@launch
        currentTeamQuery = query.orEmpty()

        team.value = Result(ResultStatus.Loading)
        team.value = query?.let { q ->
            Result(
                resultStatus = ResultStatus.Success,
                data = _team.filter {
                    val regex = Regex("^.*$q.*$", RegexOption.IGNORE_CASE)
                    it.displayName.matches(regex) || it.username.matches(regex)
                }
            )
        } ?: run {
            fixAnimation()
            try {
                _team = usersRepository.getTeam().map { it.toUser() }
                Result(ResultStatus.Success, _team)
            } catch (e: Exception) {
                Timber.w(e)
                Result(ResultStatus.Error, message = R.string.common_error_message)
            }
        }
    }

    // Edit assignees

    private fun changeAssignees(user: User, remove: Boolean) = viewModelScope.launch {
        assignees.value = Result(ResultStatus.Loading, assignees.value?.data)

        assignees.value = try {
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
            Result(ResultStatus.Success, assignees.value?.data)

        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, assignees.value?.data, message = R.string.permission_error)
        }
    }

    fun addAssignee(user: User) = changeAssignees(user, remove = false)
    fun removeAssignee(user: User) = changeAssignees(user, remove = true)

    // Edit watchers

    private fun changeWatchers(user: User, remove: Boolean) = viewModelScope.launch {
        watchers.value = Result(ResultStatus.Loading, watchers.value?.data)

        watchers.value = try {
            tasksRepository.changeWatchers(
                commonTaskId, commonTaskType,
                commonTask.value?.data?.watcherIds.orEmpty().let {
                    if (remove) it - user.id
                    else it + user.id
                },
                commonTaskVersion
            )
            loadData().join()
            Result(ResultStatus.Success, watchers.value?.data)

        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, watchers.value?.data, message = R.string.permission_error)
        }
    }

    fun addWatcher(user: User) = changeWatchers(user, remove = false)
    fun removeWatcher(user: User) = changeWatchers(user, remove = true)

    // Edit comments

    fun createComment(comment: String) = viewModelScope.launch { 
        comments.value = Result(ResultStatus.Loading, comments.value?.data)

        comments.value = try {
            tasksRepository.createComment(commonTaskId, commonTaskType, comment, commonTaskVersion)
            loadData().join()
            Result(ResultStatus.Success, comments.value?.data)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, comments.value?.data, message = R.string.permission_error)
        }
    }

    fun deleteComment(comment: Comment) = viewModelScope.launch {
        comments.value = Result(ResultStatus.Loading)

        comments.value = try {
            tasksRepository.deleteComment(commonTaskId, commonTaskType, comment.id)
            loadData().join()
            Result(ResultStatus.Success, comments.value?.data)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, comments.value?.data, message = R.string.permission_error)
        }
    }


    fun deleteAttachment(attachment: Attachment) = viewModelScope.launch {
        attachments.value = Result(ResultStatus.Loading, attachments.value?.data)

        attachments.value = try {
            tasksRepository.deleteAttachment(commonTaskType, attachment.id)
            loadData().join()
            Result(ResultStatus.Success, attachments.value?.data)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, attachments.value?.data, message = R.string.permission_error)
        }
    }

    fun addAttachment(fileName: String, inputStream: InputStream) = viewModelScope.launch {
        attachments.value = Result(ResultStatus.Loading, attachments.value?.data)

        attachments.value = try {
            tasksRepository.addAttachment(commonTaskId, commonTaskType, fileName, inputStream)
            loadData().join()
            Result(ResultStatus.Success, attachments.value?.data)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, attachments.value?.data, message = R.string.permission_error)
        }
    }

    // Edit task itself (title & description)
    val editResult = MutableLiveResult<Unit>()

    fun editTask(title: String, description: String) = viewModelScope.launch {
        editResult.value = Result(ResultStatus.Loading)

        editResult.value = try {
            tasksRepository.editCommonTask(commonTaskId, commonTaskType, title, description, commonTaskVersion)
            loadData().join()
            screensState.modify()
            Result(ResultStatus.Success)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.permission_error)
        }
    }

    // Delete task
    val deleteResult = MutableLiveResult<Unit>()

    fun deleteTask() = viewModelScope.launch {
        deleteResult.value = Result(ResultStatus.Loading)

        deleteResult.value = try {
            tasksRepository.deleteCommonTask(commonTaskType, commonTaskId)
            screensState.modify()
            Result(ResultStatus.Success)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.permission_error)
        }
    }

    val promoteResult = MutableLiveResult<CommonTask>()

    fun promoteToUserStory() = viewModelScope.launch {
        promoteResult.value = Result(ResultStatus.Loading)

        promoteResult.value = try {
            val result = tasksRepository.promoteCommonTaskToUserStory(commonTaskId, commonTaskType)
            screensState.modify()
            Result(ResultStatus.Success, result)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.permission_error)
        }
    }

    fun editCustomField(customField: CustomField, value: CustomFieldValue?) = viewModelScope.launch {
        customFields.value = Result(ResultStatus.Loading, customFields.value?.data)

        customFields.value = try {
            tasksRepository.editCustomFields(
                commonTaskType = commonTaskType,
                commonTaskId = commonTaskId,
                fields = customFields.value?.data?.fields.orEmpty().map {
                    it.id to (if (it.id == customField.id) value else it.value)
                }.toMap(),
                version = customFields.value?.data?.version ?: 0
            )
            loadData().join()
            Result(ResultStatus.Success, customFields.value?.data)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, customFields.value?.data, message = R.string.permission_error)
        }

    }
}
