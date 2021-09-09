package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.AuthRepository
import io.eugenethedev.taigamobile.domain.repositories.IAuthRepository
import io.eugenethedev.taigamobile.testdata.TestData
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AuthRepositoryTest : BaseRepositoryTest() {
    lateinit var authRepository: IAuthRepository

    @BeforeTest
    fun setupAuthRepositoryTest() {
        authRepository = AuthRepository(mockTaigaApi, mockSession)
    }

    @Test
    fun `test basic auth`() = runBlocking {
        val serverUrl = mockSession.server
        mockSession.reset()

        assertFalse(mockSession.isLogged)

        with(activeUser) {
            authRepository.auth(serverUrl, user.password, user.username)
            assertEquals(serverUrl, mockSession.server)
            assertEquals(data.id, mockSession.currentUserId)
        }
    }

    @Test
    fun `test refresh auth token`() = runBlocking {
        mockSession.token = "wrong token" // simulate token expiration (token is not valid anymore)
        mockTaigaApi.getProject(activeUser.projects.keys.first()) // successful response (because refresh happens)
        return@runBlocking
    }
}
