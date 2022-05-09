package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.dagger.toLocalDate
import io.eugenethedev.taigamobile.data.api.*
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import kotlinx.coroutines.async
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.time.LocalDate
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : ITasksRepository {
    private val currentProjectId get() = session.currentProjectId.value
    private val currentUserId get() = session.currentUserId.value

    private fun StatusesFilter.toStatus(statusType: StatusType) = Status(
        id = id,
        name = name,
        color = color,
        type = statusType
    )

    override suspend fun getFiltersData(commonTaskType: CommonTaskType, isCommonTaskFromBacklog: Boolean) = withIO {
        taigaApi.getCommonTaskFiltersData(
            taskPath = CommonTaskPathPlural(commonTaskType),
            project = currentProjectId,
            milestone = if (isCommonTaskFromBacklog) "null" else null
        ).let {
            FiltersData(
                assignees = it.assigned_to.map {
                    UsersFilter(
                        id = it.id,
                        name = it.full_name,
                        count = it.count
                    )
                },
                roles = it.roles.orEmpty().map {
                    RolesFilter(
                        id = it.id!!,
                        name = it.name!!,
                        count = it.count
                    )
                },
                tags = it.tags.orEmpty().map {
                    TagsFilter(
                        name = it.name!!,
                        color = it.color.fixNullColor(),
                        count = it.count
                    )
                },
                statuses = it.statuses.map {
                    StatusesFilter(
                        id = it.id!!,
                        color = it.color.fixNullColor(),
                        name = it.name!!,
                        count = it.count
                    )
                },
                createdBy = it.owners.map {
                    UsersFilter(
                        id = it.id!!,
                        name = it.full_name,
                        count = it.count
                    )
                },
                priorities = it.priorities.orEmpty().map {
                    StatusesFilter(
                        id = it.id!!,
                        color = it.color.fixNullColor(),
                        name = it.name!!,
                        count = it.count
                    )
                },
                severities = it.severities.orEmpty().map {
                    StatusesFilter(
                        id = it.id!!,
                        color = it.color.fixNullColor(),
                        name = it.name!!,
                        count = it.count
                    )
                },
                types = it.types.orEmpty().map {
                    StatusesFilter(
                        id = it.id!!,
                        color = it.color.fixNullColor(),
                        name = it.name!!,
                        count = it.count
                    )
                },
                epics = it.epics.orEmpty().map {
                    EpicsFilter(
                        id = it.id,
                        name = it.subject?.let { s -> "#${it.ref} $s" }.orEmpty(),
                        count = it.count
                    )
                }
            )
        }
    }

    override suspend fun getWorkingOn() = withIO {
        val epics = async {
            taigaApi.getEpics(assignedId = currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Epic) }
        }

        val stories = async {
            taigaApi.getUserStories(assignedId = currentUserId, isClosed = false, isDashboard = true)
                .map { it.toCommonTask(CommonTaskType.UserStory) }
        }

        val tasks = async {
            taigaApi.getTasks(assignedId = currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Task) }
        }

        val issues = async {
            taigaApi.getIssues(assignedIds = currentUserId.toString(), isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Issue) }
        }

        epics.await() + stories.await() + tasks.await() + issues.await()
    }

    override suspend fun getWatching() = withIO {
        val epics = async {
            taigaApi.getEpics(watcherId = currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Epic) }
        }

        val stories = async {
            taigaApi.getUserStories(watcherId = currentUserId, isClosed = false, isDashboard = true)
                .map { it.toCommonTask(CommonTaskType.UserStory) }
        }

        val tasks = async {
            taigaApi.getTasks(watcherId = currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Task) }
        }

        val issues = async {
            taigaApi.getIssues(watcherId = currentUserId, isClosed = false)
                .map { it.toCommonTask(CommonTaskType.Issue) }
        }

        epics.await() + stories.await() + tasks.await() + issues.await()
    }

    override suspend fun getStatuses(commonTaskType: CommonTaskType) = withIO {
        getFiltersData(commonTaskType).statuses.map { it.toStatus(StatusType.Status) }
    }

    override suspend fun getStatusByType(commonTaskType: CommonTaskType, statusType: StatusType) = withIO {
        if (commonTaskType != CommonTaskType.Issue && statusType != StatusType.Status) {
            throw UnsupportedOperationException("Cannot get $statusType for $commonTaskType")
        }

        getFiltersData(commonTaskType).let {
            when (statusType) {
                StatusType.Status -> it.statuses.map { it.toStatus(statusType) }
                StatusType.Type -> it.types.map { it.toStatus(statusType) }
                StatusType.Severity -> it.severities.map { it.toStatus(statusType) }
                StatusType.Priority -> it.priorities.map { it.toStatus(statusType) }
            }
        }
    }

    override suspend fun getEpics(page: Int, filters: FiltersData) = withIO {
        handle404 {
            taigaApi.getEpics(
                    page = page,
                    project = currentProjectId,
                    query = filters.query,
                    assignedIds = filters.assignees.commaString(),
                    ownerIds = filters.createdBy.commaString(),
                    statuses = filters.statuses.commaString(),
                    tags = filters.tags.tagsCommaString()
                )
                .map { it.toCommonTask(CommonTaskType.Epic) }
        }
    }

    override suspend fun getAllUserStories() = withIO {
        val filters = async { getFiltersData(CommonTaskType.UserStory) }
        val swimlanes = async { getSwimlanes() }

        taigaApi.getUserStories(project = currentProjectId)
            .map {
                it.toCommonTaskExtended(
                    commonTaskType = CommonTaskType.UserStory,
                    filters = filters.await(),
                    swimlanes = swimlanes.await(),
                    loadSprint = false
                )
            }
    }

    override suspend fun getBacklogUserStories(page: Int, filters: FiltersData) = withIO {
        handle404 {
            taigaApi.getUserStories(
                    project = currentProjectId,
                    sprint = "null",
                    page = page,
                    query = filters.query,
                    assignedIds = filters.assignees.commaString(),
                    ownerIds = filters.createdBy.commaString(),
                    roles = filters.roles.commaString(),
                    statuses = filters.statuses.commaString(),
                    epics = filters.epics.commaString(),
                    tags = filters.tags.tagsCommaString()
                )
                .map { it.toCommonTask(CommonTaskType.UserStory) }
        }
    }

    override suspend fun getEpicUserStories(epicId: Long) = withIO {
        taigaApi.getUserStories(epic = epicId)
            .map { it.toCommonTask(CommonTaskType.UserStory) }

    }

    override suspend fun getUserStoryTasks(storyId: Long) = withIO {
        handle404 {
            taigaApi.getTasks(userStory = storyId, project = currentProjectId)
                .map { it.toCommonTask(CommonTaskType.Task) }
        }
    }

    override suspend fun getIssues(page: Int, filters: FiltersData) = withIO {
        handle404 {
            taigaApi.getIssues(
                    page = page,
                    project = currentProjectId,
                    query = filters.query,
                    assignedIds = filters.assignees.commaString(),
                    ownerIds = filters.createdBy.commaString(),
                    priorities = filters.priorities.commaString(),
                    severities = filters.severities.commaString(),
                    types = filters.types.commaString(),
                    statuses = filters.statuses.commaString(),
                    roles = filters.roles.commaString(),
                    tags = filters.tags.tagsCommaString()
                )
                .map { it.toCommonTask(CommonTaskType.Issue) }
        }
    }

    private fun List<Filter>.commaString() = map { it.id }
        .joinToString(separator = ",")
        .takeIf { it.isNotEmpty() }

    private fun List<TagsFilter>.tagsCommaString() = joinToString(separator = ",") { it.name.replace(" ", "+") }
        .takeIf { it.isNotEmpty() }

    override suspend fun getCommonTask(commonTaskId: Long, type: CommonTaskType) = withIO {
        val filters = async { getFiltersData(type) }
        val swimlanes = async { getSwimlanes() }

        taigaApi.getCommonTask(CommonTaskPathPlural(type), commonTaskId).toCommonTaskExtended(
            commonTaskType = type,
            filters = filters.await(),
            swimlanes = swimlanes.await(),
        )
    }

    override suspend fun getComments(commonTaskId: Long, type: CommonTaskType) = withIO {
        taigaApi.getCommonTaskComments(CommonTaskPathSingular(type), commonTaskId)
            .sortedBy { it.postDateTime }
            .filter { it.deleteDate == null }
            .map { it.also { it.canDelete = it.author.id == currentUserId } }
    }

    override suspend fun getAttachments(commonTaskId: Long, type: CommonTaskType) = withIO {
        taigaApi.getCommonTaskAttachments(CommonTaskPathPlural(type), commonTaskId, currentProjectId)
    }

    override suspend fun getCustomFields(commonTaskId: Long, type: CommonTaskType) = withIO {
        val attributes = async { taigaApi.getCustomAttributes(CommonTaskPathSingular(type), currentProjectId) }
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
        getFiltersData(commonTaskType).tags.map { Tag(it.name, it.color) }
    }

    override suspend fun getSwimlanes() = withIO {
        taigaApi.getSwimlanes(currentProjectId)
    }

    private fun transformTaskTypeForCopyLink(commonTaskType: CommonTaskType) = when (commonTaskType) {
            CommonTaskType.UserStory -> PATH_TO_USERSTORY
            CommonTaskType.Task -> PATH_TO_TASK
            CommonTaskType.Epic -> PATH_TO_EPIC
            CommonTaskType.Issue -> PATH_TO_ISSUE
        }

    private suspend fun CommonTaskResponse.toCommonTaskExtended(
        commonTaskType: CommonTaskType,
        filters: FiltersData,
        swimlanes: List<Swimlane>,
        loadSprint: Boolean = true
    ): CommonTaskExtended {
        return CommonTaskExtended(
            id = id,
            status = Status(
                id = status,
                name = status_extra_info.name,
                color = status_extra_info.color,
                type = StatusType.Status
            ),
            taskType = commonTaskType,
            createdDateTime = created_date,
            sprint =  if (loadSprint) milestone?.let { taigaApi.getSprint(it).toSprint() } else null,
            assignedIds = assigned_users ?: listOfNotNull(assigned_to),
            watcherIds = watchers.orEmpty(),
            creatorId = owner ?: throw IllegalArgumentException("CommonTaskResponse requires not null 'owner' field"),
            ref = ref,
            title = subject,
            isClosed = is_closed,
            description = description ?: "",
            epicsShortInfo = epics.orEmpty(),
            projectSlug = project_extra_info.slug,
            tags = tags.orEmpty().map { Tag(name = it[0]!!, color = it[1].fixNullColor()) },
            swimlane = swimlanes.find { it.id == swimlane },
            dueDate = due_date,
            dueDateStatus = due_date_status,
            userStoryShortInfo = user_story_extra_info,
            version = version,
            color = color,
            type = type?.let { id -> filters.types.find { it.id == id } }?.toStatus(StatusType.Type),
            severity = severity?.let { id -> filters.severities.find { it.id == id } }?.toStatus(StatusType.Severity),
            priority = priority?.let { id -> filters.priorities.find { it.id == id } }?.toStatus(StatusType.Priority),
            url =  "${session.server.value}/project/${project_extra_info.slug}/${transformTaskTypeForCopyLink(commonTaskType)}/$ref"
        )
    }


    /**
     * Edit related
     */


    // edit task itself

    private fun Tag.toList() = listOf(name, color)

    private fun CommonTaskExtended.toEditRequest() = EditCommonTaskRequest(
        subject = title,
        description = description,
        status = status.id,
        type = type?.id,
        severity = severity?.id,
        priority = priority?.id,
        milestone = sprint?.id,
        assigned_to = assignedIds.firstOrNull(),
        assigned_users = assignedIds,
        watchers = watcherIds,
        swimlane = swimlane?.id,
        due_date = dueDate,
        color = color,
        tags = tags.map { it.toList() },
        version = version
    )

    private suspend fun editCommonTask(commonTask: CommonTaskExtended, request: EditCommonTaskRequest) {
        taigaApi.editCommonTask(CommonTaskPathPlural(commonTask.taskType), commonTask.id, request)
    }

    override suspend fun editStatus(
        commonTask: CommonTaskExtended,
        statusId: Long,
        statusType: StatusType
    ) = withIO {
        if (commonTask.taskType != CommonTaskType.Issue && statusType != StatusType.Status) {
            throw UnsupportedOperationException("Cannot change $statusType for ${commonTask.taskType}")
        }

        val request = commonTask.toEditRequest().let {
            when (statusType) {
                StatusType.Status -> it.copy(status = statusId)
                StatusType.Type -> it.copy(type = statusId)
                StatusType.Severity -> it.copy(severity = statusId)
                StatusType.Priority -> it.copy(priority = statusId)
            }
        }

        editCommonTask(commonTask, request)
    }

    override suspend fun editSprint(commonTask: CommonTaskExtended, sprintId: Long?) = withIO {
        if (commonTask.taskType in listOf(CommonTaskType.Epic, CommonTaskType.Task)) {
            throw UnsupportedOperationException("Cannot change sprint for ${commonTask.taskType}")
        }

        editCommonTask(commonTask, commonTask.toEditRequest().copy(milestone = sprintId))
    }

    override suspend fun editAssignees(commonTask: CommonTaskExtended, assignees: List<Long>) = withIO {
        val request = commonTask.toEditRequest().let {
            if (commonTask.taskType == CommonTaskType.UserStory) {
                it.copy(assigned_to = assignees.firstOrNull(), assigned_users = assignees)
            } else {
                it.copy(assigned_to = assignees.lastOrNull())
            }
        }

        editCommonTask(commonTask, request)
    }

    override suspend fun editWatchers(commonTask: CommonTaskExtended, watchers: List<Long>) = withIO {
        editCommonTask(commonTask, commonTask.toEditRequest().copy(watchers = watchers))
    }

    override suspend fun editDueDate(commonTask: CommonTaskExtended, date: LocalDate?) = withIO {
        editCommonTask(commonTask, commonTask.toEditRequest().copy(due_date = date))
    }

    override suspend fun editCommonTaskBasicInfo(
        commonTask: CommonTaskExtended,
        title: String,
        description: String,
    ) = withIO {
        editCommonTask(commonTask, commonTask.toEditRequest().copy(subject = title, description = description))
    }

    override suspend fun editTags(commonTask: CommonTaskExtended, tags: List<Tag>) = withIO {
        editCommonTask(commonTask, commonTask.toEditRequest().copy(tags = tags.map { it.toList() }))
    }

    override suspend fun editUserStorySwimlane(commonTask: CommonTaskExtended, swimlaneId: Long?) = withIO {
        if (commonTask.taskType != CommonTaskType.UserStory) {
            throw UnsupportedOperationException("Cannot change swimlane for ${commonTask.taskType}")
        }

        editCommonTask(commonTask, commonTask.toEditRequest().copy(swimlane = swimlaneId))
    }

    override suspend fun editEpicColor(commonTask: CommonTaskExtended, color: String) = withIO {
        if (commonTask.taskType != CommonTaskType.Epic) {
            throw UnsupportedOperationException("Cannot change color for ${commonTask.taskType}")
        }

        editCommonTask(commonTask, commonTask.toEditRequest().copy(color = color))
    }

    // edit other related parts

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


    override suspend fun createCommonTask(
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        parentId: Long?,
        sprintId: Long?,
        statusId: Long?,
        swimlaneId: Long?
    ) = withIO {
        when (commonTaskType) {
            CommonTaskType.Task -> taigaApi.createTask(
                createTaskRequest = CreateTaskRequest(currentProjectId, title, description, sprintId, parentId)
            )
            CommonTaskType.Issue -> taigaApi.createIssue(
                createIssueRequest = CreateIssueRequest(currentProjectId, title, description, sprintId)
            )
            CommonTaskType.UserStory -> taigaApi.createUserstory(
                createUserStoryRequest = CreateUserStoryRequest(currentProjectId, title, description, statusId, swimlaneId)
            )
            else -> taigaApi.createCommonTask(
                taskPath = CommonTaskPathPlural(commonTaskType),
                createRequest = CreateCommonTaskRequest(currentProjectId, title, description, statusId)
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
            promoteToUserStoryRequest = PromoteToUserStoryRequest(currentProjectId)
        ).first()
         .let { taigaApi.getUserStoryByRef(currentProjectId, it).toCommonTask(CommonTaskType.UserStory) }
    }

    override suspend fun addAttachment(commonTaskId: Long, commonTaskType: CommonTaskType, fileName: String, inputStream: InputStream) = withIO {
        val file = MultipartBody.Part.createFormData(
            name = "attached_file",
            filename = fileName,
            body = inputStream.readBytes().toRequestBody("*/*".toMediaType())
        )
        val project = MultipartBody.Part.createFormData("project", currentProjectId.toString())
        val objectId = MultipartBody.Part.createFormData("object_id", commonTaskId.toString())

        inputStream.use {
            taigaApi.uploadCommonTaskAttachment(
                taskPath = CommonTaskPathPlural(commonTaskType),
                file = file,
                project = project,
                objectId = objectId
            )
        }
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

    companion object {
        const val PATH_TO_USERSTORY = "us"
        const val PATH_TO_TASK = "task"
        const val PATH_TO_EPIC = "epic"
        const val PATH_TO_ISSUE = "issue"
    }
}
