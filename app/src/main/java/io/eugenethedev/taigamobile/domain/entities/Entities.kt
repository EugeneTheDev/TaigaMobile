package io.eugenethedev.taigamobile.domain.entities

import java.util.*

data class Project(
    val id: Long,
    val name: String
)

data class Status(
    val id: Long,
    val name: String,
    val color: String,
    val order: Int = 0
)

data class Story(
    val id: Long,
    val createdDate: Date,
    val title: String,
    val status: Status,
    val assignee: Assignee?
) {
    data class Assignee(
        val id: Long,
        val fullName: String,
    )
}

data class Sprint(
    val id: Long,
    val name: String,
    val order: Int,
    val start: Date,
    val finish: Date,
    val storiesCount: Int,
    val isClosed: Boolean
)