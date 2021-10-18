package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Some complicated api responses
 */

data class AuthResponse(
    val auth_token: String,
    val refresh: String?,
    val id: Long
)

data class RefreshTokenResponse(
    val auth_token: String,
    val refresh: String
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
    val tags: List<Filter>?,
    val roles: List<Filter>?,
    val assigned_to: List<UserFilter>,
    val owners: List<UserFilter>,

    // issue filters
    val priorities: List<Filter>?,
    val severities: List<Filter>?,
    val types: List<Filter>?
) {
    data class Filter(
        val id: Long?,
        val name: String?,
        val color: String?,
        val count: Int
    )

    data class UserFilter(
        val id: Long?,
        val full_name: String,
        val count: Int
    )

}

data class CommonTaskResponse(
    val id: Long,
    val subject: String,
    val created_date: LocalDateTime,
    val status: Long,
    val ref: Int,
    val assigned_to_extra_info: User?,
    val status_extra_info: StatusExtra,
    val project_extra_info: Project,
    val milestone: Long?,
    val milestone_name: String,
    val assigned_users: List<Long>?,
    val assigned_to: Long?,
    val watchers: List<Long>,
    val owner: Long,
    val description: String?,
    val epics: List<EpicShortInfo>?,
    val user_story_extra_info: UserStoryShortInfo?,
    val version: Int,
    val is_closed: Boolean,
    val tags: List<List<String?>>?,
    val swimlane: Long?,
    val due_date: LocalDate?,
    val due_date_status: DueDateStatus?,

    // for epic
    val color: String?,

    // for issue
    val type: Long?,
    val severity: Long?,
    val priority: Long?
) {
    data class StatusExtra(
        val color: String,
        val name: String
    )
}

data class SprintResponse(
    val id: Long,
    val name: String,
    val estimated_start: LocalDate,
    val estimated_finish: LocalDate,
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

data class CustomAttributeResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val order: Int,
    val type: CustomFieldType,
    val extra: List<String>?
)

data class CustomAttributesValuesResponse(
    val attributes_values: Map<Long, Any?>,
    val version: Int
)