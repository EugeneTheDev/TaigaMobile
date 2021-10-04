package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.SprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.testdata.TestData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDate
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

        assertEquals(
            expected = 1,
            userStories1.size
        )
        assertEquals(
            expected = testUserStories[0].name,
            actual = userStories1[0].title
        )

        assertEquals(
            expected = 1,
            actual = userStories2.size
        )
        assertEquals(
            expected = testUserStories[1].name,
            actual = userStories2[0].title
        )
    }

    @Test
    fun `test get sprints`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)

        assertEquals(
            expected = TestData.projects[0].sprints.size,
            actual = sprints.size
        )
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
        val sprintTasks1 = sprintsRepository.getSprintTasks(1)
        val sprintTasks2 = sprintsRepository.getSprintTasks(2)

        assertEquals(
            expected = TestData.projects[0].sprints[0].tasks.size,
            actual = sprintTasks1.size
        )
        assertEquals(
            expected = TestData.projects[0].sprints[0].tasks.map { it.name },
            actual = sprintTasks1.map { it.title }
        )

        assertEquals(
            expected = TestData.projects[0].sprints[1].tasks.size,
            actual = sprintTasks2.size
        )
        assertEquals(
            expected = TestData.projects[0].sprints[1].tasks.map { it.name },
            actual = sprintTasks2.map { it.title }
        )
    }

    @Test
    fun `test get sprint issues`() = runBlocking {
        val sprintIssues1 = sprintsRepository.getSprintIssues(1)
        val sprintIssues2 = sprintsRepository.getSprintIssues(2) //And were is Issue 3?...

        assertEquals(
            expected = TestData.projects[0].issues
                .filter { it.sprint == TestData.projects[0].sprints[0]}
                .size,
            actual = sprintIssues1.size
        )
        assertEquals(
            expected = TestData.projects[0].issues
                .filter { it.sprint == TestData.projects[0].sprints[0]}
                .map { it.name },
            actual = sprintIssues1.map { it.title }
        )

        assertEquals(
            expected = TestData.projects[0].issues
                .filter { it.sprint == TestData.projects[0].sprints[1]}
                .size,
            actual = sprintIssues2.size
        )
        assertEquals(
            expected = TestData.projects[0].issues
                .filter { it.sprint == TestData.projects[0].sprints[1]}
                .map { it.name },
            actual = sprintIssues2.map { it.title }
        )
    }

    @Test
    fun `test create sprint`() = runBlocking{
        sprintsRepository.createSprint(
            "testSprint",
            LocalDate.of(2000, 1, 1),
            LocalDate.of(3000, 1, 1)
        )

        val sprint = sprintsRepository.getSprints(1)
        assertEquals(
            expected = "testSprint",
            actual = sprint.last().name
        )
        assertEquals(
            expected = LocalDate.of(2000, 1, 1),
            actual = sprint.last().start
        )
        assertEquals(
            expected = LocalDate.of(3000, 1, 1),
            actual = sprint.last().end
        )
    }
}