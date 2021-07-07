package io.eugenethedev.taigamobile.domain.entities

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Comment(
    val id: String,
    @SerializedName("user") val author: User,
    @SerializedName("comment") val text: String,
    @SerializedName("created_at") val postDateTime: LocalDateTime,
    @SerializedName("delete_comment_date") val deleteDate: LocalDateTime?
) {
    var canDelete: Boolean? = null
}
