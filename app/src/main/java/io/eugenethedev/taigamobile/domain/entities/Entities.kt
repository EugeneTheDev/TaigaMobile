package io.eugenethedev.taigamobile.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

data class Project(
    val id: Long,
    val name: String,
    @SerializedName("i_am_member") val isMember: Boolean,
    @SerializedName("i_am_admin") val isAdmin: Boolean,
    @SerializedName("i_am_owner") val isOwner: Boolean
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

data class UserStory(
    val id: Long,
    val status: Status,
    val createdDateTime: Date,
    val sprintId: Long?,
    val sprintName: String?,
    val assignedIds: List<Long>,
    val watcherIds: List<Long>,
    val creatorId: Long,
    val ref: Int,
    val title: String,
    val description: String,
    val epics: List<Epic>,
    val projectSlug: String
)

data class Epic(
    val id: Long,
    val title: String,
    val ref: Int,
    val color: String
)

data class User(
    val id: Long,
    @SerializedName("full_name_display") val fullName: String,
    @SerializedName("photo") val avatarUrl: String?
)

data class Comment(
    val id: Long,
    val author: User,
    val text: String,
    val postDateTime: Date
)