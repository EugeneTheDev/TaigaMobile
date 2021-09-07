package io.eugenethedev.taigamobile.testdata

import io.eugenethedev.taigamobile.domain.entities.CommonTaskType

sealed interface GenericTask {
    val name: String
    val description: String
    val comments: List<String>
    val isAssigned: Boolean
    val isWatching: Boolean
    val isClosed: Boolean
    val commonTaskType: CommonTaskType
}

data class Epic(
    override val name: String,
    override val description: String = "",
    override val comments: List<String> = emptyList(),
    override val isAssigned: Boolean = false,
    override val isWatching: Boolean = false,
    override val isClosed: Boolean = false
) : GenericTask {
    override val commonTaskType = CommonTaskType.Epic
}

data class UserStory(
    override val name: String,
    override val description: String = "",
    override val comments: List<String> = emptyList(),
    override val isAssigned: Boolean = false,
    override val isWatching: Boolean = false,
    override val isClosed: Boolean = false,
    val epics: List<Epic> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val sprint: Sprint? = null
) : GenericTask {
    override val commonTaskType = CommonTaskType.UserStory
}

data class Task(
    override val name: String,
    override val description: String = "",
    override val comments: List<String> = emptyList(),
    override val isAssigned: Boolean = false,
    override val isWatching: Boolean = false,
    override val isClosed: Boolean = false
) : GenericTask {
    override val commonTaskType = CommonTaskType.Task
}

data class Issue(
    override val name: String,
    override val description: String = "",
    override val comments: List<String> = emptyList(),
    override val isAssigned: Boolean = false,
    override val isWatching: Boolean = false,
    override val isClosed: Boolean = false,
    val sprint: Sprint? = null
) : GenericTask {
    override val commonTaskType = CommonTaskType.Issue
}
