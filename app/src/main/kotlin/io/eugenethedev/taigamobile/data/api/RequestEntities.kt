package io.eugenethedev.taigamobile.data.api

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class AuthRequest(
    val password: String,
    val username: String,
    val type: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(
    val refresh: String
)

@JsonClass(generateAdapter = true)
data class ChangeStatusRequest(
    val status: Long,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangeTypeRequest(
    val type: Long,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangeSeverityRequest(
    val severity: Long,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangePriorityRequest(
    val priority: Long,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangeSprintRequest(
    val milestone: Long?,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangeUserStoryAssigneesRequest(
    val assigned_to: Long?,
    val assigned_users: List<Long>,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangeCommonTaskAssigneesRequest(
    val assigned_to: Long?,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangeWatchersRequest(
    val watchers: List<Long>,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangeUserStorySwimlaneRequest(
    val swimlane: Long?,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangeCommonTaskDueDateRequest(
    val due_date: LocalDate?,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class ChangeEpicColor(
    val color: String,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class CreateCommentRequest(
    val comment: String,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class EditCommonTaskRequest(
    val subject: String,
    val description: String,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class CreateCommonTaskRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val status: Long?
)

@JsonClass(generateAdapter = true)
data class CreateTaskRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val milestone: Long?,
    val user_story: Long?
)

@JsonClass(generateAdapter = true)
data class CreateIssueRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val milestone: Long?,
)

@JsonClass(generateAdapter = true)
data class CreateUserStoryRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val status: Long?,
    val swimlane: Long?
)

@JsonClass(generateAdapter = true)
data class LinkToEpicRequest(
    val epic: String,
    val user_story: Long
)

@JsonClass(generateAdapter = true)
data class PromoteToUserStoryRequest(
    val project_id: Long
)

@JsonClass(generateAdapter = true)
data class EditCustomAttributesValuesRequest(
    val attributes_values: Map<Long, Any?>,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class EditTagsRequest(
    val tags: List<List<String>>,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class CreateSprintRequest(
    val name: String,
    val estimated_start: LocalDate,
    val estimated_finish: LocalDate,
    val project: Long
)

@JsonClass(generateAdapter = true)
data class EditSprintRequest(
    val name: String,
    val estimated_start: LocalDate,
    val estimated_finish: LocalDate,
)

@JsonClass(generateAdapter = true)
data class EditWikiPageRequest(
    val content: String,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class NewWikiLinkRequest(
    val href: String,
    val project: Long,
    val title: String
)
