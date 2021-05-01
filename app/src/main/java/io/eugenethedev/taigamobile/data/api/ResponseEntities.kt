package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.EpicShortInfo
import io.eugenethedev.taigamobile.domain.entities.Project
import java.util.*

/**
 * Some complicated api responses
 */

data class AuthResponse(
    val auth_token: String,
    val id: Long
)

data class ProjectResponse(
    val id: Long,
    val name: String,
    val members: List<Member>
) {
    data class Member(
        val id: Long,
        val photo: String?,
        val full_name_display: String,
        val role_name: String,
        val username: String
    )
}

data class FiltersDataResponse(
    val statuses: List<Filter>,

    // issue filters
    val priorities: List<Filter>?,
    val severities: List<Filter>?,
    val types: List<Filter>?
) {
    data class Filter(
        val id: Long,
        val name: String,
        val color: String,
    )
}

data class CommonTaskResponse(
    val id: Long,
    val subject: String,
    val created_date: Date,
    val status: Long,
    val ref: Int,
    val assigned_to_extra_info: AssigneeInfo?,
    val status_extra_info: StatusExtra,
    val project_extra_info: Project,
    val milestone: Long?,
    val milestone_name: String,
    val assigned_users: List<Long>?,
    val assigned_to: Long,
    val watchers: List<Long>,
    val owner: Long,
    val description: String,
    val epics: List<EpicShortInfo>?,
    val user_story_extra_info: UserStoryExtraInfo?,
    val version: Int,
    val color: String?, // for epic
    val is_closed: Boolean,
    // for issue
    val type: Long?,
    val severity: Long?,
    val priority: Long?
) {
    data class AssigneeInfo(
        val id: Long,
        val full_name_display: String
    )

    data class StatusExtra(
        val color: String,
        val name: String
    )

    data class UserStoryExtraInfo(
        val id: Long,
        val ref: Int,
        val subject: String,
        val epics: List<EpicShortInfo>?
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

data class MemberStatsResponse(
    val closed_bugs: Map<String, Int>, // because api returns "null" key along with id keys, so...
    val closed_tasks: Map<String, Int>,
    val created_bugs: Map<String, Int>,
    val iocaine_tasks: Map<String, Int>,
    val wiki_changes: Map<String, Int>
)
