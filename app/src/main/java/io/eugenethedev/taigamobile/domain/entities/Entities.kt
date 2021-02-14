package io.eugenethedev.taigamobile.domain.entities

import java.util.*

data class Project(
    val id: Long,
    val name: String
)

data class Status(
    val id: Long,
    val name: String,
    val order: Int,
    val color: String
)

data class Story(
    val id: Long,
    val createdDate: Date,
    val title: String,
    val statusId: Long,
    val assignee: Assignee?
) {
    data class Assignee(
        val id: Long,
        val fullName: String,
    )
}