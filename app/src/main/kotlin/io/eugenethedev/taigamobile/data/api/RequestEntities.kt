package io.eugenethedev.taigamobile.data.api

import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.util.*

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
data class EditCommonTaskRequest(
    val subject: String,
    val description: String,
    val status: Long,
    val type: Long?,
    val severity: Long?,
    val priority: Long?,
    val milestone: Long?,
    val assigned_to: Long?,
    val assigned_users: List<Long>,
    val watchers: List<Long>,
    val swimlane: Long?,
    val due_date: LocalDate?,
    val color: String?,
    val tags: List<List<String>>,
    val version: Int
)

@JsonClass(generateAdapter = true)
data class CreateCommentRequest(
    val comment: String,
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
