package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.data.api.*
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import retrofit2.HttpException
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : ITasksRepository {

    override suspend fun getStatuses(commonTaskType: CommonTaskType) = withIO {
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.getUserStoriesFiltersData(session.currentProjectId)
            CommonTaskType.TASK -> taigaApi.getTasksFiltersData(session.currentProjectId)
            CommonTaskType.EPIC -> taigaApi.getEpicsFiltersData(session.currentProjectId)
            CommonTaskType.ISSUE -> taigaApi.getIssuesFiltersData(session.currentProjectId)
        }.statuses
    }

    override suspend fun getEpics(page: Int, query: String?) = withIO {
        handle404 {
            taigaApi.getEpics(session.currentProjectId, page, query).map { it.toCommonTask(CommonTaskType.EPIC) }
        }
    }

    override suspend fun getUserStories(statusId: Long, page: Int, sprintId: Long?) = withIO {
        handle404 {
            taigaApi.getUserStories(session.currentProjectId, sprintId ?: "null", statusId, null, page)
                .map { it.toCommonTask(CommonTaskType.USERSTORY) }
        }
    }

    override suspend fun getEpicUserStories(epicId: Long) = withIO {
        handle404 {
            taigaApi.getUserStories(null, null, null, epicId, null)
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
            taigaApi.getTasks(session.currentProjectId, sprintId, "null", page)
                .map { it.toCommonTask(CommonTaskType.TASK) }
        }
    }

    override suspend fun getUserStoryTasks(storyId: Long) = withIO {
        handle404 {
            taigaApi.getTasks(session.currentProjectId, null, storyId, null)
                .map { it.toCommonTask(CommonTaskType.TASK) }
        }
    }

    override suspend fun getIssues(page: Int, query: String) = withIO {
        handle404 {
            taigaApi.getIssues(session.currentProjectId, page, query, null)
                .map { it.toCommonTask(CommonTaskType.ISSUE) }
        }
    }

    override suspend fun getSprintIssues(sprintId: Long, page: Int) = withIO {
        handle404 {
            taigaApi.getIssues(session.currentProjectId, page, null, sprintId)
                .map { it.toCommonTask(CommonTaskType.ISSUE) }
        }
    }

    override suspend fun getCommonTask(commonTaskId: Long, type: CommonTaskType) = withIO {
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
                    color = it.status_extra_info.color
                ),
                createdDateTime = it.created_date,
                sprint = it.milestone?.let { taigaApi.getSprint(it).toSprint() },
                assignedIds = it.assigned_users ?: listOf(it.assigned_to),
                watcherIds = it.watchers,
                creatorId = it.owner,
                ref = it.ref,
                title = it.subject,
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
                color = it.color
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
            color = status_extra_info.color
        ),
        assignee = assigned_to_extra_info?.let {
            CommonTask.Assignee(
                id = it.id,
                fullName = it.full_name_display
            )
        },
        projectSlug = project_extra_info.slug,
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
        version: Int
    ) = withIO {
        val body = ChangeStatusRequest(statusId, version)
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.changeUserStoryStatus(commonTaskId, body)
            CommonTaskType.TASK -> taigaApi.changeTaskStatus(commonTaskId, body)
            CommonTaskType.EPIC -> taigaApi.changeEpicStatus(commonTaskId, body)
            CommonTaskType.ISSUE -> taigaApi.changeIssueStatus(commonTaskId, body)
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

    override suspend fun promoteTaskToUserStory(commonTaskId: Long) = withIO {
        taigaApi.promoteTaskToUserStory(
                taskId = commonTaskId,
                promoteToUserStoryRequest = PromoteToUserStoryRequest(session.currentProjectId)
            )
            .first()
            .let { taigaApi.getUserStoryByRef(session.currentProjectId, it).toCommonTask(CommonTaskType.USERSTORY) }
    }
}