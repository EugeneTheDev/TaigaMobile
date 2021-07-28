package io.eugenethedev.taigamobile.data.api

import java.time.LocalDate

data class AuthRequest(
    val password: String,
    val username: String,
    val type: String = "normal"
)

data class ChangeStatusRequest(
    val status: Long,
    val version: Int
)

data class ChangeTypeRequest(
    val type: Long,
    val version: Int
)

data class ChangeSeverityRequest(
    val severity: Long,
    val version: Int
)

data class ChangePriorityRequest(
    val priority: Long,
    val version: Int
)

data class ChangeSprintRequest(
    val milestone: Long?,
    val version: Int
)

data class ChangeUserStoryAssigneesRequest(
    val assigned_to: Long?,
    val assigned_users: List<Long>,
    val version: Int
)

data class ChangeCommonTaskAssigneesRequest(
    val assigned_to: Long?,
    val version: Int
)

data class ChangeWatchersRequest(
    val watchers: List<Long>,
    val version: Int
)

data class ChangeUserStorySwimlaneRequest(
    val swimlane: Long?,
    val version: Int
)

data class ChangeCommonTaskDueDateRequest(
    val due_date: LocalDate?,
    val version: Int
)

data class CreateCommentRequest(
    val comment: String,
    val version: Int
)

data class EditCommonTaskRequest(
    val subject: String,
    val description: String,
    val version: Int
)

data class CreateCommonTaskRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val status: Long?
)

data class CreateTaskRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val milestone: Long?,
    val user_story: Long?
)

data class CreateIssueRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val milestone: Long?,
)

data class CreateUserStoryRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val status: Long?,
    val swimlane: Long?
)

data class LinkToEpicRequest(
    val epic: String,
    val user_story: Long
)

data class PromoteToUserStoryRequest(
    val project_id: Long
)

data class EditCustomAttributesValuesRequest(
    val attributes_values: Map<Long, Any?>,
    val version: Int
)

data class EditTagsRequest(
    val tags: List<List<String>>,
    val version: Int
)

data class CreateSprintRequest(
    val name: String,
    val estimated_start: LocalDate,
    val estimated_finish: LocalDate,
    val project: Long
)

data class EditSprintRequest(
    val name: String,
    val estimated_start: LocalDate,
    val estimated_finish: LocalDate,
)
