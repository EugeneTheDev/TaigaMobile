package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.insertHeaderItem
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.paging.CommonPagingSource
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.state.postUpdate
import io.eugenethedev.taigamobile.ui.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.InputStream
import java.time.LocalDate
import javax.inject.Inject

class CommonTaskViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var usersRepository: IUsersRepository
    @Inject lateinit var sprintsRepository: ISprintsRepository

    companion object {
        val SPRINT_HEADER = Sprint(-1, "HEADER", -1, LocalDate.MIN, LocalDate.MIN, 0, false)
        val SWIMLANE_HEADER = Swimlane(-1, "HEADER", -1)
    }

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

    val team = MutableResultFlow<List<User>>()
    val tags = MutableResultFlow<List<Tag>>()
    val swimlanes = MutableResultFlow<List<Swimlane>>()
    val statuses = MutableResultFlow<Map<StatusType, List<Status>>>()

    val isAssignedToMe = assignees.map { session.currentUserId.value in it.data?.map { it.id }.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
    val isWatchedByMe = watchers.map { session.currentUserId.value in it.data?.map { it.id }.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
    val projectName by lazy { session.currentProjectName }

    init {
        appComponent.inject(this)
    }

    fun onOpen(commonTaskId: Long, commonTaskType: CommonTaskType) {
        this.commonTaskId = commonTaskId
        this.commonTaskType = commonTaskType
        loadData(isReloading = false)
    }

    private fun loadData(isReloading: Boolean = true) = viewModelScope.launch {
        commonTask.loadOrError(showLoading = !isReloading) {
            tasksRepository.getCommonTask(commonTaskId, commonTaskType).also {

                suspend fun MutableResultFlow<List<User>>.loadUsersFromIds(ids: List<Long>) =
                    loadOrError(showLoading = false) {
                        coroutineScope {
                            ids.map {
                                async { usersRepository.getUser(it) }
                            }.awaitAll()
                        }
                    }

                val jobsToLoad = arrayOf(
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
                    },
                    launch {
                        tags.loadOrError(showLoading = false) {
                            tasksRepository.getAllTags(commonTaskType).also { tagsSearched.value = it }
                        }
                    }
                ) + if (!isReloading) {
                    arrayOf(
                        launch {
                            team.loadOrError(showLoading = false) {
                                usersRepository.getTeam()
                                    .map { it.toUser() }
                                    .also { teamSearched.value = it }
                            }
                        },
                        launch {
                            swimlanes.loadOrError(showLoading = false) {
                                listOf(SWIMLANE_HEADER) + tasksRepository.getSwimlanes() // prepend "unclassified"
                            }
                        },
                        launch {
                            statuses.loadOrError(showLoading = false) {
                                StatusType.values().filter {
                                    if (commonTaskType != CommonTaskType.Issue) it == StatusType.Status else true
                                }.associateWith { tasksRepository.getStatusByType(commonTaskType, it) }
                            }
                        }
                    )
                } else {
                    emptyArray()
                }

                joinAll(*jobsToLoad)
            }
        }
    }

    /**
     * Edit related stuff
     */

    // Edit status (and also type, severity, priority)
    val statusSelectResult = MutableResultFlow<StatusType>()

    fun selectStatus(status: Status) = viewModelScope.launch {
        statusSelectResult.value = LoadingResult(status.type)

        statusSelectResult.loadOrError(R.string.permission_error) {
            tasksRepository.changeStatus(commonTaskId, commonTaskType, status.id, status.type, commonTaskVersion.value)
            loadData().join()
            session.taskEdit.postUpdate()
            status.type
        }
    }

    // Edit sprint
    val sprints by lazy {
        Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
            CommonPagingSource { sprintsRepository.getSprints(it) }
        }.flow.map { it.insertHeaderItem(item = SPRINT_HEADER) } // prepend "Move to backlog"
            .asLazyPagingItems(viewModelScope)
    }

    val selectSprintResult = MutableResultFlow<Unit>(NothingResult())

    fun selectSprint(sprint: Sprint) = viewModelScope.launch {
        selectSprintResult.loadOrError(R.string.permission_error) {
            tasksRepository.changeSprint(commonTaskId, commonTaskType, sprint.takeIf { it != SPRINT_HEADER }?.id, commonTaskVersion.value)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }

    // Edit linked epic
    private val epicsQuery = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    val epics by lazy {
        epicsQuery.flatMapLatest { query ->
            Pager(PagingConfig(CommonPagingSource.PAGE_SIZE)) {
                CommonPagingSource { tasksRepository.getEpics(it, FiltersData(query = query)) }
            }.flow
        }.asLazyPagingItems(viewModelScope)
    }

    fun searchEpics(query: String) {
        epicsQuery.value = query
    }

    val linkToEpicResult = MutableResultFlow<Unit>(NothingResult())

    fun linkToEpic(epic: CommonTask) = viewModelScope.launch {
        linkToEpicResult.loadOrError(R.string.permission_error) {
            tasksRepository.linkToEpic(epic.id, commonTaskId)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }

    fun unlinkFromEpic(epic: EpicShortInfo) = viewModelScope.launch {
        linkToEpicResult.loadOrError(R.string.permission_error) {
            tasksRepository.unlinkFromEpic(epic.id, commonTaskId)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }

    // use team for both assignees and watchers
    val teamSearched = MutableStateFlow(emptyList<User>())

    fun searchTeam(query: String) = viewModelScope.launch {
        val q = query.lowercase()
        teamSearched.value = team.value.data
            .orEmpty()
            .filter { q in it.username.lowercase() || q in it.displayName.lowercase() }
    }

    // Edit assignees

    private fun changeAssignees(userId: Long, remove: Boolean) = viewModelScope.launch {
        assignees.loadOrError(R.string.permission_error) {
            teamSearched.value = team.value.data.orEmpty()

            tasksRepository.changeAssignees(
                commonTaskId, commonTaskType,
                commonTask.value.data?.assignedIds.orEmpty().let {
                    if (remove) it - userId
                    else it + userId
                },
                commonTaskVersion.value
            )

            loadData().join()
            session.taskEdit.postUpdate()
            assignees.value.data
        }
    }

    fun addAssignee(user: User) = changeAssignees(user.id, remove = false)
    fun addAssigneeById(userId: Long = session.currentUserId.value) = changeAssignees(userId, remove = false)
    fun removeAssignee(user: User) = changeAssignees(user.id, remove = true)
    fun removeAssigneeById(userId: Long = session.currentUserId.value) = changeAssignees(userId, remove = true)


    // Edit watchers

    private fun changeWatchers(userId: Long, remove: Boolean) = viewModelScope.launch {
        watchers.loadOrError(R.string.permission_error) {
            teamSearched.value = team.value.data.orEmpty()

            tasksRepository.changeWatchers(
                commonTaskId, commonTaskType,
                commonTask.value.data?.watcherIds.orEmpty().let {
                    if (remove) it - userId
                    else it + userId
                },
                commonTaskVersion.value
            )

            loadData().join()
            watchers.value.data
        }
    }

    fun addWatcher(user: User) = changeWatchers(user.id, remove = false)
    fun addWatcherById(userId: Long = session.currentUserId.value) = changeWatchers(userId, remove = false)
    fun removeWatcher(user: User) = changeWatchers(user.id, remove = true)
    fun removeWatcherById(userId: Long = session.currentUserId.value) = changeWatchers(userId, remove = true)

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
            session.taskEdit.postUpdate()
        }
    }

    // Delete task
    val deleteResult = MutableResultFlow<Unit>()

    fun deleteTask() = viewModelScope.launch {
        deleteResult.loadOrError(R.string.permission_error) {
            tasksRepository.deleteCommonTask(commonTaskType, commonTaskId)
            session.taskEdit.postUpdate()
        }
    }

    val promoteResult = MutableResultFlow<CommonTask>()

    fun promoteToUserStory() = viewModelScope.launch {
        promoteResult.loadOrError(R.string.permission_error, preserveValue = false) {
            tasksRepository.promoteCommonTaskToUserStory(commonTaskId, commonTaskType).also {
                session.taskEdit.postUpdate()
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
    val tagsSearched = MutableStateFlow(emptyList<Tag>())

    fun searchTags(query: String) = viewModelScope.launch {
        tagsSearched.value = tags.value.data.orEmpty().filter { query.isNotEmpty() && query.lowercase() in it.name }
    }

    private fun editTag(tag: Tag, remove: Boolean) = viewModelScope.launch {
        tags.loadOrError(R.string.permission_error) {
            tagsSearched.value = tags.value.data.orEmpty()

            tasksRepository.editTags(
                commonTaskType = commonTaskType,
                commonTaskId = commonTaskId,
                tags = commonTask.value.data?.tags.orEmpty()
                    .let { if (remove) it - tag else it + tag },
                version = commonTaskVersion.value
            )

            loadData().join()
            session.taskEdit.postUpdate()
            tags.value.data
        }
    }

    fun addTag(tag: Tag) = editTag(tag, remove = false)
    fun deleteTag(tag: Tag) = editTag(tag, remove = true)

    fun selectSwimlane(swimlane: Swimlane) = viewModelScope.launch {
        swimlanes.loadOrError(R.string.permission_error) {
            tasksRepository.changeUserStorySwimlane(commonTaskId, swimlane.takeIf { it != SWIMLANE_HEADER }?.id, commonTaskVersion.value)
            loadData().join()
            session.taskEdit.postUpdate()
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

    // Epic color
    val colorResult = MutableResultFlow<Unit>()

    fun selectEpicColor(color: String) = viewModelScope.launch {
        colorResult.loadOrError(R.string.permission_error) {
            tasksRepository.changeEpicColor(commonTaskId, color, commonTaskVersion.value)
            loadData().join()
            session.taskEdit.postUpdate()
        }
    }
}
