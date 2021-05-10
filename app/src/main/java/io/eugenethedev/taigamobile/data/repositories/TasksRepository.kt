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
        CommonTaskType.UserStory -> taigaApi.getUserStoriesFiltersData(session.currentProjectId)
        CommonTaskType.Task -> taigaApi.getTasksFiltersData(session.currentProjectId)
        CommonTaskType.Epic -> taigaApi.getEpicsFiltersData(session.currentProjectId)
        CommonTaskType.Issue -> taigaApi.getIssuesFiltersData(session.currentProjectId)
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
                .map { it.toCommonTask(CommonTaskType.Epic) }
        }

        val stories = async {
            taigaApi.getUserStories(assignedId = session.currentUserId, isClosed = false, isDashboard = true)
                .map { it.toCommonTask(CommonTaskType.UserStory) }
        }

        val tasks = async {
            taigaApi.getTasks(assignedId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Task) }
        }

        val issues = async {
            taigaApi.getIssues(assignedId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Issue) }
        }

        epics.await() + stories.await() + tasks.await() + issues.await()
    }

    override suspend fun getWatching() = withIO {
        val epics = async {
            taigaApi.getEpics(watcherId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Epic) }
        }

        val stories = async {
            taigaApi.getUserStories(watcherId = session.currentUserId, isClosed = false, isDashboard = true)
                .map { it.toCommonTask(CommonTaskType.UserStory) }
        }

        val tasks = async {
            taigaApi.getTasks(watcherId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Task) }
        }

        val issues = async {
            taigaApi.getIssues(watcherId = session.currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Issue) }
        }

        epics.await() + stories.await() + tasks.await() + issues.await()
    }

    override suspend fun getStatuses(commonTaskType: CommonTaskType) = withIO {
        getFiltersData(commonTaskType).statuses.map { it.toStatus(StatusType.Status) }
    }

    override suspend fun getStatusByType(commonTaskType: CommonTaskType, statusType: StatusType) = withIO {
        if (commonTaskType != CommonTaskType.Issue && statusType != StatusType.Status) {
            throw UnsupportedOperationException("Cannot change $statusType for $commonTaskType")
        }

        getFiltersData(commonTaskType).let {
            when (statusType) {
                StatusType.Status -> it.statuses.map { it.toStatus(statusType) }
                StatusType.Type -> it.types.orEmpty().map { it.toStatus(statusType) }
                StatusType.Severity -> it.severities.orEmpty().map { it.toStatus(statusType) }
                StatusType.Priority -> it.priorities.orEmpty().map { it.toStatus(statusType) }
            }
        }
    }

    override suspend fun getEpics(page: Int, query: String?) = withIO {
        handle404 {
            taigaApi.getEpics(page = page, project = session.currentProjectId, query = query).map { it.toCommonTask(CommonTaskType.Epic) }
        }
    }

    override suspend fun getBacklogUserStories(page: Int, query: String) = withIO {
        handle404 {
            taigaApi.getUserStories(session.currentProjectId, sprint = "null", page = page, query = query)
                .map { it.toCommonTask(CommonTaskType.UserStory) }
        }
    }

    override suspend fun getSprintUserStories(sprintId: Long) = withIO {
        taigaApi.getUserStories(project = session.currentProjectId, sprint = sprintId)
            .map { it.toCommonTask(CommonTaskType.UserStory) }
    }

    override suspend fun getEpicUserStories(epicId: Long) = withIO {
        taigaApi.getUserStories(epic = epicId)
            .map { it.toCommonTask(CommonTaskType.UserStory) }

    }

    override suspend fun getSprints(page: Int) = withIO {
        handle404 {
            taigaApi.getSprints(session.currentProjectId, page).map { it.toSprint() }
        }
    }

    override suspend fun getSprintTasks(sprintId: Long) = withIO {
        taigaApi.getTasks(userStory = "null", project = session.currentProjectId, sprint = sprintId)
            .map { it.toCommonTask(CommonTaskType.Task) }
    }

    override suspend fun getUserStoryTasks(storyId: Long) = withIO {
        handle404 {
            taigaApi.getTasks(userStory = storyId, project = session.currentProjectId)
                .map { it.toCommonTask(CommonTaskType.Task) }
        }
    }

    override suspend fun getIssues(page: Int, query: String) = withIO {
        handle404 {
            taigaApi.getIssues(page = page, project = session.currentProjectId, query = query)
                .map { it.toCommonTask(CommonTaskType.Issue) }
        }
    }

    override suspend fun getSprintIssues(sprintId: Long) = withIO {
        taigaApi.getIssues(project = session.currentProjectId, sprint = sprintId)
            .map { it.toCommonTask(CommonTaskType.Issue) }

    }

    override suspend fun getCommonTask(commonTaskId: Long, type: CommonTaskType) = withIO {
        val filters = getFiltersData(type)

        when (type) {
            CommonTaskType.UserStory -> taigaApi.getUserStory(commonTaskId)
            CommonTaskType.Task -> taigaApi.getTask(commonTaskId)
            CommonTaskType.Epic -> taigaApi.getEpic(commonTaskId)
            CommonTaskType.Issue -> taigaApi.getIssue(commonTaskId)
        }.let {
            CommonTaskExtended(
                id = it.id,
                status = Status(
                    id = it.status,
                    name = it.status_extra_info.name,
                    color = it.status_extra_info.color,
                    type = StatusType.Status
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
                userStoryShortInfo = it.user_story_extra_info,
                version = it.version,
                color = it.color,
                type = it.type?.let { id -> filters.types?.find { it.id == id } }?.toStatus(StatusType.Type),
                severity = it.severity?.let { id -> filters.severities?.find { it.id == id } }?.toStatus(StatusType.Severity),
                priority = it.priority?.let { id -> filters.priorities?.find { it.id == id } }?.toStatus(StatusType.Priority)
            )
        }
    }

    override suspend fun getComments(commonTaskId: Long, type: CommonTaskType) = withIO {
        when (type) {
            CommonTaskType.UserStory -> taigaApi.getUserStoryComments(commonTaskId)
            CommonTaskType.Task -> taigaApi.getTaskComments(commonTaskId)
            CommonTaskType.Epic -> taigaApi.getEpicComments(commonTaskId)
            CommonTaskType.Issue -> taigaApi.getIssueComments(commonTaskId)
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
            type = StatusType.Status
        ),
        assignee = assigned_to_extra_info,
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
        if (commonTaskType != CommonTaskType.Issue && statusType != StatusType.Status) {
            throw UnsupportedOperationException("Cannot change $statusType for $commonTaskType")
        }

        val body = ChangeStatusRequest(statusId, version)
        when (commonTaskType) {
            CommonTaskType.UserStory -> taigaApi.changeUserStoryStatus(commonTaskId, body)
            CommonTaskType.Task -> taigaApi.changeTaskStatus(commonTaskId, body)
            CommonTaskType.Epic -> taigaApi.changeEpicStatus(commonTaskId, body)
            CommonTaskType.Issue -> when (statusType) {
                StatusType.Status -> taigaApi.changeIssueStatus(commonTaskId, body)
                StatusType.Type -> taigaApi.changeIssueType(
                    id = commonTaskId,
                    changeTypeRequest = ChangeTypeRequest(statusId, version)
                )
                StatusType.Severity -> taigaApi.changeIssueSeverity(
                    id = commonTaskId,
                    changeSeverityRequest = ChangeSeverityRequest(statusId, version)
                )
                StatusType.Priority -> taigaApi.changeIssuePriority(
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
            CommonTaskType.UserStory -> taigaApi.changeUserStorySprint(commonTaskId, body)
            CommonTaskType.Task -> taigaApi.changeTaskSprint(commonTaskId, body)
            CommonTaskType.Issue -> taigaApi.changeIssueSprint(commonTaskId, body)
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
            CommonTaskType.UserStory -> taigaApi.changeUserStoryAssignees(
                id = commonTaskId,
                changeAssigneesRequest = ChangeUserStoryAssigneesRequest(assignees.firstOrNull(), assignees, version)
            )
            CommonTaskType.Task -> taigaApi.changeTaskAssignees(commonTaskId, body)
            CommonTaskType.Epic -> taigaApi.changeEpicAssignees(commonTaskId, body)
            CommonTaskType.Issue -> taigaApi.changeIssueAssignees(commonTaskId, body)
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
            CommonTaskType.UserStory -> taigaApi.changeUserStoryWatchers(commonTaskId, body)
            CommonTaskType.Task -> taigaApi.changeTaskWatchers(commonTaskId, body)
            CommonTaskType.Epic -> taigaApi.changeEpicWatchers(commonTaskId, body)
            CommonTaskType.Issue -> taigaApi.changeIssuesWatchers(commonTaskId, body)
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
            CommonTaskType.UserStory -> taigaApi.createUserStoryComment(commonTaskId, body)
            CommonTaskType.Task -> taigaApi.createTaskComment(commonTaskId, body)
            CommonTaskType.Epic -> taigaApi.createEpicComment(commonTaskId, body)
            CommonTaskType.Issue -> taigaApi.createIssueComment(commonTaskId, body)
        }
    }

    override suspend fun deleteComment(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        commentId: String
    ) = withIO {
        when (commonTaskType) {
            CommonTaskType.UserStory -> taigaApi.deleteUserStoryComment(commonTaskId, commentId)
            CommonTaskType.Task -> taigaApi.deleteTaskComment(commonTaskId, commentId)
            CommonTaskType.Epic -> taigaApi.deleteEpicComment(commonTaskId, commentId)
            CommonTaskType.Issue -> taigaApi.deleteIssueComment(commonTaskId, commentId)
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
            CommonTaskType.UserStory -> taigaApi.editUserStory(commonTaskId, body)
            CommonTaskType.Task -> taigaApi.editTask(commonTaskId, body)
            CommonTaskType.Epic -> taigaApi.editEpic(commonTaskId, body)
            CommonTaskType.Issue -> taigaApi.editIssue(commonTaskId, body)
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
            CommonTaskType.Task -> taigaApi.createTask(
                createTaskRequest = CreateTaskRequest(session.currentProjectId, title, description, sprintId, parentId)
            )
            CommonTaskType.UserStory -> taigaApi.createUserStory(body)
            CommonTaskType.Epic -> taigaApi.createEpic(body)
            CommonTaskType.Issue -> taigaApi.createIssue(
                createIssueRequest = CreateIssueRequest(session.currentProjectId, title, description, sprintId)
            )
        }.toCommonTask(commonTaskType)
    }

    override suspend fun deleteCommonTask(commonTaskType: CommonTaskType, commonTaskId: Long) = withIO {
        when (commonTaskType) {
            CommonTaskType.UserStory -> taigaApi.deleteUserStory(commonTaskId)
            CommonTaskType.Task -> taigaApi.deleteTask(commonTaskId)
            CommonTaskType.Epic -> taigaApi.deleteEpic(commonTaskId)
            CommonTaskType.Issue -> taigaApi.deleteIssue(commonTaskId)
        }
        return@withIO
    }

    override suspend fun promoteCommonTaskToUserStory(commonTaskId: Long, commonTaskType: CommonTaskType) = withIO {
        val body = PromoteToUserStoryRequest(session.currentProjectId)
        when (commonTaskType) {
            CommonTaskType.Task -> taigaApi.promoteTaskToUserStory(commonTaskId, body)
            CommonTaskType.Issue -> taigaApi.promoteIssueToUserStory(commonTaskId, body)
            else -> throw UnsupportedOperationException("Cannot promote to user story $commonTaskType")
        }.first()
         .let { taigaApi.getUserStoryByRef(session.currentProjectId, it).toCommonTask(CommonTaskType.UserStory) }
    }
}