package io.eugenethedev.taigamobile.domain.entities

import com.google.gson.annotations.SerializedName

data class Attachment(
    val id: Long,
    val name: String,
    @SerializedName("size") val sizeInBytes: Long,
    val url: String
)
