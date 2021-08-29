package io.eugenethedev.taigamobile.domain.repositories

interface IAuthRepository {
    suspend fun auth(taigaServer: String, password: String, username: String)
}