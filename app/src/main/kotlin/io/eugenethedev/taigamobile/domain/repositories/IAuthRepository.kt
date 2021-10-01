package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.AuthType

interface IAuthRepository {
    suspend fun auth(taigaServer: String, authType: AuthType, password: String, username: String)
}