package io.eugenethedev.taigamobile.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Tasks related entities
 */

data class Status(
    val id: Long,
    val name: String,
    val color: String,
    val order: Int = 0
)


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


enum class CommonTaskType {
    USERSTORY,
    TASK,
    EPIC
}


data class CommonTask(
    val id: Long,
    val createdDate: Date,
    val title: String,
    val ref: Int,
    val status: Status,
    val assignee: Assignee? = null,
    val projectSlug: String,
    val taskType: CommonTaskType,
    val isClosed: Boolean,
    val colors: List<String> = emptyList() // colored indicators (for stories and epics)
) {
    data class Assignee(
        val id: Long,
        val fullName: String,
    )
}


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
    val epicsShortInfo: List<EpicShortInfo>,
    val projectSlug: String,
    val userStoryShortInfo: UserStoryShortInfo? = null,
    val version: Int,
    val color: String? = null // for epic
)


data class EpicShortInfo(
    val id: Long,
    @SerializedName("subject") val title: String,
    val ref: Int,
    val color: String
)


data class UserStoryShortInfo(
    val id: Long,
    val ref: Int,
    val title: String,
    val epicColors: List<String>
)

data class Comment(
    val id: String,
    @SerializedName("user") val author: User,
    @SerializedName("comment") val text: String,
    @SerializedName("created_at") val postDateTime: Date,
    @SerializedName("delete_comment_date") val deleteDate: Date?
) {
    var canDelete: Boolean? = null
}