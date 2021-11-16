package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.SprintsRepository
import io.eugenethedev.taigamobile.data.repositories.TasksRepository
import io.eugenethedev.taigamobile.data.repositories.UsersRepository
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.repositories.utils.TestCommonTask
import io.eugenethedev.taigamobile.repositories.utils.getTestTasks
import io.eugenethedev.taigamobile.testdata.TestData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDate
import kotlin.test.*


class TasksRepositoryTest : BaseRepositoryTest() {
    lateinit var tasksRepository: ITasksRepository
    lateinit var sprintsRepository: ISprintsRepository
    lateinit var usersRepository: IUsersRepository

    @BeforeTest
    fun setupSprintsRepositoryTest() {
        tasksRepository = TasksRepository(mockTaigaApi, mockSession)
        sprintsRepository = SprintsRepository(mockTaigaApi, mockSession)
        usersRepository = UsersRepository(mockTaigaApi, mockSession)
    }

    private fun testCommonTaskEquality(
        expectedList: List<TestCommonTask>,
        actualList: List<TestCommonTask>
    ) {

        assertEquals(
            expected = expectedList.size,
            actual = actualList.size
        )
        actualList.forEachIndexed { index, data ->
            assertEquals(
                expected = expectedList[index].title,
                actual = data.title
            )
            assertEquals(
                expected = expectedList[index].assigneeFullName,
                actual = data.assigneeFullName
            )
            assertEquals(
                expected = expectedList[index].isClosed,
                actual = data.isClosed
            )
        }
    }

    private fun testCommonTaskExtEquality(
        expectedTask: CommonTaskExtended,
        actualTask: CommonTaskExtended
    ) {
        assertEquals(
            expected = expectedTask.id,
            actual = actualTask.id
        )
        assertEquals(
            expected = expectedTask.version + 1,
            actual = actualTask.version
        )
    }

    private suspend fun getCommonTasksExt(
        types: List<CommonTaskType>
    ): MutableMap<CommonTaskType, List<CommonTaskExtended>> {
        val dataForTest = mutableMapOf<CommonTaskType, List<CommonTaskExtended>>()
        val userStories = tasksRepository.getAllUserStories()
        types.forEach { type ->
            when (type) {
                CommonTaskType.Epic -> {
                    dataForTest[CommonTaskType.Epic] = tasksRepository
                        .getEpics(1, FiltersData())
                        .map {
                            tasksRepository.getCommonTask(it.id, it.taskType)
                        }
                }
                CommonTaskType.Issue -> {
                    dataForTest[CommonTaskType.Issue] = tasksRepository
                        .getIssues(1, FiltersData())
                        .map {
                            tasksRepository.getCommonTask(it.id, it.taskType)
                        }
                }
                CommonTaskType.Task -> {
                    dataForTest[CommonTaskType.Task] = userStories
                        .flatMap { tasksRepository.getUserStoryTasks(it.id) }
                        .map {
                            tasksRepository.getCommonTask(it.id, it.taskType)
                        }
                }
                CommonTaskType.UserStory -> {
                    dataForTest[CommonTaskType.UserStory] = userStories
                }
            }
        }
        return dataForTest
    }

