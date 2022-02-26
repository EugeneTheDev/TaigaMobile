package io.eugenethedev.taigamobile.domain.entities

import com.google.gson.annotations.SerializedName

/**
 * Project related entities
 */

data class Project(
    val id: Long,
    val name: String,
    val slug: String,
    @SerializedName("i_am_member") val isMember: Boolean = false,
    @SerializedName("i_am_admin") val isAdmin: Boolean = false,
    @SerializedName("i_am_owner") val isOwner: Boolean = false,
    val description: String? = null,
    val avatarUrl: String? = null,
    val members: List<Long> = emptyList(),
    val fansCount: Int = 0,
    val watchersCount: Int = 0,
    val isPrivate: Boolean = false
)
