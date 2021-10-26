package io.eugenethedev.taigamobile.domain.entities

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
        assignees = assignees - other.assignees,
        roles = roles - other.roles,
        tags = tags - other.tags,
        statuses = statuses - other.statuses,
        createdBy = createdBy - other.createdBy,
        priorities = priorities - other.priorities,
        severities = severities - other.severities,
        types = types - other.types,
        epics = epics - other.epics
    )

    val filtersNumber = listOf(assignees, roles, tags, statuses, createdBy, priorities, severities, types).sumOf { it.size }
}

fun List<Filter>.hasData() = any { it.count > 0 }

sealed interface Filter {
    val id: Long?
    val name: String
    val count: Int
    val color: String?
}

data class StatusesFilter(
    override val id: Long,
    override val color: String,
    override val name: String,
    override val count: Int
) : Filter

data class UsersFilter(
    override val id: Long?,
    override val name: String,
    override val count: Int
) : Filter {
    override val color: String? = null
}

data class RolesFilter(
    override val id: Long,
    override val name: String,
    override val count: Int
) : Filter {
    override val color: String? = null
}

data class TagsFilter(
    override val name: String,
    override val color: String,
    override val count: Int
) : Filter {
    override val id: Long? = null
}

data class EpicsFilter(
    override val id: Long?,
    override val name: String,
    override val count: Int
) : Filter {
    override val color: String? = null
}