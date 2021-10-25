package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.*
import java.io.InputStream
import java.time.LocalDate

interface ITasksRepository {
    suspend fun getWorkingOn(): List<CommonTask>
    suspend fun getWatching(): List<CommonTask>

    suspend fun getStatuses(commonTaskType: CommonTaskType): List<Status>
    suspend fun getStatusByType(commonTaskType: CommonTaskType, statusType: StatusType): List<Status>

    suspend fun getFiltersData(commonTaskType: CommonTaskType): FiltersData

    suspend fun getEpics(page: Int, query: String? = null): List<CommonTask>

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

    // edit related
    suspend fun changeStatus(commonTaskId: Long, commonTaskType: CommonTaskType, statusId: Long, statusType: StatusType, version: Int)
    suspend fun changeSprint(commonTaskId: Long, commonTaskType: CommonTaskType, sprintId: Long?, version: Int)
    suspend fun linkToEpic(epicId: Long, userStoryId: Long)
    suspend fun unlinkFromEpic(epicId: Long, userStoryId: Long)
    suspend fun changeAssignees(commonTaskId: Long, commonTaskType: CommonTaskType, assignees: List<Long>, version: Int)
    suspend fun changeWatchers(commonTaskId: Long, commonTaskType: CommonTaskType, watchers: List<Long>, version: Int)
    suspend fun changeDueDate(commonTaskId: Long, commonTaskType: CommonTaskType, date: LocalDate?, version: Int)
    suspend fun createComment(commonTaskId: Long, commonTaskType: CommonTaskType, comment: String, version: Int)
    suspend fun deleteComment(commonTaskId: Long, commonTaskType: CommonTaskType, commentId: String)
    suspend fun editCommonTask(commonTaskId: Long, commonTaskType: CommonTaskType, title: String, description: String, version: Int)

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

    suspend fun editTags(commonTaskType: CommonTaskType, commonTaskId: Long, tags: List<Tag>, version: Int)

    suspend fun changeUserStorySwimlane(userStoryId: Long, swimlaneId: Long?, version: Int)

    suspend fun changeEpicColor(epicId: Long, color: String, version: Int)
}
