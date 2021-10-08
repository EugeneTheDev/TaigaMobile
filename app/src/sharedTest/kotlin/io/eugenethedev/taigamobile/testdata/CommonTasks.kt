package io.eugenethedev.taigamobile.testdata

import io.eugenethedev.taigamobile.domain.entities.CommonTaskType

class Comment(
    val text: String,
    val author: User
)

sealed interface GenericTask {
    val title: String
    val creator: User
    val description: String
    val comments: List<Comment>
    val assignedTo: User?
    val watchers: List<User>
    val isClosed: Boolean
    val commonTaskType: CommonTaskType
}

class Epic(
    override val title: String,
    override val creator: User,
    override val description: String = "",
    override val comments: List<Comment> = emptyList(),
    override val assignedTo: User? = null,
    override val watchers: List<User> = emptyList(),
    override val isClosed: Boolean = false
) : GenericTask {
    override val commonTaskType = CommonTaskType.Epic
}

class UserStory(
    override val title: String,
    override val creator: User,
    override val description: String = "",
    override val comments: List<Comment> = emptyList(),
    override val assignedTo: User? = null,
    override val watchers: List<User> = emptyList(),
    override val isClosed: Boolean = false,
    val epics: List<Epic> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val sprint: Sprint? = null
) : GenericTask {
    override val commonTaskType = CommonTaskType.UserStory
}

class Task(
    override val title: String,
    override val creator: User,
    override val description: String = "",
    override val comments: List<Comment> = emptyList(),
    override val assignedTo: User? = null,
    override val watchers: List<User> = emptyList(),
    override val isClosed: Boolean = false
) : GenericTask {
    override val commonTaskType = CommonTaskType.Task
}

class Issue(
    override val title: String,
    override val creator: User,
    override val description: String = "",
    override val comments: List<Comment> = emptyList(),
    override val assignedTo: User? = null,
    override val watchers: List<User> = emptyList(),
    override val isClosed: Boolean = false,
    val sprint: Sprint? = null
) : GenericTask {
    override val commonTaskType = CommonTaskType.Issue
}
