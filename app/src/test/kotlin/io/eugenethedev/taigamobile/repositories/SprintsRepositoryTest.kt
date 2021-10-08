package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.SprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.testdata.TestData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SprintsRepositoryTest : BaseRepositoryTest() {
    lateinit var sprintsRepository: ISprintsRepository

    @BeforeTest
    fun setupSprintsRepositoryTest() {
        sprintsRepository = SprintsRepository(mockTaigaApi, mockSession)
    }

    @Test
    fun `test get user stories`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        val testUserStories = TestData.projects[0].userstories.filter { it.sprint != null }

        sprints.forEach {
            sprintsRepository.getSprintUserStories(it.id).forEach { story ->
                assertEquals(
                    expected = testUserStories.find { it.name == story.title }!!.isClosed,
                    actual = story.isClosed
                )
            }
        }
    }

    @Test
    fun `test get sprints`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)

        assertEquals(
            expected = TestData.projects[0]
                .sprints
                .sortedBy { it.name }
                .map { it.name },
            actual = sprints.sortedBy { it.name }
                .map { it.name }
        )
    }

    @Test
    fun `test get sprint`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)

        sprints.forEach {
            sprintsRepository.getSprint(it.id).let { sprint ->
                TestData.projects[0].sprints.find { it.name == sprint.name }!!.let {
                    assertEquals(it.start, sprint.start)
                    assertEquals(it.end, sprint.end)
                }
            }
        }
    }

    @Test
    fun `test get sprint tasks`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        val testTasks = TestData.projects[0].sprints.associate { it.name to it.tasks }

        sprints.forEach { sprint ->
            sprintsRepository.getSprintTasks(sprint.id).forEach { task ->
                assertEquals(
                    expected = testTasks[sprint.name]!!.find { it.name == task.title }!!.isClosed,
                    actual = task.isClosed
                )
            }
        }
    }

    @Test
    fun `test get sprint issues`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        val testIssues = TestData.projects[0].issues
            .filter { it.sprint != null }
            .groupBy { it.sprint!!.name }

        sprints.forEach { sprint ->
            sprintsRepository.getSprintIssues(sprint.id).forEach { issue ->
                assertEquals(
                    expected = testIssues[sprint.name]!!.find { it.name == issue.title }!!.isClosed,
                    actual = issue.isClosed
                )
            }
        }
    }

    @Test
    fun `test create sprint`() = runBlocking {
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
        sprints.map { it.id }.forEach { id ->
            val title =  "editSprint$id"
            val start = LocalDate.of(2000 + id.toInt(), 1, 1)
            val end = LocalDate.of(3000 + id.toInt(), 1, 1)

            sprintsRepository.editSprint(id, title, start, end)

            val sprint = sprintsRepository.getSprint(id)
            assertEquals(
                expected = title,
                actual = sprint.name
            )
            assertEquals(
                expected = start,
                actual = sprint.start
            )
            assertEquals(
                expected = end,
                actual = sprint.end
            )
        }
    }

    @Test
    fun `test delete sprint`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)

        sprints.forEach {
            sprintsRepository.deleteSprint(it.id)
            assertFails { sprintsRepository.getSprint(it.id) }
        }
    }
}
