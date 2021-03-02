package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val taigaApi: TaigaApi
) : IUsersRepository {

    override suspend fun getUser(userId: Long) = withIO { taigaApi.getUser(userId) }
}