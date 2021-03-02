package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.Epic
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
    val status_extra_info: StatusExtra,
    val project_extra_info: ProjectExtraInfo,
    val milestone: Long,
    val milestone_name: String,
    val assigned_users: List<Long>?,
    val assigned_to: Long,
    val watchers: List<Long>,
    val owner: Long,
    val description: String,
    val epics: List<Epic>?,
    val user_story_extra_info: UserStoryExtraInfo?
) {
    data class AssigneeInfo(
        val id: Long,
        val full_name_display: String
    )

    data class StatusExtra(
        val color: String,
        val name: String
    )

    data class ProjectExtraInfo(
        val slug: String
    )

    data class UserStoryExtraInfo(
        val id: Long,
        val ref: Int,
        val subject: String,
        val epics: List<Epic>?
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
