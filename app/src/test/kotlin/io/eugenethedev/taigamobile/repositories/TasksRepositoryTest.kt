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

    @Test
    fun `test get common task`() = runBlocking {
        val userStories = TestData.projects[0].userstories + TestData.projects[1].userstories
        val tasks =
            TestData.projects[0].sprints.flatMap { it.tasks } + TestData.projects[1].sprints.flatMap { it.tasks }
        val epics = TestData.projects[0].epics + TestData.projects[1].epics
        val issue = TestData.projects[0].issues + TestData.projects[1].issues
        val dataForTest = hashMapOf(
            CommonTaskType.UserStory to userStories,
            CommonTaskType.Task to tasks,
            CommonTaskType.Epic to epics,
            CommonTaskType.Issue to issue
        )

        CommonTaskType.values().forEach { type ->
            for (index in 1..dataForTest[type]?.size!!) {
                val commonTaskExt = tasksRepository.getCommonTask(index.toLong(), type)
                assertEquals(
                    expected = dataForTest[type]?.get(index - 1)?.title,
                    actual = commonTaskExt.title
                )
                assertEquals(
                    expected = dataForTest[type]?.get(index - 1)?.isClosed,
                    actual = commonTaskExt.isClosed
                )
                assertEquals(
                    expected = dataForTest[type]?.get(index - 1)?.description,
                    actual = commonTaskExt.description
                )
            }
        }
    }

    @Test
    fun `test get comments`() = runBlocking {
        val userStories = TestData.projects[0].userstories +
                TestData.projects[1].userstories
        val tasks = TestData.projects[0].sprints.flatMap { it.tasks } +
                TestData.projects[1].sprints.flatMap { it.tasks }
        val epics = TestData.projects[0].epics + TestData.projects[1].epics
        val issue = TestData.projects[0].issues + TestData.projects[1].issues
        val dataForTest = hashMapOf(
            CommonTaskType.UserStory to userStories,
            CommonTaskType.Task to tasks,
            CommonTaskType.Epic to epics,
            CommonTaskType.Issue to issue
        )

        CommonTaskType.values().forEach { type ->
            for (index in 1..dataForTest[type]?.size!!) {
                val comments = tasksRepository.getComments(index.toLong(), type)
                val testComments = dataForTest[type]?.get(index - 1)?.comments

                assertEquals(
                    expected = testComments?.size,
                    actual = comments.size
                )
                comments.forEachIndexed { ind, comment ->
                    assertEquals(
                        expected = testComments?.get(ind)?.text,
                        actual = comment.text
                    )
                }
            }
        }
    }

    @Test
    fun `test change status`() = runBlocking {
        val issues = tasksRepository.getIssues(1, FiltersData())
        val statuses = tasksRepository.getStatuses(CommonTaskType.Issue)

        issues.forEachIndexed { index, issue ->
            val commonTask = tasksRepository.getCommonTask(issue.id, CommonTaskType.Issue)
            tasksRepository.changeStatus(
                commonTask.id,
                commonTask.taskType,
                statuses[index % statuses.size].id,
                statuses[index % statuses.size].type,
                commonTask.version
            )
            val commonTaskAfterChange = tasksRepository.getCommonTask(issue.id, CommonTaskType.Issue)

            assertEquals(
                expected = commonTask.id,
                actual = commonTaskAfterChange.id
            )
            assertEquals(
                expected = statuses[index % statuses.size].id,
                actual = commonTaskAfterChange.status.id
            )
            assertEquals(
                expected = statuses[index % statuses.size].type,
                actual = commonTaskAfterChange.status.type
            )
            assertEquals(
                expected = commonTask.version + 1,
                actual = commonTaskAfterChange.version
            )
        }
    }
}