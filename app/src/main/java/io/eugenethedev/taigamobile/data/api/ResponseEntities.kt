package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.Status
import java.util.*

/**
 * Some complicated api responses
 */

data class AuthResponse(
    val auth_token: String
)

data class ProjectResponse(
    val id: Long,
    val name: String
)

data class FiltersDataResponse(
    val statuses: List<Status>
)

data class UserStoryResponse(
    val id: Long,
    val subject: String,
    val created_date: Date,
    val status: Long,
    val assigned_to_extra_info: AssigneeInfo?
) {
    data class AssigneeInfo(
        val id: Long,
        val full_name_display: String
    )
}