package io.eugenethedev.taigamobile.domain.entities

data class FiltersData(
    val query: String = "",
    val assignees: List<UsersFilter> = emptyList(),
    val roles: List<RolesFilter> = emptyList(),
    val tags: List<TagsFilter> = emptyList(),
    val statuses: List<StatusesFilter> = emptyList(),
    val createdBy: List<UsersFilter> = emptyList(),

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
        types = types - other.types
    )

    val filtersNumber = listOf(assignees, roles, tags, statuses, createdBy, priorities, severities, types).sumOf { it.size }
}

fun List<Filter>.hasData() = any { it.count > 0 }

sealed interface Filter {
    val name: String
    val count: Int
    val color: String?
}

data class StatusesFilter(
    val id: Long,
    override val color: String,
    override val name: String,
    override val count: Int
) : Filter

data class UsersFilter(
    val id: Long?,
    override val name: String,
    override val count: Int
) : Filter {
    override val color: String? = null
}

data class RolesFilter(
    val id: Long,
    override val name: String,
    override val count: Int
) : Filter {
    override val color: String? = null
}

data class TagsFilter(
    override val color: String,
    override val name: String,
    override val count: Int
) : Filter
