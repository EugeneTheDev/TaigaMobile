package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.SprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.testdata.TestData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SprintsRepositoryTest: BaseRepositoryTest() {
    lateinit var sprintsRepository : ISprintsRepository

    @BeforeTest
    fun setupSprintsRepositoryTest() {
        sprintsRepository = SprintsRepository(mockTaigaApi, mockSession)
    }

    @Test
    fun `test get user stories`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        val testUserStories = TestData.projects[0].userstories.filter { it.sprint != null }
        sprints.map { it.id }.forEach{ index ->
        val userStories = sprintsRepository.getSprintUserStories(index)
            assertEquals(
                expected = testUserStories[index.toInt() - 1].name,
                actual = userStories[0].title
            )
            assertEquals(
                expected = testUserStories[index.toInt() - 1].isClosed,
                actual = userStories[0].isClosed
            )
        }
    }

    @Test
    fun `test get sprints`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)

        assertEquals(
            expected = TestData
                .projects[0]
                .sprints
                .sortedBy {it.name}
                .map { it.name },
            actual = sprints
                .sortedBy {it.name}
                .map { it.name }
        )
    }

    @Test
    fun `test get sprint`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        sprints.map { it.id }.forEach{ index ->
        val sprint = sprintsRepository.getSprint(index)
            assertEquals(
                expected = TestData.projects[0].sprints[index.toInt() - 1].name,
                actual = sprint.name
            )
            assertEquals(
                expected = TestData.projects[0].sprints[index.toInt() - 1].start,
                actual = sprint.start
            )
            assertEquals(
                expected = TestData.projects[0].sprints[index.toInt() - 1].end,
                actual = sprint.end
            )
        }
    }

    @Test
    fun `test get sprint tasks`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        sprints.map { it.id }.forEach{ index ->
            val sprintTasks = sprintsRepository.getSprintTasks(index)
            assertEquals(
                expected = TestData.projects[0].sprints[index.toInt() - 1].tasks.map { it.name },
                actual = sprintTasks.map { it.title }
            )
            assertEquals(
                expected = TestData.projects[0].sprints[index.toInt() - 1].tasks.map { it.isClosed },
                actual = sprintTasks.map { it.isClosed }
            )
        }
    }

    @Test
    fun `test get sprint issues`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        sprints.map { it.id }.forEach{ index ->
            val sprintIssues = sprintsRepository.getSprintIssues(index)
            assertEquals(
                expected = TestData.projects[0].issues[index.toInt() - 1].name,
                actual = sprintIssues[0].title
            )
        }
    }

    @Test
    fun `test create sprint`() = runBlocking{
        sprintsRepository.createSprint(
            "testSprint",
            LocalDate.of(2000, 1, 1),
            LocalDate.of(3000, 1, 1)
        )

        val sprints = sprintsRepository.getSprints(1)
        assertEquals(
            expected = "testSprint",
            actual = sprints.last().name
        )
        assertEquals(
            expected = LocalDate.of(2000, 1, 1),
            actual = sprints.last().start
        )
        assertEquals(
            expected = LocalDate.of(3000, 1, 1),
            actual = sprints.last().end
        )
    }

    @Test
    fun `test edit sprint`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        sprints.map { it.id }.forEach{ index ->
            sprintsRepository.editSprint(
                index,
                "editSprint${index}",
                LocalDate.of(2000 + index.toInt(), 1, 1),
                LocalDate.of(3000 + index.toInt(), 1, 1)
            )
            val sprint = sprintsRepository.getSprint(index)
            assertEquals(
                expected = "editSprint${index}",
                actual = sprint.name
            )
            assertEquals(
                expected = LocalDate.of(2000 + index.toInt(), 1, 1),
                actual = sprint.start
            )
            assertEquals(
                expected = LocalDate.of(3000 + index.toInt(), 1, 1),
                actual = sprint.end
            )
        }
    }

    @Test
    fun `test delete sprint`() = runBlocking{
        val sprints = sprintsRepository.getSprints(1)
        sprints.sortedBy { it.id }.map { it.id }.forEach{ index ->
            val nameSprint = sprintsRepository.getSprint(index).name
            sprintsRepository.deleteSprint(index)
            val sprintsNew = sprintsRepository.getSprints(1)
            assertEquals(
                expected = TestData.projects[0].sprints.size - index.toInt(),
                actual = sprintsNew.size
            )
            assertTrue(nameSprint !in sprintsNew.map { it.name })
        }
    }
}