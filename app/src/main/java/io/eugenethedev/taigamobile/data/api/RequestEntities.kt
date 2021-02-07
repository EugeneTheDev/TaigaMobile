package io.eugenethedev.taigamobile.data.api

data class AuthRequest(
    val password: String,
    val username: String,
    val type: String = "normal"
)