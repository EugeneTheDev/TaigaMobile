package io.eugenethedev.taigamobile.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

data class ProjectInSearch(
    val id: Long,
    val name: String,
    val slug: String,
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

enum class CommonTaskType {
    USERSTORY,
    TASK
}

data class CommonTask(
    val id: Long,
    val createdDate: Date,
    val title: String,
    val ref: Int,
    val status: Status,
    val assignee: Assignee?,
    val projectSlug: String,
    val taskType: CommonTaskType
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

data class CommonTaskExtended(
    val id: Long,
    val status: Status,
    val createdDateTime: Date,
    val sprint: Sprint?,
    val assignedIds: List<Long>,
    val watcherIds: List<Long>,
    val creatorId: Long,
    val ref: Int,
    val title: String,
    val description: String,
    val epics: List<Epic>,
    val projectSlug: String,
    val userStoryShortInfo: UserStoryShortInfo?,
    val version: Int
)

data class Epic(
    val id: Long,
    @SerializedName("subject") val title: String,
    val ref: Int,
    val color: String
)

data class UserStoryShortInfo(
    val id: Long,
    val ref: Int,
    val title: String,
    val epicColor: String?
)

data class User(
    val id: Long?, // sometimes there is no id
    @SerializedName("full_name_display") val fullName: String?,
    @SerializedName("photo") val avatarUrl: String?,
    val username: String,
    val name: String? = null // sometimes name appears here
) {
    val displayName get() = fullName ?: name!!
}

data class Comment(
    val id: String,
    @SerializedName("user") val author: User,
    @SerializedName("comment") val text: String,
    @SerializedName("created_at") val postDateTime: Date
)

data class TeamMember(
    val id: Long,
    val avatarUrl: String?,
    val name: String,
    val role: String,
    val username: String,
    val totalPower: Int
) {
    fun toUser() = User(
        id = id,
        fullName = name,
        avatarUrl = avatarUrl,
        username = username
    )
}