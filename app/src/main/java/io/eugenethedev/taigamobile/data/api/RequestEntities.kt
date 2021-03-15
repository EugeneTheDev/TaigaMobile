package io.eugenethedev.taigamobile.data.api

data class AuthRequest(
    val password: String,
    val username: String,
    val type: String = "normal"
)

data class ChangeStatusRequest(
    val status: Long,
    val version: Int
)