package io.eugenethedev.taigamobile.data.api

data class AuthRequest(
    val password: String,
    val username: String,
    val type: String = "normal"
)

data class ChangeStatusRequest(
    val status: Long,
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
    val description: String
)

data class CreateTaskRequest(
    val project: Long,
    val subject: String,
    val description: String,
    val milestone: Long?,
    val user_story: Long?
)

data class LinkToEpicRequest(
    val epic: String,
    val user_story: Long
)

data class PromoteToUserStoryRequest(
    val project_id: Long
)