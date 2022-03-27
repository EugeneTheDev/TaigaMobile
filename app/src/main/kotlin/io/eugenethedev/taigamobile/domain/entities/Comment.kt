package io.eugenethedev.taigamobile.domain.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Comment(
    val id: String,
    @Json(name = "user") val author: User,
    @Json(name = "comment") val text: String,
    @Json(name = "created_at") val postDateTime: LocalDateTime,
    @Json(name = "delete_comment_date") val deleteDate: LocalDateTime?
) {
    var canDelete: Boolean = false
}