    @Test
    fun `test get working on`() = runBlocking {
        val listCommonTasks = tasksRepository.getWorkingOn()
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignee?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }
        val totalTestCommonTasks = TestData.projects
            .flatMapIndexed { index, project -> project.epics + getTestTasks(index) + project.issues + project.userstories }
            .filter { it.assignedTo == TestData.activeUser && !it.isClosed }
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignedTo?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }

        testCommonTaskEquality(totalTestCommonTasks, listCommonTasks)
    }

    @Test
    fun `test get watching`() = runBlocking {
        val listCommonTasks = tasksRepository.getWatching()
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignee?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }
        val totalTestCommonTasks = TestData.projects
            .flatMapIndexed { index, project -> project.epics + getTestTasks(index) + project.issues + project.userstories }
            .filter { commonTask ->
                (TestData.activeUser in commonTask.watchers || TestData.activeUser in commonTask.comments.map { it.author })
                        && !commonTask.isClosed
            }
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignedTo?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }

        testCommonTaskEquality(totalTestCommonTasks, listCommonTasks)
    }

    @Test
    fun `test get epics`() = runBlocking {
        val epics = tasksRepository.getEpics(1, FiltersData())
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignee?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }
        val testEpics = TestData.projects[0].epics
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignedTo?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }

        testCommonTaskEquality(testEpics, epics)
    }

    @Test
    fun `test get all user stories`() = runBlocking {
        val userStories = tasksRepository.getAllUserStories()
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = null,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }
        val testUsersStory = TestData.projects[0].userstories
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = null,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }

        testCommonTaskEquality(testUsersStory, userStories)
    }

    @Test
    fun `test get backlog user stories`() = runBlocking {
        val listUserStories = tasksRepository
            .getBacklogUserStories(1, FiltersData())
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignee?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }
        val listTestUserStories = TestData.projects[0].userstories
            .filter { it.sprint == null }
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignedTo?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }

        testCommonTaskEquality(listTestUserStories, listUserStories)
    }

    @Test
    fun `test get epic user stories`() = runBlocking {
        tasksRepository.getEpics(1, FiltersData()).forEach { epic ->
            val epicsUserStories = tasksRepository.getEpicUserStories(epic.id)
                .map {
                    TestCommonTask(
                        title = it.title,
                        assigneeFullName = it.assignee?.fullName,
                        isClosed = it.isClosed
                    )
                }
                .sortedBy { it.title }
            val testEpicsUserStories = TestData.projects[0].userstories
                .filter { epic.title in it.epics.map { it.title } }
                .map {
                    TestCommonTask(
                        title = it.title,
                        assigneeFullName = it.assignedTo?.fullName,
                        isClosed = it.isClosed
                    )
                }
                .sortedBy { it.title }

            testCommonTaskEquality(testEpicsUserStories, epicsUserStories)
        }
    }

    @Test
    fun `test get user story tasks`() = runBlocking {
        tasksRepository.getAllUserStories().forEach { story ->
            val tasks = tasksRepository.getUserStoryTasks(story.id)
                .map {
                    TestCommonTask(
                        title = it.title,
                        assigneeFullName = it.assignee?.fullName,
                        isClosed = it.isClosed
                    )
                }
                .sortedBy { it.title }
            val testTask = TestData.projects[0].userstories
                .find { it.title == story.title }
                ?.tasks
                ?.map {
                    TestCommonTask(
                        title = it.title,
                        assigneeFullName = it.assignedTo?.fullName,
                        isClosed = it.isClosed
                    )
                }
                ?.sortedBy { it.title }

            testTask?.let {
                testCommonTaskEquality(it, tasks)
            }
        }
    }

    @Test
    fun `test get issues`() = runBlocking {
        val issues = tasksRepository.getIssues(1, FiltersData())
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignee?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }
        val testIssues = TestData.projects[0].issues
            .map {
                TestCommonTask(
                    title = it.title,
                    assigneeFullName = it.assignedTo?.fullName,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }

        testCommonTaskEquality(testIssues, issues)
    }

    @Test
    fun `test get common task`() = runBlocking {
        val userStories = TestData.projects[0].userstories + TestData.projects[1].userstories
        val tasks =
            TestData.projects[0].sprints.flatMap { it.tasks } + TestData.projects[1].sprints.flatMap { it.tasks }
        val epics = TestData.projects[0].epics + TestData.projects[1].epics
        val issue = TestData.projects[0].issues + TestData.projects[1].issues
        val dataForTest = mapOf(
            CommonTaskType.UserStory to userStories,
            CommonTaskType.Task to tasks,
            CommonTaskType.Epic to epics,
            CommonTaskType.Issue to issue
        )

        CommonTaskType.values().forEach { type ->
            for ((index, task) in dataForTest[type]?.withIndex()!!) {
                val commonTaskExt = tasksRepository.getCommonTask(index.toLong() + 1, type)
                assertEquals(
                    expected = task.title,
                    actual = commonTaskExt.title
                )
                assertEquals(
                    expected = task.isClosed,
                    actual = commonTaskExt.isClosed
                )
                assertEquals(
                    expected = task.description,
                    actual = commonTaskExt.description
                )
            }
        }
    }

    @Test
    fun `test get comments`() = runBlocking {
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
            for ((index, task) in dataForTest[type]?.withIndex()!!) {
                val comments = tasksRepository.getComments(index.toLong() + 1, type)
                val testComments = task.comments

                assertEquals(
                    expected = testComments.size,
                    actual = comments.size
                )
                comments.forEachIndexed { ind, comment ->
                    assertEquals(
                        expected = testComments[ind].text,
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
            val commonTaskAfterChange =
                tasksRepository.getCommonTask(issue.id, CommonTaskType.Issue)

            testCommonTaskExtEquality(commonTask, commonTaskAfterChange)
            assertEquals(
                expected = statuses[index % statuses.size].id,
                actual = commonTaskAfterChange.status.id
            )
            assertEquals(
                expected = statuses[index % statuses.size].type,
                actual = commonTaskAfterChange.status.type
            )
        }
    }

    @Test
    fun `test change sprint`() = runBlocking {
        val issues = tasksRepository.getIssues(1, FiltersData())
        val userStories = tasksRepository.getAllUserStories()
        val sprints = sprintsRepository.getSprints(1) + sprintsRepository.getSprints(1, true)

        issues.forEachIndexed { index, issue ->
            val commonTask = tasksRepository.getCommonTask(issue.id, CommonTaskType.Issue)
            tasksRepository.changeSprint(
                commonTask.id,
                commonTask.taskType,
                sprints[index % sprints.size].id,
                commonTask.version
            )
            val commonTaskAfterChange =
                tasksRepository.getCommonTask(issue.id, CommonTaskType.Issue)

            testCommonTaskExtEquality(commonTask, commonTaskAfterChange)
            assertEquals(
                expected = sprints[index % sprints.size].id,
                actual = commonTaskAfterChange.sprint?.id
            )
            assertEquals(
                expected = sprints[index % sprints.size].name,
                actual = commonTaskAfterChange.sprint?.name
            )
        }

        userStories.forEachIndexed { index, story ->
            tasksRepository.changeSprint(
                story.id,
                story.taskType,
                sprints[index % sprints.size].id,
                story.version
            )
            val storyAfterChange = tasksRepository.getCommonTask(story.id, CommonTaskType.UserStory)

            testCommonTaskExtEquality(story, storyAfterChange)
            assertEquals(
                expected = sprints[index % sprints.size].id,
                actual = storyAfterChange.sprint?.id
            )
            assertEquals(
                expected = sprints[index % sprints.size].name,
                actual = storyAfterChange.sprint?.name
            )
        }
    }

    @Test
    fun `test link to epic`() = runBlocking {
        val epics = tasksRepository.getEpics(1, FiltersData())
        val userStories = tasksRepository.getAllUserStories()

        epics.forEachIndexed { index, epic ->
            tasksRepository.linkToEpic(epic.id, userStories[(index + 1) % userStories.size].id)
            val userStoryLink = tasksRepository.getEpicUserStories(epic.id)
                .find { it.id == userStories[(index + 1) % userStories.size].id }

            assertEquals(
                expected = userStories[(index + 1) % userStories.size].title,
                actual = userStoryLink?.title
            )
            assertEquals(
                expected = userStories[(index + 1) % userStories.size].isClosed,
                actual = userStoryLink?.isClosed
            )
            assertEquals(
                expected = userStories[(index + 1) % userStories.size].status,
                actual = userStoryLink?.status
            )
        }
    }

    @Test
    fun `test unlink to epic`() = runBlocking {
        val epics = tasksRepository.getEpics(1, FiltersData())

        epics.forEach { epic ->
            tasksRepository.getEpicUserStories(epic.id).forEach { story ->
                tasksRepository.unlinkFromEpic(epic.id, story.id)
                val currentUserStories = tasksRepository.getEpicUserStories(epic.id)
                assertTrue(
                    story.id !in currentUserStories.map { it.id }
                )
            }
        }
    }


    @Test
    fun `test change assignees`() = runBlocking {
        val users = usersRepository.getTeam()
        val dataForTest = getCommonTasksExt(
            listOf(
                CommonTaskType.Epic,
                CommonTaskType.Issue,
                CommonTaskType.Task,
                CommonTaskType.UserStory
            )
        )

        CommonTaskType.values().forEach { type ->
            dataForTest[type]?.forEachIndexed { index, data ->
                tasksRepository.changeAssignees(
                    data.id,
                    data.taskType,
                    listOf(users[index % users.size].id),
                    data.version
                )
                val commonTaskAfterChange = tasksRepository.getCommonTask(data.id, data.taskType)

                testCommonTaskExtEquality(data, commonTaskAfterChange)
                assertEquals(
                    expected = listOf(users[index % users.size].id),
                    actual = commonTaskAfterChange.assignedIds
                )
            }
        }
    }

    @Test
    fun `test change watchers`() = runBlocking {
        val users = usersRepository.getTeam()
        val dataForTest = getCommonTasksExt(
            listOf(
                CommonTaskType.Epic,
                CommonTaskType.Issue,
                CommonTaskType.Task,
                CommonTaskType.UserStory
            )
        )

        CommonTaskType.values().forEach { type ->
            dataForTest[type]?.forEachIndexed { index, data ->
                tasksRepository.changeWatchers(
                    data.id,
                    data.taskType,
                    listOf(users[index % users.size].id),
                    data.version
                )
                val commonTaskAfterChange = tasksRepository.getCommonTask(data.id, data.taskType)

                testCommonTaskExtEquality(data, commonTaskAfterChange)
                assertEquals(
                    expected = listOf(users[index % users.size].id),
                    actual = commonTaskAfterChange.watcherIds
                )
            }
        }
    }

    @Test
    fun `test change due date`() = runBlocking {
        val dataForTest = getCommonTasksExt(
            listOf(
                CommonTaskType.Issue,
                CommonTaskType.Task,
                CommonTaskType.UserStory
            )
        )

        dataForTest.forEach {
            it.value.forEachIndexed { index, data ->
                val currentDate = LocalDate.now()
                tasksRepository.changeDueDate(
                    data.id,
                    data.taskType,
                    currentDate,
                    data.version
                )
                val commonTaskAfterChange = tasksRepository.getCommonTask(data.id, data.taskType)

                testCommonTaskExtEquality(data, commonTaskAfterChange)
                assertEquals(
                    expected = currentDate,
                    actual = commonTaskAfterChange.dueDate
                )
            }
        }
    }

    @Test
    fun `test create comment`() = runBlocking {
        val dataForTest = getCommonTasksExt(
            listOf(
                CommonTaskType.Epic,
                CommonTaskType.Issue,
                CommonTaskType.Task,
                CommonTaskType.UserStory
            )
        )

        dataForTest.forEach {
            it.value.forEachIndexed { index, data ->
                tasksRepository.createComment(
                    data.id,
                    data.taskType,
                    "Comment${index}",
                    data.version
                )
                assertNotNull(
                    tasksRepository.getComments(data.id, data.taskType)
                        .find { it.text == "Comment${index}" }
                )
            }
        }
    }

    @Test
    fun `test delete comment`() = runBlocking {
        val dataForTest = getCommonTasksExt(
            listOf(
                CommonTaskType.Issue,
                CommonTaskType.Task
            )
        )

        dataForTest.forEach {
            it.value.forEachIndexed { index, data ->
                var comments = tasksRepository.getComments(data.id, data.taskType)
                if (comments.isEmpty()) {
                    tasksRepository.createComment(
                        data.id,
                        data.taskType,
                        "Comment${index}",
                        data.version
                    )
                    comments = tasksRepository.getComments(data.id, data.taskType)
                }
                comments.forEach { comment ->
                    tasksRepository.deleteComment(data.id, data.taskType, comment.id)
                    assertTrue {
                        comment !in tasksRepository.getComments(data.id, data.taskType)
                    }
                }
            }
        }
    }

    @Test
    fun `test edit common task`() = runBlocking {
        val dataForTest = getCommonTasksExt(
            listOf(
                CommonTaskType.Epic,
                CommonTaskType.Issue,
                CommonTaskType.Task,
                CommonTaskType.UserStory
            )
        )

        dataForTest.forEach {
            it.value.forEachIndexed { index, commonTask ->
                tasksRepository.editCommonTask(
                    commonTask.id,
                    commonTask.taskType,
                    "Title${index}",
                    "Description${index}",
                    commonTask.version
                )

                val taskAfterChange =
                    tasksRepository.getCommonTask(commonTask.id, commonTask.taskType)
                assertEquals(
                    expected = "Title${index}",
                    actual = taskAfterChange.title
                )
                assertEquals(
                    expected = "Description${index}",
                    actual = taskAfterChange.description
                )
            }
        }
    }

    @Test
    fun `test create common task`() = runBlocking {
        val sprints = sprintsRepository.getSprints(1)
        val swimlanes = tasksRepository.getSwimlanes()
        CommonTaskType.values().forEachIndexed { index, type ->
            val statuses = tasksRepository.getStatuses(type)
            val commonTask = tasksRepository.createCommonTask(
                commonTaskType = type,
                title = "Title${index}",
                description = "Description${index}",
                parentId = null,
                sprintId = if (sprints.isNotEmpty()) sprints[index % sprints.size].id else null,
                statusId = if (statuses.isNotEmpty()) statuses[index % statuses.size].id else null,
                swimlaneId = if (swimlanes.isNotEmpty()) swimlanes[index % swimlanes.size].id else null
            )
            val extendedCommonTask =
                tasksRepository.getCommonTask(commonTask.id, commonTask.taskType)

            assertEquals(
                expected = type,
                actual = extendedCommonTask.taskType
            )
            assertEquals(
                expected = "Title${index}",
                actual = extendedCommonTask.title
            )
            assertEquals(
                expected = "Description${index}",
                actual = extendedCommonTask.description
            )
        }
    }

    @Test
    fun `test delete common task`() = runBlocking {
        val dataForTest = getCommonTasksExt(
            listOf(
                CommonTaskType.Epic,
                CommonTaskType.Issue,
                CommonTaskType.Task,
                CommonTaskType.UserStory
            )
        )

        dataForTest.forEach {
            it.value.forEachIndexed { index, commonTask ->
                tasksRepository.deleteCommonTask(commonTask.taskType, commonTask.id)
                assertFailsWith<retrofit2.HttpException> {
                    tasksRepository.getCommonTask(commonTask.id, commonTask.taskType)
                }
            }
        }
    }

    @Test
    fun `test promote common task to userstory`() = runBlocking {
        val dataForTest = getCommonTasksExt(
            listOf(
                CommonTaskType.Issue,
                CommonTaskType.Task
            )
        )

        dataForTest.forEach {
            it.value.forEachIndexed { index, commonTask ->
                val promotedCommonTask =
                    tasksRepository.promoteCommonTaskToUserStory(commonTask.id, commonTask.taskType)

                assertEquals(
                    expected = CommonTaskType.UserStory,
                    actual = promotedCommonTask.taskType
                )
                assertEquals(
                    expected = commonTask.title,
                    actual = promotedCommonTask.title
                )
                assertEquals(
                    expected = commonTask.tags,
                    actual = promotedCommonTask.tags
                )
            }
        }
    }

    @Test
    fun `test edit tags`() = runBlocking {
        val dataForTest = getCommonTasksExt(
            listOf(
                CommonTaskType.Epic,
                CommonTaskType.Issue,
                CommonTaskType.Task,
                CommonTaskType.UserStory
            )
        )

        dataForTest.forEach {
            val tags = tasksRepository.getAllTags(it.key)
            it.value.forEachIndexed { index, commonTask ->
                tasksRepository.editTags(
                    commonTask.taskType,
                    commonTask.id,
                    if (tags.isNotEmpty()) listOf(tags[index % tags.size]) else listOf(),
                    commonTask.version
                )
                val commonTaskAfterChange =
                    tasksRepository.getCommonTask(commonTask.id, commonTask.taskType)

                testCommonTaskExtEquality(commonTask, commonTaskAfterChange)
                assertEquals(
                    expected = if (tags.isNotEmpty()) listOf(tags[index % tags.size]) else listOf(),
                    actual = commonTaskAfterChange.tags,
                )
            }
        }
    }

    @Test
    fun `test change epic color`() = runBlocking {
        val epics = tasksRepository.getEpics(1, FiltersData()).map {
            tasksRepository.getCommonTask(it.id, it.taskType)
        }
        val colors = listOf("#108D32", "#C207BF", "#950A52", "#BD33EE")

        epics.forEachIndexed { index, epic ->
            tasksRepository.changeEpicColor(
                epic.id,
                colors[index % colors.size],
                epic.version
            )
            val epicAfterChange = tasksRepository.getCommonTask(epic.id, CommonTaskType.Epic)

            testCommonTaskExtEquality(epic, epicAfterChange)
            assertEquals(
                expected = colors[index % colors.size],
                actual = epicAfterChange.color
            )
        }
    }
}