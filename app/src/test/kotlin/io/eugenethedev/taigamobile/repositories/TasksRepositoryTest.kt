package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.TasksRepository
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.domain.entities.UsersFilter
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.repositories.utils.TestCommonTask
import io.eugenethedev.taigamobile.repositories.utils.getTestTasks
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
        val totalTestCommonTasks: MutableList<TestCommonTask> = mutableListOf()

        TestData.projects.forEachIndexed { index, project ->
            val epics = project.epics
                .filter { it.assignedTo == TestData.activeUser && !it.isClosed }
                .map {
                    TestCommonTask(
                        title = it.title,
                        assignee = it.assignedTo,
                        isClosed = it.isClosed
                    )
                }

            val stories = project.userstories
                .filter { it.assignedTo == TestData.activeUser && !it.isClosed }
                .map {
                    TestCommonTask(
                        title = it.title,
                        assignee = it.assignedTo,
                        isClosed = it.isClosed
                    )
                }

            val tasks = getTestTasks(index)
                .filter { it.assignedTo == TestData.activeUser && !it.isClosed }
                .map {
                    TestCommonTask(
                        title = it.title,
                        assignee = it.assignedTo,
                        isClosed = it.isClosed
                    )
                }

            val issue = project.issues
                .filter { it.assignedTo == TestData.activeUser && !it.isClosed }
                .map {
                    TestCommonTask(
                        title = it.title,
                        assignee = it.assignedTo,
                        isClosed = it.isClosed
                    )
                }
            totalTestCommonTasks += epics + stories + tasks + issue
        }
        totalTestCommonTasks.sortBy { it.title }
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
    fun `test get watching`() = runBlocking {
        val listCommonTasks = tasksRepository.getWatching().sortedBy { it.title }
        val totalTestCommonTasks: MutableList<TestCommonTask> = mutableListOf()

        TestData.projects.forEachIndexed { index, project ->
            val epics = project.epics
                .filter {
                    (TestData.activeUser in it.watchers || TestData.activeUser in it.comments.map { it.author })
                            && !it.isClosed
                }
                .map {
                    TestCommonTask(
                        title = it.title,
                        assignee = it.assignedTo,
                        isClosed = it.isClosed
                    )
                }

            val stories = project.userstories
                .filter {
                    (TestData.activeUser in it.watchers || TestData.activeUser in it.comments.map { it.author })
                            && !it.isClosed
                }
                .map {
                    TestCommonTask(
                        title = it.title,
                        assignee = it.assignedTo,
                        isClosed = it.isClosed
                    )
                }

            val tasks = getTestTasks(index)
                .filter {
                    (TestData.activeUser in it.watchers || TestData.activeUser in it.comments.map { it.author })
                            && !it.isClosed
                }
                .map {
                    TestCommonTask(
                        title = it.title,
                        assignee = it.assignedTo,
                        isClosed = it.isClosed
                    )
                }

            val issue = project.issues
                .filter {
                    (TestData.activeUser in it.watchers || TestData.activeUser in it.comments.map { it.author })
                            && !it.isClosed
                }
                .map {
                    TestCommonTask(
                        title = it.title,
                        assignee = it.assignedTo,
                        isClosed = it.isClosed
                    )
                }
            totalTestCommonTasks += epics + stories + tasks + issue
        }
        totalTestCommonTasks.sortBy { it.title }
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

//    @Test
//    fun `test get filter data`() = runBlocking {
//        CommonTaskType.values().forEach { type ->
//            val filter = tasksRepository.getFiltersData(type)
//            println(filter)
//        }
//    }

    @Test
    fun `test get epics`() = runBlocking {
        val epics = tasksRepository.getEpics(1, FiltersData()).sortedBy { it.title }
        val testEpics = TestData.projects[0].epics.sortedBy { it.title }
        val epicsWithFilter = tasksRepository.getEpics(
            page = 1,
            filters = FiltersData(
                createdBy = listOf(
                    UsersFilter(
                        id = 0,
                        name = activeUser.user.fullName,
                        count = 0
                    )
                )
            )
        ).sortedBy { it.title }
        val testEpicsWithFilter = TestData.projects[0].epics
            .filter { it.creator.fullName == activeUser.user.fullName }
            .sortedBy { it.title }

        assertEquals(
            expected = epics.size,
            actual = testEpics.size
        )
        testEpics.forEachIndexed() { index, testEpic ->
            assertEquals(
                expected = testEpic.title,
                actual = epics[index].title
            )
            assertEquals(
                expected = testEpic.assignedTo?.fullName,
                actual = epics[index].assignee?.fullName
            )
            assertEquals(
                expected = testEpic.isClosed,
                actual = epics[index].isClosed
            )
        }

        assertEquals(
            expected = epicsWithFilter.size,
            actual = testEpicsWithFilter.size
        )
        testEpicsWithFilter.forEachIndexed() { index, testEpic ->
            assertEquals(
                expected = testEpic.title,
                actual = epicsWithFilter[index].title
            )
            assertEquals(
                expected = testEpic.assignedTo?.fullName,
                actual = epicsWithFilter[index].assignee?.fullName
            )
            assertEquals(
                expected = testEpic.isClosed,
                actual = epicsWithFilter[index].isClosed
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

    @Test
    fun `test get backlog user stories`() = runBlocking {
        val listUserStories = tasksRepository
            .getBacklogUserStories(1, FiltersData())
            .sortedBy { it.title }
        val listTestUserStories = TestData.projects[0].userstories
            .filter { it.sprint == null }

        assertEquals(
            expected = listTestUserStories.size,
            actual = listUserStories.size
        )
        listUserStories.forEachIndexed { index, story ->
            assertEquals(
                expected = story.title,
                actual = listTestUserStories[index].title
            )
            assertEquals(
                expected = story.assignee?.fullName,
                actual = listTestUserStories[index].assignedTo?.fullName
            )
            assertEquals(
                expected = story.isClosed,
                actual = listTestUserStories[index].isClosed
            )
        }
    }

    @Test
    fun `test get epic user stories`() = runBlocking {
        tasksRepository.getEpics(1, FiltersData()).forEach { epic ->
            val epicsUserStories = tasksRepository.getEpicUserStories(epic.id).sortedBy { it.title }
            val testEpicsUserStories = TestData.projects[0].userstories
                .filter { epic.title in it.epics.map { it.title } }
                .sortedBy { it.title }

            assertEquals(
                expected = epicsUserStories.size,
                actual = testEpicsUserStories.size
            )
            epicsUserStories.forEachIndexed { index, story ->
                assertEquals(
                    expected = testEpicsUserStories[index].title,
                    actual = story.title
                )
                assertEquals(
                    expected = testEpicsUserStories[index].assignedTo?.fullName,
                    actual = story.assignee?.fullName
                )
                assertEquals(
                    expected = testEpicsUserStories[index].isClosed,
                    actual = story.isClosed
                )
            }
        }
    }

    @Test
    fun `test get user story tasks`() = runBlocking {
        tasksRepository.getAllUserStories().forEach { story ->
            val tasks = tasksRepository.getUserStoryTasks(story.id).sortedBy { it.title }
            val testTask = TestData.projects[0].userstories
                .find { it.title == story.title }
                ?.tasks
                ?.sortedBy { it.title }

            assertEquals(
                expected = testTask?.size,
                actual = tasks.size
            )
            if (testTask != null) {
                tasks.forEachIndexed { index, task ->
                    assertEquals(
                        expected = testTask[index].title,
                        actual = task.title
                    )
                    assertEquals(
                        expected = testTask[index].assignedTo?.fullName,
                        actual = task.assignee?.fullName
                    )
                    assertEquals(
                        expected = testTask[index].isClosed,
                        actual = task.isClosed
                    )
                }
            }
        }
    }

    @Test
    fun `test get issues`() = runBlocking {
        val issues = tasksRepository.getIssues(1, FiltersData()).sortedBy { it.title }
        val testIssues = TestData.projects[0].issues.sortedBy { it.title }

        assertEquals(
            expected = testIssues.size,
            actual = issues.size
        )
        issues.forEachIndexed { index, issue ->
            assertEquals(
                expected = testIssues[index].title,
                actual = issue.title
            )
            assertEquals(
                expected = testIssues[index].assignedTo?.fullName,
                actual = issue.assignee?.fullName
            )
            assertEquals(
                expected = testIssues[index].isClosed,
                actual = issue.isClosed
            )
        }
    }
}