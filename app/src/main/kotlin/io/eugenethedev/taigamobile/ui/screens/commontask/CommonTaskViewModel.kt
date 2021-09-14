package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.commons.*
import io.eugenethedev.taigamobile.ui.utils.fixAnimation
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.InputStream
import java.time.LocalDate
import javax.inject.Inject

class CommonTaskViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var usersRepository: IUsersRepository
    @Inject lateinit var sprintsRepository: ISprintsRepository

    private var commonTaskId: Long = -1
    private lateinit var commonTaskType: CommonTaskType

    val commonTask = MutableResultFlow<CommonTaskExtended>()
    private val commonTaskVersion = commonTask.map { commonTask.value.data?.version ?: -1 }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = -1
    )
    
    val creator = MutableResultFlow<User>()
    val customFields = MutableResultFlow<CustomFields>()
    val attachments = MutableResultFlow<List<Attachment>>()
    val assignees = MutableResultFlow<List<User>>()
    val watchers = MutableResultFlow<List<User>>()
    val userStories = MutableResultFlow<List<CommonTask>>()
    val tasks = MutableResultFlow<List<CommonTask>>()
    val comments = MutableResultFlow<List<Comment>>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start(commonTaskId: Long, commonTaskType: CommonTaskType) {
        this.commonTaskId = commonTaskId
        this.commonTaskType = commonTaskType
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        commonTask.loadOrError(showLoading = commonTask.value is NothingResult) {
            tasksRepository.getCommonTask(commonTaskId, commonTaskType).also {

                suspend fun MutableResultFlow<List<User>>.loadUsersFromIds(ids: List<Long>) =
                    loadOrError(showLoading = false) {
                        ids.map {
                            coroutineScope {
                                async { usersRepository.getUser(it) }
                            }
                        }.awaitAll()
                    }

                joinAll(
                    launch {
                        creator.loadOrError(showLoading = false) { usersRepository.getUser(it.creatorId) }
                    },
                    launch {
                        customFields.loadOrError(showLoading = false) { tasksRepository.getCustomFields(commonTaskId, commonTaskType) }
                    },
                    launch {
                        attachments.loadOrError(showLoading = false) { tasksRepository.getAttachments(commonTaskId, commonTaskType) }
                    },
                    launch { assignees.loadUsersFromIds(it.assignedIds) },
                    launch { watchers.loadUsersFromIds(it.watcherIds) },
                    launch {
                        userStories.loadOrError(showLoading = false) { tasksRepository.getEpicUserStories(commonTaskId) }
                    },
                    launch {
                        tasks.loadOrError(showLoading = false) { tasksRepository.getUserStoryTasks(commonTaskId) }
                    },
                    launch {
                        comments.loadOrError(showLoading = false) { tasksRepository.getComments(commonTaskId, commonTaskType) }
                    }
                )
            }
        }
    }

    /**
     * Edit related stuff
     */

    // Edit status (and also type, severity, priority)

    val statuses = MutableResultFlow<List<Status>>()
    val statusSelectResult = MutableResultFlow<StatusType>()

    fun loadStatuses(statusType: StatusType) = viewModelScope.launch {
        statuses.loadOrError(preserveValue = false) {
            fixAnimation()
            tasksRepository.getStatusByType(commonTaskType, statusType)
        }
    }

    fun selectStatus(status: Status) = viewModelScope.launch {
        statusSelectResult.value = LoadingResult(status.type)

        statusSelectResult.loadOrError(R.string.permission_error) {
            tasksRepository.changeStatus(commonTaskId, commonTaskType, status.id, status.type, commonTaskVersion.value)
            loadData().join()
            screensState.modify()
            status.type
        }
    }

    // Edit sprint

    val sprints = MutableResultFlow<List<Sprint?>>()
    private var currentSprintPage = 0
    private var maxSprintPage = Int.MAX_VALUE

    fun loadSprints(query: String?) = viewModelScope.launch {
        if (query == null) { // only handling null. search not supported
            currentSprintPage = 0
            maxSprintPage = Int.MAX_VALUE
            sprints.value = NothingResult()
        }

        if (currentSprintPage == maxSprintPage) return@launch
        
        sprints.loadOrError {
            fixAnimation()
            
            sprintsRepository.getSprints(++currentSprintPage).also {
                if (it.isEmpty()) maxSprintPage = currentSprintPage
            }.let {
                // prepending null here to always show "remove from sprints" sprint first
                listOf(null) + (sprints.value.data.orEmpty().filterNotNull() + it)
            }
        }
    }

    fun selectSprint(sprint: Sprint?) = viewModelScope.launch {
        sprints.loadOrError(R.string.permission_error) {
            tasksRepository.changeSprint(commonTaskId, commonTaskType, sprint?.id, commonTaskVersion.value)
            loadData().join()
            screensState.modify()
            sprints.value.data
        }
    }

    // Edit linked epic
    val epics = MutableResultFlow<List<CommonTask>>()
    private var currentEpicQuery = ""
    private var currentEpicPage = 0
    private var maxEpicPage = Int.MAX_VALUE

    fun loadEpics(query: String?) = viewModelScope.launch {
        query.takeIf { it != currentEpicQuery }?.let {
            currentEpicQuery = it
            currentEpicPage = 0
            maxEpicPage = Int.MAX_VALUE
            epics.value = NothingResult()
        }

        if (currentEpicPage == maxEpicPage) return@launch
        
        epics.loadOrError {
            fixAnimation()
            
            tasksRepository.getEpics(++currentEpicPage, query).also {
                if (it.isEmpty()) maxEpicPage = currentEpicPage
            }.let {
                epics.value.data.orEmpty() + it
            }
        }
    }

    fun linkToEpic(epic: CommonTask) = viewModelScope.launch {
        epics.loadOrError(R.string.permission_error) {
            tasksRepository.linkToEpic(epic.id, commonTaskId)
            loadData().join()
            screensState.modify()
            epics.value.data
        }
    }

    fun unlinkFromEpic(epic: EpicShortInfo) = viewModelScope.launch {
        epics.loadOrError(R.string.permission_error) {
            tasksRepository.unlinkFromEpic(epic.id, commonTaskId)
            loadData().join()
            screensState.modify()
            epics.value.data
        }
    }


    // use team for both assignees and watchers
    val team = MutableResultFlow<List<User>>()
    private var _team = emptyList<User>()
    private var currentTeamQuery: String = ""

    fun loadTeam(query: String?) = viewModelScope.launch {
        if (query == currentTeamQuery) return@launch
        currentTeamQuery = query.orEmpty()

        team.loadOrError(preserveValue = false) {
            query?.let { q ->
                _team.filter {
                    val regex = Regex("^.*$q.*$", RegexOption.IGNORE_CASE)
                    it.displayName.matches(regex) || it.username.matches(regex)
                }
            } ?: run {
                fixAnimation()
                _team = usersRepository.getTeam().map { it.toUser() }
                _team
            }
        }
    }

    // Edit assignees

    private fun changeAssignees(user: User, remove: Boolean) = viewModelScope.launch {
        assignees.loadOrError(R.string.permission_error) {
            tasksRepository.changeAssignees(
                commonTaskId, commonTaskType,
                commonTask.value.data?.assignedIds.orEmpty().let {
                    if (remove) it - user.id
                    else it + user.id
                },
                commonTaskVersion.value
            )
            loadData().join()
            screensState.modify()
            assignees.value.data
        }
    }

    fun addAssignee(user: User) = changeAssignees(user, remove = false)
    fun removeAssignee(user: User) = changeAssignees(user, remove = true)

    // Edit watchers

    private fun changeWatchers(user: User, remove: Boolean) = viewModelScope.launch {
        watchers.loadOrError(R.string.permission_error) {
            tasksRepository.changeWatchers(
                commonTaskId, commonTaskType,
                commonTask.value.data?.watcherIds.orEmpty().let {
                    if (remove) it - user.id
                    else it + user.id
                },
                commonTaskVersion.value
            )
            loadData().join()
            watchers.value.data
        }
    }

    fun addWatcher(user: User) = changeWatchers(user, remove = false)
    fun removeWatcher(user: User) = changeWatchers(user, remove = true)

    // Edit comments

    fun createComment(comment: String) = viewModelScope.launch {
        comments.loadOrError(R.string.permission_error) {
            tasksRepository.createComment(commonTaskId, commonTaskType, comment, commonTaskVersion.value)
            loadData().join()
            comments.value.data
        }
    }

    fun deleteComment(comment: Comment) = viewModelScope.launch {
        comments.loadOrError(R.string.permission_error) {
            tasksRepository.deleteComment(commonTaskId, commonTaskType, comment.id)
            loadData().join()
            comments.value.data
        }
    }


    fun deleteAttachment(attachment: Attachment) = viewModelScope.launch {
        attachments.loadOrError(R.string.permission_error) {
            tasksRepository.deleteAttachment(commonTaskType, attachment.id)
            loadData().join()
            attachments.value.data
        }
    }

    fun addAttachment(fileName: String, inputStream: InputStream) = viewModelScope.launch {
        attachments.loadOrError(R.string.permission_error) {
            tasksRepository.addAttachment(commonTaskId, commonTaskType, fileName, inputStream)
            loadData().join()
            attachments.value.data
        }
    }

    // Edit task itself (title & description)
    val editResult = MutableResultFlow<Unit>()

    fun editTask(title: String, description: String) = viewModelScope.launch {
        editResult.loadOrError(R.string.permission_error) {
            tasksRepository.editCommonTask(commonTaskId, commonTaskType, title, description, commonTaskVersion.value)
            loadData().join()
            screensState.modify()
        }
    }

    // Delete task
    val deleteResult = MutableResultFlow<Unit>()

    fun deleteTask() = viewModelScope.launch {
        deleteResult.loadOrError(R.string.permission_error) {
            tasksRepository.deleteCommonTask(commonTaskType, commonTaskId)
            screensState.modify()
        }
    }

    val promoteResult = MutableResultFlow<CommonTask>()

    fun promoteToUserStory() = viewModelScope.launch {
        promoteResult.loadOrError(R.string.permission_error, preserveValue = false) {
            tasksRepository.promoteCommonTaskToUserStory(commonTaskId, commonTaskType).also {
                screensState.modify()
            }
        }
    }

    fun editCustomField(customField: CustomField, value: CustomFieldValue?) = viewModelScope.launch {
        customFields.loadOrError(R.string.permission_error) {
            tasksRepository.editCustomFields(
                commonTaskType = commonTaskType,
                commonTaskId = commonTaskId,
                fields = customFields.value.data?.fields.orEmpty().map {
                    it.id to (if (it.id == customField.id) value else it.value)
                }.toMap(),
                version = customFields.value.data?.version ?: 0
            )
            loadData().join()
            customFields.value.data
        }

    }

    // Tags

    val tags = MutableResultFlow<List<Tag>>()
    private var _tags = emptyList<Tag>()
    private var currentTagsQuery: String = ""

    fun loadTags(query: String?) = viewModelScope.launch {
        if (query == null) {
            tags.value = NothingResult()
            return@launch
        }

        if (query == currentTagsQuery) return@launch
        currentTagsQuery = query.orEmpty()

        tags.loadOrError(showLoading = false) {
            _tags.also {
                if (it.isEmpty()) _tags = tasksRepository.getAllTags(commonTaskType)
            }.let {
                it.filter { query.isNotEmpty() && query.lowercase() in it.name.lowercase() }
            }
        }

    }

    private fun editTag(tag: Tag, remove: Boolean) = viewModelScope.launch {
        tags.loadOrError(R.string.permission_error) {
            tasksRepository.editTags(
                commonTaskType = commonTaskType,
                commonTaskId = commonTaskId,
                tags = commonTask.value.data?.tags.orEmpty()
                    .let { if (remove) it - tag else it + tag },
                version = commonTaskVersion.value
            )
            loadData().join()
            screensState.modify()
            tags.value.data
        }
    }

    fun addTag(tag: Tag) = editTag(tag, remove = false)
    fun deleteTag(tag: Tag) = editTag(tag, remove = true)

    // Swimlanes

    val swimlanes = MutableResultFlow<List<Swimlane?>>()

    fun loadSwimlanes(query: String?) = viewModelScope.launch {
        // only load swimlanes if null
        query ?: run {
            swimlanes.loadOrError(preserveValue = false) {
                listOf(null) + tasksRepository.getSwimlanes() // prepend "unclassified"
            }
        }
    }

    fun selectSwimlane(swimlane: Swimlane?) = viewModelScope.launch {
        swimlanes.loadOrError(R.string.permission_error) {
            tasksRepository.changeUserStorySwimlane(commonTaskId, swimlane?.id, commonTaskVersion.value)
            loadData().join()
            screensState.modify()
            swimlanes.value.data
        }
    }

    // Due date

    val dueDateResult = MutableResultFlow<Unit>()

    fun selectDueDate(date: LocalDate?) = viewModelScope.launch {
        dueDateResult.loadOrError(R.string.permission_error) {
            tasksRepository.changeDueDate(commonTaskId, commonTaskType, date, commonTaskVersion.value)
            loadData().join()
        }
    }

    val colorResult = MutableResultFlow<Unit>()

    fun selectEpicColor(color: String) = viewModelScope.launch {
        colorResult.loadOrError(R.string.permission_error) {
            tasksRepository.changeEpicColor(commonTaskId, color, commonTaskVersion.value)
            loadData().join()
            screensState.modify()
        }
    }
}
