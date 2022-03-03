package io.eugenethedev.taigamobile.domain.entities

import com.google.gson.annotations.SerializedName

/**
 * Users related entities
 */

data class User(
    @SerializedName("id") val _id: Long?,
    @SerializedName("full_name_display") val fullName: String?,
    val photo: String?,
    @SerializedName("big_photo") val bigPhoto: String?,
    val username: String,
    val name: String? = null, // sometimes name appears here
    val pk: Long? = null
) {
    val displayName get() = fullName ?: name!!
    val avatarUrl get() = bigPhoto ?: photo
    val id get() = _id ?: pk!!
}



data class TeamMember(
    val id: Long,
    val avatarUrl: String?,
    val name: String,
    val role: String,
    val username: String,
    val totalPower: Int
) {
    fun toUser() = User(
        _id = id,
        fullName = name,
        photo = avatarUrl,
        bigPhoto = null,
        username = username
    )
}

data class Stats(
    val roles: List<String> = emptyList(),
    @SerializedName("total_num_closed_userstories")
    val totalNumClosedUserStories: Int,
    @SerializedName("total_num_contacts")
    val totalNumContacts: Int,
    @SerializedName("total_num_projects")
    val totalNumProjects: Int,
)