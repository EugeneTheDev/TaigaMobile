package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.Status
import java.util.*

/**
 * Some complicated api responses
 */

data class AuthResponse(
    val auth_token: String
)

data class FiltersDataResponse(
    val statuses: List<Status>
)

data class CommonTaskResponse(
    val id: Long,
    val subject: String,
    val created_date: Date,
    val status: Long,
    val ref: Int,
    val assigned_to_extra_info: AssigneeInfo?,
    val status_extra_info: StatusExtra
) {
    data class AssigneeInfo(
        val id: Long,
        val full_name_display: String
    )

    data class StatusExtra(
        val color: String,
        val name: String
    )
}

data class SprintResponse(
    val id: Long,
    val name: String,
    val estimated_start: Date,
    val estimated_finish: Date,
    val closed: Boolean,
    val order: Int,
    val user_stories: List<UserStory>
) {
    data class UserStory(
        val id: Long
    )
}
