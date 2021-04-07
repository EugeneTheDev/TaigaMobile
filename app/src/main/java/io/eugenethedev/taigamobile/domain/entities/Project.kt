package io.eugenethedev.taigamobile.domain.entities

import com.google.gson.annotations.SerializedName

/**
 * Project related entities
 */

data class Project(
    val id: Long,
    val name: String,
    val slug: String,
    @SerializedName("i_am_member") val isMember: Boolean,
    @SerializedName("i_am_admin") val isAdmin: Boolean,
    @SerializedName("i_am_owner") val isOwner: Boolean
)
