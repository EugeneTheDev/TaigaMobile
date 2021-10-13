package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.UsersRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.testdata.TestData
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import org.junit.Test
import kotlin.test.assertEquals

class UsersRepositoryTest : BaseRepositoryTest() {
    lateinit var usersRepository: IUsersRepository

    @BeforeTest
    fun setupSprintsRepositoryTest() {
        usersRepository = UsersRepository(mockTaigaApi, mockSession)
    }

    @Test
    fun `test getMe`() = runBlocking {
        val user = usersRepository.getMe()

        assertEquals(
            expected = user.username,
            actual = TestData.activeUser.username
        )
        assertEquals(
            expected = user.fullName,
            actual = TestData.activeUser.fullName
        )
    }
}