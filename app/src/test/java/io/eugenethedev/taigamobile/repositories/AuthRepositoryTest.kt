package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.AuthRepository
import io.eugenethedev.taigamobile.domain.repositories.IAuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
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

        mockServer.enqueue(MockResponse().setResponseCode(200).setBody("""
            {"id": 0, "username": "Test", "full_name": "Test", "full_name_display": "Test Test", "color": "#19db70", "bio": "", "lang": "", "theme": "", "timezone": "", "is_active": true, "photo": null, "big_photo": null, "gravatar_id": "1234567faf45", "roles": ["Product Owner"], "total_private_projects": 1, "total_public_projects": 1, "email": "test@test.com", "uuid": "000000000000000000", "date_joined": "2019-03-05T11:23:18.539Z", "read_new_terms": true, "accepted_terms": true, "max_private_projects": 1, "max_public_projects": null, "max_memberships_private_projects": 3, "max_memberships_public_projects": null, "verified_email": true, "refresh": "this7is7refresh", "auth_token": "this7is7auth"}
        """.trimIndent()))
        authRepository.auth(serverUrl, "test", "testpassword")

        assertEquals(serverUrl, mockSession.server)
        assertEquals(0, mockSession.currentUserId)
        assertEquals("this7is7auth", mockSession.token)
        assertEquals("this7is7refresh", mockSession.refreshToken)
    }
}
