package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.SprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.testdata.TestData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class SprintsRepositoryTest: BaseRepositoryTest() {
    lateinit var sprintsRepository : ISprintsRepository

    @BeforeTest
    fun setupSprintsRepositoryTest() {
        sprintsRepository = SprintsRepository(mockTaigaApi, mockSession)
    }

    @Test
    fun `test get user stories`() = runBlocking {
        val userStories1 = sprintsRepository.getSprintUserStories(1)
        val userStories2 = sprintsRepository.getSprintUserStories(2)

        val testUserStories = TestData.projects[0].userstories.filter { it.sprint != null }

        assertEquals(1, userStories1.size)
        assertEquals(expected = testUserStories[0].name, actual = userStories1[0].title)

        assertEquals(1, userStories2.size)
        assertEquals(expected = testUserStories[1].name, actual = userStories2[0].title)
    }

    @Test
    fun `test get sprints`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)

        assertEquals(TestData.projects[0].sprints.size, sprints.size)
        assertEquals(
            expected = TestData.projects[0].sprints.map { it.name },
            actual = sprints
                .sortedBy {it.name} //Upload in vice versa order, therefore need this
                .map { it.name }
        )
    }

    @Test
    fun `test get sprint`() = runBlocking {
        val sprint1 = sprintsRepository.getSprint(1)
        val sprint2 = sprintsRepository.getSprint(2)

        assertEquals(
            expected = TestData.projects[0].sprints[0].name,
            actual = sprint1.name
        )
        assertEquals(
            expected = TestData.projects[0].sprints[1].name,
            actual = sprint2.name
        )
    }

    @Test
    fun `test get sprint tasks`() = runBlocking {
        val sprintTasks = sprintsRepository.getSprintTasks(1)

        assertEquals(
            expected = TestData.projects[0].sprints[0].tasks.size,
            actual = sprintTasks.size
        )
        assertEquals(
            expected = TestData.projects[0].sprints[0].tasks.map { it.name },
            actual = sprintTasks.map { it.title }
        )
    }
}