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

data class ChangeAssigneesRequest(
    val assigned_to: Long?,
    val assigned_users: List<Long>,
    val version: Int
)

data class ChangeTaskAssigneesRequest(
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