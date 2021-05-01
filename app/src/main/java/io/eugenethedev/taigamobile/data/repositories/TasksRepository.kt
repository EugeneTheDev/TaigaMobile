package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.data.api.*
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import kotlinx.coroutines.async
import retrofit2.HttpException
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : ITasksRepository {

    private suspend fun getFiltersData(commonTaskType: CommonTaskType) = when (commonTaskType) {
        CommonTaskType.USERSTORY -> taigaApi.getUserStoriesFiltersData(session.currentProjectId)
        CommonTaskType.TASK -> taigaApi.getTasksFiltersData(session.currentProjectId)
        CommonTaskType.EPIC -> taigaApi.getEpicsFiltersData(session.currentProjectId)
        CommonTaskType.ISSUE -> taigaApi.getIssuesFiltersData(session.currentProjectId)
    }

    private fun FiltersDataResponse.Filter.toStatus(statusType: StatusType) = Status(
        id = id,
        name = name,
        color = color,
        type = statusType
    )

    override suspend fun getWorkingOn() = withIO {
        val epics = async {
            taigaApi.getEpics(assignedId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.EPIC) }
        }

        val stories = async {
            taigaApi.getUserStories(assignedId = session.currentUserId, isClosed = false, isDashboard = true)
                .map { it.toCommonTask(CommonTaskType.USERSTORY) }
        }

        val tasks = async {
            taigaApi.getTasks(assignedId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.TASK) }
        }

        val issues = async {
            taigaApi.getIssues(assignedId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.ISSUE) }
        }

        epics.await() + stories.await() + tasks.await() + issues.await()
    }

    override suspend fun getWatching() = withIO {
        val epics = async {
            taigaApi.getEpics(watcherId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.EPIC) }
        }

        val stories = async {
            taigaApi.getUserStories(watcherId = session.currentUserId, isClosed = false, isDashboard = true)
                .map { it.toCommonTask(CommonTaskType.USERSTORY) }
        }

        val tasks = async {
            taigaApi.getTasks(watcherId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.TASK) }
        }

        val issues = async {
            taigaApi.getIssues(watcherId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.ISSUE) }
        }

        epics.await() + stories.await() + tasks.await() + issues.await()
    }

    override suspend fun getStatuses(commonTaskType: CommonTaskType) = withIO {
        getFiltersData(commonTaskType).statuses.map { it.toStatus(StatusType.STATUS) }
    }

    override suspend fun getStatusByType(commonTaskType: CommonTaskType, statusType: StatusType) = withIO {
        if (commonTaskType != CommonTaskType.ISSUE && statusType != StatusType.STATUS) {
            throw UnsupportedOperationException("Cannot change $statusType for $commonTaskType")
        }

        getFiltersData(commonTaskType).let {
            when (statusType) {
                StatusType.STATUS -> it.statuses.map { it.toStatus(statusType) }
                StatusType.TYPE -> it.types.orEmpty().map { it.toStatus(statusType) }
                StatusType.SEVERITY -> it.severities.orEmpty().map { it.toStatus(statusType) }
                StatusType.PRIORITY -> it.priorities.orEmpty().map { it.toStatus(statusType) }
            }
        }
    }

    override suspend fun getEpics(page: Int, query: String?) = withIO {
        handle404 {
            taigaApi.getEpics(page = page, project = session.currentProjectId, query = query).map { it.toCommonTask(CommonTaskType.EPIC) }
        }
    }

    override suspend fun getUserStories(statusId: Long, page: Int, sprintId: Long?) = withIO {
        handle404 {
            taigaApi.getUserStories(session.currentProjectId, sprintId ?: "null", statusId, page = page)
                .map { it.toCommonTask(CommonTaskType.USERSTORY) }
        }
    }

    override suspend fun getEpicUserStories(epicId: Long) = withIO {
        handle404 {
            taigaApi.getUserStories(epic = epicId)
                .map { it.toCommonTask(CommonTaskType.USERSTORY) }
        }
    }

    override suspend fun getSprints(page: Int) = withIO {
        handle404 {
            taigaApi.getSprints(session.currentProjectId, page).map { it.toSprint() }
        }
    }

    override suspend fun getSprintTasks(sprintId: Long, page: Int) = withIO {
        handle404 {
            taigaApi.getTasks(userStory = "null", project = session.currentProjectId, sprint = sprintId, page = page)
                .map { it.toCommonTask(CommonTaskType.TASK) }
        }
    }

    override suspend fun getUserStoryTasks(storyId: Long) = withIO {
        handle404 {
            taigaApi.getTasks(userStory = storyId, project = session.currentProjectId)
                .map { it.toCommonTask(CommonTaskType.TASK) }
        }
    }

    override suspend fun getIssues(page: Int, query: String) = withIO {
        handle404 {
            taigaApi.getIssues(page = page, project = session.currentProjectId, query = query)
                .map { it.toCommonTask(CommonTaskType.ISSUE) }
        }
    }

    override suspend fun getSprintIssues(sprintId: Long, page: Int) = withIO {
        handle404 {
            taigaApi.getIssues(page = page, project = session.currentProjectId, sprint = sprintId)
                .map { it.toCommonTask(CommonTaskType.ISSUE) }
        }
    }

    override suspend fun getCommonTask(commonTaskId: Long, type: CommonTaskType) = withIO {
        val filters = getFiltersData(type)

        when (type) {
            CommonTaskType.USERSTORY -> taigaApi.getUserStory(commonTaskId)
            CommonTaskType.TASK -> taigaApi.getTask(commonTaskId)
            CommonTaskType.EPIC -> taigaApi.getEpic(commonTaskId)
            CommonTaskType.ISSUE -> taigaApi.getIssue(commonTaskId)
        }.let {
            CommonTaskExtended(
                id = it.id,
                status = Status(
                    id = it.status,
                    name = it.status_extra_info.name,
                    color = it.status_extra_info.color,
                    type = StatusType.STATUS
                ),
                createdDateTime = it.created_date,
                sprint = it.milestone?.let { taigaApi.getSprint(it).toSprint() },
                assignedIds = it.assigned_users ?: listOf(it.assigned_to),
                watcherIds = it.watchers,
                creatorId = it.owner,
                ref = it.ref,
                title = it.subject,
                isClosed = it.is_closed,
                description = it.description,
                epicsShortInfo = it.epics.orEmpty(),
                projectSlug = it.project_extra_info.slug,
                userStoryShortInfo = it.user_story_extra_info?.let {
                    UserStoryShortInfo(
                        id = it.id,
                        ref = it.ref,
                        title = it.subject,
                        epicColors = it.epics.orEmpty().map { it.color }
                    )
                },
                version = it.version,
                color = it.color,
                type = it.type?.let { id -> filters.types?.find { it.id == id } }?.toStatus(StatusType.TYPE),
                severity = it.severity?.let { id -> filters.severities?.find { it.id == id } }?.toStatus(StatusType.SEVERITY),
                priority = it.priority?.let { id -> filters.priorities?.find { it.id == id } }?.toStatus(StatusType.PRIORITY)
            )
        }
    }

    override suspend fun getComments(commonTaskId: Long, type: CommonTaskType) = withIO {
        when (type) {
            CommonTaskType.USERSTORY -> taigaApi.getUserStoryComments(commonTaskId)
            CommonTaskType.TASK -> taigaApi.getTaskComments(commonTaskId)
            CommonTaskType.EPIC -> taigaApi.getEpicComments(commonTaskId)
            CommonTaskType.ISSUE -> taigaApi.getIssueComments(commonTaskId)
        }.sortedBy { it.postDateTime }
    }


    private fun CommonTaskResponse.toCommonTask(commonTaskType: CommonTaskType) = CommonTask(
        id = id,
        createdDate = created_date,
        title = subject,
        ref = ref,
        status = Status(
            id = status,
            name = status_extra_info.name,
            color = status_extra_info.color,
            type = StatusType.STATUS
        ),
        assignee = assigned_to_extra_info?.let {
            CommonTask.Assignee(
                id = it.id,
                fullName = it.full_name_display
            )
        },
        projectInfo = project_extra_info,
        taskType = commonTaskType,
        colors = color?.let { listOf(it) } ?: epics.orEmpty().map { it.color },
        isClosed = is_closed
    )
    
    private fun SprintResponse.toSprint() = Sprint(
        id = id,
        name = name,
        order = order,
        start = estimated_start,
        finish = estimated_finish,
        storiesCount = user_stories.size,
        isClosed = closed
    )

    private suspend fun <T> handle404(action: suspend () -> List<T>): List<T> = try {
        action()
    } catch (e: HttpException) {
        // suppress error if page not found (maximum page was reached)
        e.takeIf { it.code() == 404 }?.let { emptyList() } ?: throw e
    }


    override suspend fun changeStatus(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        statusId: Long,
        statusType: StatusType,
        version: Int
    ) = withIO {
        if (commonTaskType != CommonTaskType.ISSUE && statusType != StatusType.STATUS) {
            throw UnsupportedOperationException("Cannot change $statusType for $commonTaskType")
        }

        val body = ChangeStatusRequest(statusId, version)
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.changeUserStoryStatus(commonTaskId, body)
            CommonTaskType.TASK -> taigaApi.changeTaskStatus(commonTaskId, body)
            CommonTaskType.EPIC -> taigaApi.changeEpicStatus(commonTaskId, body)
            CommonTaskType.ISSUE -> when (statusType) {
                StatusType.STATUS -> taigaApi.changeIssueStatus(commonTaskId, body)
                StatusType.TYPE -> taigaApi.changeIssueType(
                    id = commonTaskId,
                    changeTypeRequest = ChangeTypeRequest(statusId, version)
                )
                StatusType.SEVERITY -> taigaApi.changeIssueSeverity(
                    id = commonTaskId,
                    changeSeverityRequest = ChangeSeverityRequest(statusId, version)
                )
                StatusType.PRIORITY -> taigaApi.changeIssuePriority(
                    id = commonTaskId,
                    changePriorityRequest = ChangePriorityRequest(statusId, version)
                )
            }
        }
    }

    override suspend fun changeSprint(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        sprintId: Long?,
        version: Int
    ) = withIO {
        val body = ChangeSprintRequest(sprintId, version)
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.changeUserStorySprint(commonTaskId, body)
            CommonTaskType.TASK -> taigaApi.changeTaskSprint(commonTaskId, body)
            CommonTaskType.ISSUE -> taigaApi.changeIssueSprint(commonTaskId, body)
            else -> throw UnsupportedOperationException("Cannot change sprint for $commonTaskType")
        }
    }

    override suspend fun linkToEpic(epicId: Long, userStoryId: Long) = withIO {
        taigaApi.linkToEpic(
            epicId = epicId,
            linkToEpicRequest = LinkToEpicRequest(epicId.toString(), userStoryId)
        )
    }

    override suspend fun unlinkFromEpic(epicId: Long, userStoryId: Long) = withIO {
        taigaApi.unlinkFromEpic(epicId, userStoryId)
        return@withIO
    }

    override suspend fun changeAssignees(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        assignees: List<Long>,
        version: Int
    ) = withIO {
        val body = ChangeCommonTaskAssigneesRequest(assignees.lastOrNull(), version)
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.changeUserStoryAssignees(
                id = commonTaskId,
                changeAssigneesRequest = ChangeUserStoryAssigneesRequest(assignees.firstOrNull(), assignees, version)
            )
            CommonTaskType.TASK -> taigaApi.changeTaskAssignees(commonTaskId, body)
            CommonTaskType.EPIC -> taigaApi.changeEpicAssignees(commonTaskId, body)
            CommonTaskType.ISSUE -> taigaApi.changeIssueAssignees(commonTaskId, body)
        }
    }

    override suspend fun changeWatchers(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        watchers: List<Long>,
        version: Int
    ) = withIO {
        val body = ChangeWatchersRequest(watchers, version)
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.changeUserStoryWatchers(commonTaskId, body)
            CommonTaskType.TASK -> taigaApi.changeTaskWatchers(commonTaskId, body)
            CommonTaskType.EPIC -> taigaApi.changeEpicWatchers(commonTaskId, body)
            CommonTaskType.ISSUE -> taigaApi.changeIssuesWatchers(commonTaskId, body)
        }
    }

    override suspend fun createComment(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        comment: String,
        version: Int
    ) = withIO {
        val body = CreateCommentRequest(comment, version)
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.createUserStoryComment(commonTaskId, body)
            CommonTaskType.TASK -> taigaApi.createTaskComment(commonTaskId, body)
            CommonTaskType.EPIC -> taigaApi.createEpicComment(commonTaskId, body)
            CommonTaskType.ISSUE -> taigaApi.createIssueComment(commonTaskId, body)
        }
    }

    override suspend fun deleteComment(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        commentId: String
    ) = withIO {
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.deleteUserStoryComment(commonTaskId, commentId)
            CommonTaskType.TASK -> taigaApi.deleteTaskComment(commonTaskId, commentId)
            CommonTaskType.EPIC -> taigaApi.deleteEpicComment(commonTaskId, commentId)
            CommonTaskType.ISSUE -> taigaApi.deleteIssueComment(commonTaskId, commentId)
        }
    }

    override suspend fun editTask(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        version: Int
    ) = withIO {
        val body = EditCommonTaskRequest(title, description, version)
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.editUserStory(commonTaskId, body)
            CommonTaskType.TASK -> taigaApi.editTask(commonTaskId, body)
            CommonTaskType.EPIC -> taigaApi.editEpic(commonTaskId, body)
            CommonTaskType.ISSUE -> taigaApi.editIssue(commonTaskId, body)
        }
    }


    override suspend fun createCommonTask(
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        parentId: Long?,
        sprintId: Long?
    ) = withIO {
        val body = CreateCommonTaskRequest(session.currentProjectId, title, description)
        when (commonTaskType) {
            CommonTaskType.TASK -> taigaApi.createTask(
                createTaskRequest = CreateTaskRequest(session.currentProjectId, title, description, sprintId, parentId)
            )
            CommonTaskType.USERSTORY -> taigaApi.createUserStory(body)
            CommonTaskType.EPIC -> taigaApi.createEpic(body)
            CommonTaskType.ISSUE -> taigaApi.createIssue(
                createIssueRequest = CreateIssueRequest(session.currentProjectId, title, description, sprintId)
            )
        }.toCommonTask(commonTaskType)
    }

    override suspend fun deleteCommonTask(commonTaskType: CommonTaskType, commonTaskId: Long) = withIO {
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.deleteUserStory(commonTaskId)
            CommonTaskType.TASK -> taigaApi.deleteTask(commonTaskId)
            CommonTaskType.EPIC -> taigaApi.deleteEpic(commonTaskId)
            CommonTaskType.ISSUE -> taigaApi.deleteIssue(commonTaskId)
        }
        return@withIO
    }

    override suspend fun promoteCommonTaskToUserStory(commonTaskId: Long, commonTaskType: CommonTaskType) = withIO {
        val body = PromoteToUserStoryRequest(session.currentProjectId)
        when (commonTaskType) {
            CommonTaskType.TASK -> taigaApi.promoteTaskToUserStory(commonTaskId, body)
            CommonTaskType.ISSUE -> taigaApi.promoteIssueToUserStory(commonTaskId, body)
            else -> throw UnsupportedOperationException("Cannot promote to user story $commonTaskType")
        }.first()
         .let { taigaApi.getUserStoryByRef(session.currentProjectId, it).toCommonTask(CommonTaskType.USERSTORY) }
    }
}