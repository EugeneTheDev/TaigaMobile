package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.TasksRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.testdata.TestData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals


class TasksRepositoryTest : BaseRepositoryTest() {
    lateinit var tasksRepository: ITasksRepository

    @BeforeTest
    fun setupSprintsRepositoryTest() {
        tasksRepository = TasksRepository(mockTaigaApi, mockSession)
    }

    @Test
    fun `test get working on`() = runBlocking {
        val listCommonTasks = tasksRepository.getWorkingOn().sortedBy { it.title }

        val epics = TestData.projects[0].epics
            .filter { it.assignedTo == TestData.activeUser && !it.isClosed }
            .map{ TestCommonTask(
                title = it.title,
                assignee = it.assignedTo,
                isClosed = it.isClosed
            )}

        val stories = TestData.projects[0].userstories
            .filter { it.assignedTo == TestData.activeUser && !it.isClosed}
            .map{ TestCommonTask(
                title = it.title,
                assignee = it.assignedTo,
                isClosed = it.isClosed
            )}

        val tasks = getTestTasks()
            .filter { it.assignedTo == TestData.activeUser && !it.isClosed}
            .map{ TestCommonTask(
                title = it.title,
                assignee = it.assignedTo,
                isClosed = it.isClosed
            )}

        val issue = TestData.projects[0].issues
            .filter { it.assignedTo == TestData.activeUser && !it.isClosed}
            .map{ TestCommonTask(
                title = it.title,
                assignee = it.assignedTo,
                isClosed = it.isClosed
            )}

        val totalTestCommonTasks = (epics + stories + tasks + issue).sortedBy { it.title }

        listCommonTasks.forEachIndexed { index, data ->
            assertEquals(
                expected = totalTestCommonTasks[index].title,
                actual = data.title
            )
            assertEquals(
                expected = totalTestCommonTasks[index].assignee?.fullName,
                actual = data.assignee?.fullName
            )
            assertEquals(
                expected = totalTestCommonTasks[index].isClosed,
                actual = data.isClosed
            )
        }
    }

    @Test
    fun `test get all user stories`() = runBlocking {
        val userStories = tasksRepository.getAllUserStories().sortedBy { it.title }
        val testUsersStory = TestData.projects[0].userstories.sortedBy { it.title }

        userStories.forEachIndexed { index, story ->
            assertEquals(
                expected = testUsersStory[index].title,
                actual = story.title
            )
            assertEquals(
                expected = testUsersStory[index].isClosed,
                actual = story.isClosed
            )
        }
    }
}