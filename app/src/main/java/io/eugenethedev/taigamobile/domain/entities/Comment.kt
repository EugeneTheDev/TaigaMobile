package io.eugenethedev.taigamobile.domain.entities

import com.google.gson.annotations.SerializedName
import java.util.*

data class Comment(
    val id: String,
    @SerializedName("user") val author: User,
    @SerializedName("comment") val text: String,
    @SerializedName("created_at") val postDateTime: Date,
    @SerializedName("delete_comment_date") val deleteDate: Date?
) {
    var canDelete: Boolean? = null
}
