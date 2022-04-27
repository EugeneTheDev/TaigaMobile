package io.eugenethedev.taigamobile.domain.entities

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FiltersData(
    val query: String = "",
    val assignees: List<UsersFilter> = emptyList(),
    val roles: List<RolesFilter> = emptyList(),
    val tags: List<TagsFilter> = emptyList(),
    val statuses: List<StatusesFilter> = emptyList(),
    val createdBy: List<UsersFilter> = emptyList(),

    // user story filters
    val epics: List<EpicsFilter> = emptyList(),

    // issue filters
    val priorities: List<StatusesFilter> = emptyList(),
    val severities: List<StatusesFilter> = emptyList(),
    val types: List<StatusesFilter> = emptyList()
) {
    operator fun minus(other: FiltersData) = FiltersData(
        assignees = assignees - other.assignees.toSet(),
        roles = roles - other.roles.toSet(),
        tags = tags - other.tags.toSet(),
        statuses = statuses - other.statuses.toSet(),
        createdBy = createdBy - other.createdBy.toSet(),
        priorities = priorities - other.priorities.toSet(),
        severities = severities - other.severities.toSet(),
        types = types - other.types.toSet(),
        epics = epics - other.epics.toSet()
    )

    val filtersNumber = listOf(assignees, roles, tags, statuses, createdBy, priorities, severities, types, epics).sumOf { it.size }
}

fun List<Filter>.hasData() = any { it.count > 0 }

sealed interface Filter {
    val id: Long?
    val name: String
    val count: Int
    val color: String?
}

@JsonClass(generateAdapter = true)
data class StatusesFilter(
    override val id: Long,
    override val color: String,
    override val name: String,
    override val count: Int
) : Filter

@JsonClass(generateAdapter = true)
data class UsersFilter(
    override val id: Long?,
    override val name: String,
    override val count: Int
) : Filter {
    override val color: String? = null
}

@JsonClass(generateAdapter = true)
data class RolesFilter(
    override val id: Long,
    override val name: String,
    override val count: Int
) : Filter {
    override val color: String? = null
}

@JsonClass(generateAdapter = true)
data class TagsFilter(
    override val name: String,
    override val color: String,
    override val count: Int
) : Filter {
    override val id: Long? = null
}

@JsonClass(generateAdapter = true)
data class EpicsFilter(
    override val id: Long?,
    override val name: String,
    override val count: Int
) : Filter {
    override val color: String? = null
}