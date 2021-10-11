package io.eugenethedev.taigamobile.domain.entities

data class FiltersData(
    val query: String = "",
    val assignees: List<AssigneesFilter> = emptyList(),
    val roles: List<RolesFilter> = emptyList(),
    val tags: List<TagsFilter> = emptyList(),
    val statuses: List<StatusesFilter> = emptyList(),

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
        priorities = priorities - other.priorities,
        severities = severities - other.severities,
        types = types - other.types
    )
}

fun List<Filter>.noData() = all { it.count == 0 }

sealed interface Filter {
    val name: String
    val count: Int
}

data class StatusesFilter(
    val id: Long,
    val color: String,
    override val name: String,
    override val count: Int
) : Filter

data class AssigneesFilter(
    val id: Long?,
    override val name: String,
    override val count: Int
) : Filter

data class RolesFilter(
    val id: Long,
    override val name: String,
    override val count: Int
) : Filter

data class TagsFilter(
    val color: String,
    override val name: String,
    override val count: Int
) : Filter
