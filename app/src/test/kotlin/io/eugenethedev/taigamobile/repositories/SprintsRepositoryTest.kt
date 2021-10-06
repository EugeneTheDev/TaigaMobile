package io.eugenethedev.taigamobile.repositories

import android.util.Log
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
        val sprints = sprintsRepository.getSprints(1)
        val testUserStories = TestData.projects[0].userstories.filter { it.sprint != null }
        if (sprints.isNotEmpty()) {
            for (i in 1..sprints.size) {
                val userStories = sprintsRepository.getSprintUserStories(i.toLong())
                assertEquals(
                    expected = testUserStories[i - 1].name,
                    actual = userStories[0].title
                )
                assertEquals(
                    expected = testUserStories[i - 1].isClosed,
                    actual = userStories[0].isClosed
                )
            }
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
        if (sprints.isNotEmpty()) {
            for (i in 1..sprints.size) {
                val sprint = sprintsRepository.getSprint(i.toLong())
                assertEquals(
                    expected = TestData.projects[0].sprints[i - 1].name,
                    actual = sprint.name
                )
            }
        }
    }

    @Test
    fun `test get sprint tasks`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        if (sprints.isNotEmpty()) {
            for (i in 1..sprints.size) {
                val sprintTasks = sprintsRepository.getSprintTasks(i.toLong())
                assertEquals(
                    expected = TestData.projects[0].sprints[i - 1].tasks.size,
                    actual = sprintTasks.size
                )
                assertEquals(
                    expected = TestData.projects[0].sprints[i - 1].tasks.map { it.name },
                    actual = sprintTasks.map { it.title }
                )
            }
        }
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
        sprintsRepository.editSprint(
            1,
            "editSprint",
            LocalDate.of(2000, 1, 1),
            LocalDate.of(3000, 1, 1)
        )

        val sprint = sprintsRepository.getSprint(1)
        assertEquals(
            expected = "editSprint",
            actual = sprint.name
        )
        assertEquals(
            expected = LocalDate.of(2000, 1, 1),
            actual = sprint.start
        )
        assertEquals(
            expected = LocalDate.of(3000, 1, 1),
            actual = sprint.end
        )
    }

    @Test
    fun `test delete sprint`() = runBlocking{
        val nameSprint = sprintsRepository.getSprint(1).name
        sprintsRepository.deleteSprint(1)
        val sprints = sprintsRepository.getSprints(1)

        assertEquals(
            expected = TestData.projects[0].sprints.size - 1,
            actual = sprints.size
        )
        assert(nameSprint !in sprints.map { it.name })
    }
}