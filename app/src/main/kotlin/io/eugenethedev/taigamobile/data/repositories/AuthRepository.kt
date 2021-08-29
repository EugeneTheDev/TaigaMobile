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
        taigaApi.auth(AuthRequest(password, username)).let {
            session.token = it.auth_token
            session.refreshToken = it.refresh ?: "missing" // compatibility with older Taiga versions without refresh token
            session.currentUserId = it.id
        }
    }
}
