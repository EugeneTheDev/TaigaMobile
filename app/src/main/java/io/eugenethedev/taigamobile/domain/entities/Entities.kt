package io.eugenethedev.taigamobile.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

data class Project(
    val id: Long,
    val name: String,
    val isMember: Boolean,
    val isAdmin: Boolean,
    val isOwner: Boolean
)

data class Status(
    val id: Long,
    val name: String,
    val color: String,
    val order: Int = 0
)

data class CommonTask(
    val id: Long,
    val createdDate: Date,
    val title: String,
    val ref: Int,
    val status: Status,
    val assignee: Assignee?
) {
    data class Assignee(
        val id: Long,
        val fullName: String,
    )
}

@Parcelize
data class Sprint(
    val id: Long,
    val name: String,
    val order: Int,
    val start: Date,
    val finish: Date,
    val storiesCount: Int,
    val isClosed: Boolean
) : Parcelable