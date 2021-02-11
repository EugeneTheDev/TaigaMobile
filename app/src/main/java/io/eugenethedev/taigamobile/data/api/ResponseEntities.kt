package io.eugenethedev.taigamobile.data.api

data class AuthResponse(
    val auth_token: String
)

data class ProjectResponse(
    val id: Int,
    val name: String
)