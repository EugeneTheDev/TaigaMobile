package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.data.api.AuthRequest
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.repositories.IAuthRepository
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IAuthRepository {
    override suspend fun auth(taigaServer: String, password: String, username: String) = withIO {
        session.server = taigaServer
        session.token = taigaApi.auth(AuthRequest(password, username)).auth_token
    }

}