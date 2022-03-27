package io.eugenethedev.taigamobile.domain.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Attachment(
    val id: Long,
    val name: String,
    @Json(name = "size") val sizeInBytes: Long,
    val url: String
)
