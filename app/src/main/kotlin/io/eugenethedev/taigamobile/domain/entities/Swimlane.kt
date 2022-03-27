package io.eugenethedev.taigamobile.domain.entities

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Swimlane(
    val id: Long,
    val name: String,
    val order: Long
)
