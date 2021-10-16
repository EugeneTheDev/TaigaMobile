package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.data.api.AuthRequest
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.entities.AuthType
import io.eugenethedev.taigamobile.domain.repositories.IAuthRepository
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IAuthRepository {
    override suspend fun auth(taigaServer: String, authType: AuthType, password: String, username: String) = withIO {
        session.changeServer(taigaServer)
        taigaApi.auth(
            AuthRequest(
                username = username,
                password = password,
                type = when (authType) {
                    AuthType.Normal -> "normal"
                    AuthType.LDAP -> "ldap"
                }
            )
        ).let {
            session.changeAuthCredentials(
                token = it.auth_token,
                refreshToken = it.refresh ?: "missing" // compatibility with older Taiga versions without refresh token
            )
            session.changeCurrentUserId(it.id)
        }
    }
}
