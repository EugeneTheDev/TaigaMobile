package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.dagger.toLocalDate
import io.eugenethedev.taigamobile.data.api.*
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import kotlinx.coroutines.async
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.InputStream
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : ITasksRepository {

    private suspend fun getFiltersData(commonTaskType: CommonTaskType) =
        taigaApi.getCommonTaskFiltersData(CommonTaskPathPlural(commonTaskType), session.currentProjectId)

    private fun FiltersDataResponse.Filter.toStatus(statusType: StatusType) = Status(
        id = id,
        name = name,
        color = color.fixNullColor(),
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

    override suspend fun getAllUserStories() = withIO {
        val filters = async { getFiltersData(CommonTaskType.UserStory) }
        taigaApi.getUserStories(project = session.currentProjectId).map { it.toCommonTaskExtended(filters.await(), loadSprint = false) }
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
        val filters = async { getFiltersData(type) }
        taigaApi.getCommonTask(CommonTaskPathPlural(type), commonTaskId).toCommonTaskExtended(filters.await())
    }

    override suspend fun getComments(commonTaskId: Long, type: CommonTaskType) = withIO {
        taigaApi.getCommonTaskComments(CommonTaskPathSingular(type), commonTaskId).sortedBy { it.postDateTime }
    }

    override suspend fun getAttachments(commonTaskId: Long, type: CommonTaskType) = withIO {
        taigaApi.getCommonTaskAttachments(CommonTaskPathPlural(type), commonTaskId, session.currentProjectId)
    }

    override suspend fun getCustomFields(commonTaskId: Long, type: CommonTaskType) = withIO {
        val attributes = async { taigaApi.getCustomAttributes(CommonTaskPathSingular(type), session.currentProjectId) }
        val values = taigaApi.getCustomAttributesValues(CommonTaskPathPlural(type), commonTaskId)

        CustomFields(
            version = values.version,
            fields = attributes.await().sortedBy { it.order }
                .map {
                    CustomField(
                        id = it.id,
                        type = it.type,
                        name = it.name,
                        description = it.description?.takeIf { it.isNotEmpty() },
                        value = values.attributes_values[it.id]?.let { value ->
                            CustomFieldValue(
                                when (it.type) {
                                    CustomFieldType.Date -> (value as? String)?.takeIf { it.isNotEmpty() }?.toLocalDate()
                                    CustomFieldType.Number -> (value as? Double)?.toInt()
                                    CustomFieldType.Checkbox -> value as? Boolean
                                    else -> value
                                } ?: return@let null
                            )
                        },
                        options = it.extra.orEmpty()
                    )
            }
        )
    }

    override suspend fun getAllTags(commonTaskType: CommonTaskType) = withIO {
        getFiltersData(commonTaskType).tags.orEmpty().map { Tag(it.name, it.color.fixNullColor()) }
    }

    private fun String?.fixNullColor() = this ?: "#A9AABC" /* gray, because api returns null instead of gray -_- */

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
        isClosed = is_closed,
        tags = tags.orEmpty().map { Tag(name = it[0]!!, color = it[1].fixNullColor()) }
    )
    
    private suspend fun CommonTaskResponse.toCommonTaskExtended(filters: FiltersDataResponse, loadSprint: Boolean = true): CommonTaskExtended {
        return CommonTaskExtended(
            id = id,
            status = Status(
                id = status,
                name = status_extra_info.name,
                color = status_extra_info.color,
                type = StatusType.Status
            ),
            createdDateTime = created_date,
            sprint =  if (loadSprint) milestone?.let { taigaApi.getSprint(it).toSprint() } else null,
            assignedIds = assigned_users ?: listOf(assigned_to),
            watcherIds = watchers,
            creatorId = owner,
            ref = ref,
            title = subject,
            isClosed = is_closed,
            description = description ?: "",
            epicsShortInfo = epics.orEmpty(),
            projectSlug = project_extra_info.slug,
            tags = tags.orEmpty().map { Tag(name = it[0]!!, color = it[1].fixNullColor()) },
            userStoryShortInfo = user_story_extra_info,
            version = version,
            color = color,
            type = type?.let { id -> filters.types?.find { id == id } }?.toStatus(StatusType.Type),
            severity = severity?.let { id -> filters.severities?.find { id == id } }?.toStatus(StatusType.Severity),
            priority = priority?.let { id -> filters.priorities?.find { id == id } }?.toStatus(StatusType.Priority)
        )
    }
    
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

    // edit related

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
        when (statusType) {
            StatusType.Status -> taigaApi.changeCommonTaskStatus(
                taskPath = CommonTaskPathPlural(commonTaskType),
                id = commonTaskId,
                changeStatusRequest = body
            )
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

    override suspend fun changeSprint(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        sprintId: Long?,
        version: Int
    ) = withIO {
        if (commonTaskType in listOf(CommonTaskType.Epic, CommonTaskType.Task)) {
            throw UnsupportedOperationException("Cannot change sprint for $commonTaskType")
        }

        taigaApi.changeCommonTaskSprint(
            taskPath = CommonTaskPathPlural(commonTaskType),
            id = commonTaskId,
            changeSprintRequest = ChangeSprintRequest(sprintId, version)
        )
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
        if (commonTaskType == CommonTaskType.UserStory) {
            taigaApi.changeUserStoryAssignees(
                id = commonTaskId,
                changeAssigneesRequest = ChangeUserStoryAssigneesRequest(assignees.firstOrNull(), assignees, version)
            )
        } else {
            taigaApi.changeCommonTaskAssignees(
                taskPath = CommonTaskPathPlural(commonTaskType),
                id = commonTaskId,
                changeAssigneesRequest = ChangeCommonTaskAssigneesRequest(assignees.lastOrNull(), version)
            )
        }
    }

    override suspend fun changeWatchers(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        watchers: List<Long>,
        version: Int
    ) = withIO {
        taigaApi.changeCommonTaskWatchers(
            taskPath = CommonTaskPathPlural(commonTaskType),
            id = commonTaskId,
            changeWatchersRequest = ChangeWatchersRequest(watchers, version)
        )
    }

    override suspend fun createComment(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        comment: String,
        version: Int
    ) = withIO {
        taigaApi.createCommonTaskComment(
            taskPath = CommonTaskPathPlural(commonTaskType),
            id = commonTaskId,
            createCommentRequest = CreateCommentRequest(comment, version)
        )
    }

    override suspend fun deleteComment(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        commentId: String
    ) = withIO {
        taigaApi.deleteCommonTaskComment(
            taskPath = CommonTaskPathSingular(commonTaskType),
            id = commonTaskId,
            commentId = commentId
        )
    }

    override suspend fun editCommonTask(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        version: Int
    ) = withIO {
        taigaApi.editCommonTask(
            taskPath = CommonTaskPathPlural(commonTaskType),
            id = commonTaskId,
            editRequest = EditCommonTaskRequest(title, description, version)
        )
    }


    override suspend fun createCommonTask(
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        parentId: Long?,
        sprintId: Long?,
        statusId: Long?
    ) = withIO {
        when (commonTaskType) {
            CommonTaskType.Task -> taigaApi.createTask(
                createTaskRequest = CreateTaskRequest(session.currentProjectId, title, description, sprintId, parentId)
            )
            CommonTaskType.Issue -> taigaApi.createIssue(
                createIssueRequest = CreateIssueRequest(session.currentProjectId, title, description, sprintId)
            )
            else -> taigaApi.createCommonTask(
                taskPath = CommonTaskPathPlural(commonTaskType),
                createRequest = CreateCommonTaskRequest(session.currentProjectId, title, description, statusId)
            )
        }.toCommonTask(commonTaskType)
    }

    override suspend fun deleteCommonTask(commonTaskType: CommonTaskType, commonTaskId: Long) = withIO {
        taigaApi.deleteCommonTask(
            taskPath = CommonTaskPathPlural(commonTaskType),
            id = commonTaskId
        )
        return@withIO
    }

    override suspend fun promoteCommonTaskToUserStory(commonTaskId: Long, commonTaskType: CommonTaskType) = withIO {
        if (commonTaskType in listOf(CommonTaskType.Epic, CommonTaskType.UserStory)) {
            throw UnsupportedOperationException("Cannot promote to user story $commonTaskType")
        }

        taigaApi.promoteCommonTaskToUserStory(
            taskPath = CommonTaskPathPlural(commonTaskType),
            taskId = commonTaskId,
            promoteToUserStoryRequest = PromoteToUserStoryRequest(session.currentProjectId)
        ).first()
         .let { taigaApi.getUserStoryByRef(session.currentProjectId, it).toCommonTask(CommonTaskType.UserStory) }
    }

    override suspend fun addAttachment(commonTaskId: Long, commonTaskType: CommonTaskType, fileName: String, inputStream: InputStream) = withIO {
        val file = MultipartBody.Part.createFormData(
            name = "attached_file",
            filename = fileName,
            body = inputStream.readBytes().toRequestBody("*/*".toMediaType())
        )
        val project = MultipartBody.Part.createFormData("project", session.currentProjectId.toString())
        val objectId = MultipartBody.Part.createFormData("object_id", commonTaskId.toString())

        taigaApi.uploadCommonTaskAttachment(
            taskPath = CommonTaskPathPlural(commonTaskType),
            file = file,
            project = project,
            objectId = objectId
        ).also { inputStream.close() }
    }

    override suspend fun deleteAttachment(commonTaskType: CommonTaskType, attachmentId: Long) = withIO {
        taigaApi.deleteCommonTaskAttachment(
            taskPath = CommonTaskPathPlural(commonTaskType),
            attachmentId = attachmentId
        )
        return@withIO
    }

    override suspend fun editCustomFields(
        commonTaskType: CommonTaskType,
        commonTaskId: Long,
        fields: Map<Long, CustomFieldValue?>,
        version: Int
    ) = withIO {
        taigaApi.editCustomAttributesValues(
            taskPath = CommonTaskPathPlural(commonTaskType),
            taskId = commonTaskId,
            editRequest = EditCustomAttributesValuesRequest(fields.mapValues { it.value?.value }, version)
        )
    }

    override suspend fun editTags(
        commonTaskType: CommonTaskType,
        commonTaskId: Long,
        tags: List<Tag>,
        version: Int
    ) = withIO {
        taigaApi.editTags(
            taskPath = CommonTaskPathPlural(commonTaskType),
            taskId = commonTaskId,
            editRequest = EditTagsRequest(tags = tags.map { listOf(it.name, it.color) }, version = version)
        )
    }
}