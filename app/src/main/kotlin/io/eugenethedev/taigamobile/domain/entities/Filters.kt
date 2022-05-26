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

    // updates current filters data using other filters data
    // (helpful for updating already selected filters)
    fun updateData(other: FiltersData): FiltersData {
        fun List<UsersFilter>.updateUsers(other: List<UsersFilter>) = map { current ->
            other.find { new -> current.id == new.id  }?.let {
                current.copy(name = it.name, count = it.count)
            } ?: current.copy(count = 0)
        }

        fun List<StatusesFilter>.updateStatuses(other: List<StatusesFilter>) = map { current ->
            other.find { new -> current.id == new.id  }?.let {
                current.copy(name = it.name, color = it.color, count = it.count)
            } ?: current.copy(count = 0)
        }

        return FiltersData(
            assignees = assignees.updateUsers(other.assignees),
            roles = roles.map { current ->
                other.roles.find { new -> current.id == new.id  }?.let {
                    current.copy(name = it.name, count = it.count)
                } ?: current.copy(count = 0)
            },
            tags = tags.map { current ->
                other.tags.find { new -> current.name == new.name  }?.let {
                    current.copy(color = it.color, count = it.count)
                } ?: current.copy(count = 0)
            },
            statuses = statuses.updateStatuses(other.statuses),
            createdBy = createdBy.updateUsers(other.createdBy),
            epics = epics.map { current ->
                other.epics.find { new -> current.id == new.id  }?.let {
                    current.copy(name = it.name, count = it.count)
                } ?: current.copy(count = 0)
            },
            priorities = priorities.updateStatuses(other.priorities),
            severities = severities.updateStatuses(other.severities),
            types = types.updateStatuses(other.types)
        )
    }

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