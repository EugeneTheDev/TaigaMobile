package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.*
import java.io.InputStream
import java.time.LocalDate

interface ITasksRepository {
    suspend fun getWorkingOn(): List<CommonTask>
    suspend fun getWatching(): List<CommonTask>

    suspend fun getStatuses(commonTaskType: CommonTaskType): List<Status>
    suspend fun getStatusByType(commonTaskType: CommonTaskType, statusType: StatusType): List<Status>

    suspend fun getFiltersData(commonTaskType: CommonTaskType, isCommonTaskFromBacklog: Boolean = false): FiltersData

    suspend fun getEpics(page: Int, filters: FiltersData): List<CommonTask>

    suspend fun getAllUserStories(): List<CommonTaskExtended> // for stories kanban
    suspend fun getBacklogUserStories(page: Int, filters: FiltersData): List<CommonTask>
    suspend fun getEpicUserStories(epicId: Long): List<CommonTask>

    suspend fun getUserStoryTasks(storyId: Long): List<CommonTask>

    suspend fun getIssues(page: Int, filters: FiltersData): List<CommonTask>

    suspend fun getCommonTask(commonTaskId: Long, type: CommonTaskType): CommonTaskExtended

    suspend fun getComments(commonTaskId: Long, type: CommonTaskType): List<Comment>

    suspend fun getAttachments(commonTaskId: Long, type: CommonTaskType): List<Attachment>

    suspend fun getCustomFields(commonTaskId: Long, type: CommonTaskType): CustomFields

    suspend fun getAllTags(commonTaskType: CommonTaskType): List<Tag>

    suspend fun getSwimlanes(): List<Swimlane>

    // ============
    // Edit methods
    // ============

    // edit task
    suspend fun editStatus(commonTask: CommonTaskExtended, statusId: Long, statusType: StatusType)
    suspend fun editSprint(commonTask: CommonTaskExtended, sprintId: Long?)
    suspend fun editAssignees(commonTask: CommonTaskExtended, assignees: List<Long>)
    suspend fun editWatchers(commonTask: CommonTaskExtended, watchers: List<Long>)
    suspend fun editDueDate(commonTask: CommonTaskExtended, date: LocalDate?)
    suspend fun editCommonTaskBasicInfo(commonTask: CommonTaskExtended, title: String, description: String)
    suspend fun editTags(commonTask: CommonTaskExtended, tags: List<Tag>)
    suspend fun editUserStorySwimlane(commonTask: CommonTaskExtended, swimlaneId: Long?)
    suspend fun editEpicColor(commonTask: CommonTaskExtended, color: String)
    suspend fun editBlocked(commonTask: CommonTaskExtended, blockedNote: String?)

    // related edits
    suspend fun createComment(commonTaskId: Long, commonTaskType: CommonTaskType, comment: String, version: Int)
    suspend fun deleteComment(commonTaskId: Long, commonTaskType: CommonTaskType, commentId: String)
    suspend fun linkToEpic(epicId: Long, userStoryId: Long)
    suspend fun unlinkFromEpic(epicId: Long, userStoryId: Long)

    suspend fun createCommonTask(
        commonTaskType: CommonTaskType,
        title: String,
        description: String,
        parentId: Long?,
        sprintId: Long?,
        statusId: Long?,
        swimlaneId: Long?
    ): CommonTask

    suspend fun deleteCommonTask(commonTaskType: CommonTaskType, commonTaskId: Long)

    suspend fun promoteCommonTaskToUserStory(commonTaskId: Long, commonTaskType: CommonTaskType): CommonTask

    suspend fun addAttachment(commonTaskId: Long, commonTaskType: CommonTaskType, fileName: String, inputStream: InputStream)
    suspend fun deleteAttachment(commonTaskType: CommonTaskType, attachmentId: Long)

    suspend fun editCustomFields(commonTaskType: CommonTaskType, commonTaskId: Long, fields: Map<Long, CustomFieldValue?>, version: Int)
}
