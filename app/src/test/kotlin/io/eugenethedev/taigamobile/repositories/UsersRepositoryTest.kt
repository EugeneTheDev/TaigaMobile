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

    @Test
    fun `test getTeam`() = runBlocking {
        val team = usersRepository.getTeam().sortedBy { it.username }
        val testTeam = TestData.users.sortedBy { it.username }

        team.forEachIndexed { index, member ->
            assertEquals(
                expected = testTeam[index].username,
                actual = member.username
            )
            assertEquals(
                expected = testTeam[index].fullName,
                actual = member.name
            )
        }
    }

    @Test
    fun `test getUser`() = runBlocking {
        val testUsers = TestData.users.sortedBy { it.username }

        usersRepository.getTeam().sortedBy { it.username }.forEachIndexed { index, member ->
            val user = usersRepository.getUser(member.id)
            assertEquals(
                expected = testUsers[index].username,
                actual = user.username
            )
            assertEquals(
                expected = testUsers[index].fullName,
                actual = user.fullName
            )
        }
    }
}