package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.SprintsRepository
import io.eugenethedev.taigamobile.data.repositories.TasksRepository
import io.eugenethedev.taigamobile.data.repositories.UsersRepository
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.domain.entities.UsersFilter
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

    @Test
    fun `test get working on`() = runBlocking {
        val listCommonTasks = tasksRepository.getWorkingOn().sortedBy { it.title }

        val totalTestCommonTasks = TestData.projects
            .flatMapIndexed { index, project -> project.epics + getTestTasks(index) + project.issues + project.userstories }
            .filter { it.assignedTo == TestData.activeUser && !it.isClosed }
            .map {
                TestCommonTask(
                    title = it.title,
                    assignee = it.assignedTo,
                    isClosed = it.isClosed
                )
            }
            .sortedBy { it.title }

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
            val commonTaskAfterChange =
                tasksRepository.getCommonTask(issue.id, CommonTaskType.Issue)

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

            assertEquals(
                expected = commonTask.id,
                actual = commonTaskAfterChange.id
            )
            assertEquals(
                expected = sprints[index % sprints.size].id,
                actual = commonTaskAfterChange.sprint?.id
            )
            assertEquals(
                expected = sprints[index % sprints.size].name,
                actual = commonTaskAfterChange.sprint?.name
            )
            assertEquals(
                expected = commonTask.version + 1,
                actual = commonTaskAfterChange.version
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

            assertEquals(
                expected = story.id,
                actual = storyAfterChange.id
            )
            assertEquals(
                expected = sprints[index % sprints.size].id,
                actual = storyAfterChange.sprint?.id
            )
            assertEquals(
                expected = sprints[index % sprints.size].name,
                actual = storyAfterChange.sprint?.name
            )
            assertEquals(
                expected = story.version + 1,
                actual = storyAfterChange.version
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
        val userStories = tasksRepository.getAllUserStories()
        val users = usersRepository.getTeam()
        val dataForTest = hashMapOf(
            CommonTaskType.Epic to tasksRepository.getEpics(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Issue to tasksRepository.getIssues(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Task to userStories.flatMap { tasksRepository.getUserStoryTasks(it.id) }
                .map {
                    tasksRepository.getCommonTask(it.id, it.taskType)
                },
            CommonTaskType.UserStory to userStories
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

                assertEquals(
                    expected = data.id,
                    actual = commonTaskAfterChange.id
                )
                assertEquals(
                    expected = listOf(users[index % users.size].id),
                    actual = commonTaskAfterChange.assignedIds
                )
                assertEquals(
                    expected = data.version + 1,
                    actual = commonTaskAfterChange.version
                )
            }
        }
    }

    @Test
    fun `test change watchers`() = runBlocking {
        val userStories = tasksRepository.getAllUserStories()
        val users = usersRepository.getTeam()
        val dataForTest = hashMapOf(
            CommonTaskType.Epic to tasksRepository.getEpics(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Issue to tasksRepository.getIssues(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Task to userStories.flatMap { tasksRepository.getUserStoryTasks(it.id) }
                .map {
                    tasksRepository.getCommonTask(it.id, it.taskType)
                },
            CommonTaskType.UserStory to userStories
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

                assertEquals(
                    expected = data.id,
                    actual = commonTaskAfterChange.id
                )
                assertEquals(
                    expected = listOf(users[index % users.size].id),
                    actual = commonTaskAfterChange.watcherIds
                )
                assertEquals(
                    expected = data.version + 1,
                    actual = commonTaskAfterChange.version
                )
            }
        }
    }

    @Test
    fun `test change due date`() = runBlocking {
        val userStories = tasksRepository.getAllUserStories()
        val dataForTest = hashMapOf(
            CommonTaskType.Issue to tasksRepository.getIssues(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Task to userStories.flatMap { tasksRepository.getUserStoryTasks(it.id) }
                .map {
                    tasksRepository.getCommonTask(it.id, it.taskType)
                },
            CommonTaskType.UserStory to userStories
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

                assertEquals(
                    expected = data.id,
                    actual = commonTaskAfterChange.id
                )
                assertEquals(
                    expected = currentDate,
                    actual = commonTaskAfterChange.dueDate
                )
                assertEquals(
                    expected = data.version + 1,
                    actual = commonTaskAfterChange.version
                )
            }
        }
    }

    @Test
    fun `test create comment`() = runBlocking {
        val userStories = tasksRepository.getAllUserStories()
        val dataForTest = hashMapOf(
            CommonTaskType.Epic to tasksRepository.getEpics(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Issue to tasksRepository.getIssues(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Task to userStories.flatMap { tasksRepository.getUserStoryTasks(it.id) }
                .map {
                    tasksRepository.getCommonTask(it.id, it.taskType)
                },
            CommonTaskType.UserStory to userStories
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
        val userStories = tasksRepository.getAllUserStories()
        val dataForTest = hashMapOf(
            CommonTaskType.Issue to tasksRepository.getIssues(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Task to userStories.flatMap { tasksRepository.getUserStoryTasks(it.id) }
                .map {
                    tasksRepository.getCommonTask(it.id, it.taskType)
                }
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
        val userStories = tasksRepository.getAllUserStories()
        val dataForTest = hashMapOf(
            CommonTaskType.Epic to tasksRepository.getEpics(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Issue to tasksRepository.getIssues(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Task to userStories.flatMap { tasksRepository.getUserStoryTasks(it.id) }
                .map {
                    tasksRepository.getCommonTask(it.id, it.taskType)
                },
            CommonTaskType.UserStory to userStories
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
        val userStories = tasksRepository.getAllUserStories()
        val dataForTest = hashMapOf(
            CommonTaskType.Epic to tasksRepository.getEpics(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Issue to tasksRepository.getIssues(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Task to userStories.flatMap { tasksRepository.getUserStoryTasks(it.id) }
                .map {
                    tasksRepository.getCommonTask(it.id, it.taskType)
                },
            CommonTaskType.UserStory to userStories
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
        val userStories = tasksRepository.getAllUserStories()
        val dataForTest = hashMapOf(
            CommonTaskType.Issue to tasksRepository.getIssues(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Task to userStories.flatMap { tasksRepository.getUserStoryTasks(it.id) }
                .map {
                    tasksRepository.getCommonTask(it.id, it.taskType)
                }
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
        val userStories = tasksRepository.getAllUserStories()
        val dataForTest = hashMapOf(
            CommonTaskType.Epic to tasksRepository.getEpics(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Issue to tasksRepository.getIssues(1, FiltersData()).map {
                tasksRepository.getCommonTask(it.id, it.taskType)
            },
            CommonTaskType.Task to userStories.flatMap { tasksRepository.getUserStoryTasks(it.id) }
                .map {
                    tasksRepository.getCommonTask(it.id, it.taskType)
                },
            CommonTaskType.UserStory to userStories
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

                assertEquals(
                    expected = commonTask.id,
                    actual = commonTaskAfterChange.id
                )
                assertEquals(
                    expected = if (tags.isNotEmpty()) listOf(tags[index % tags.size]) else listOf(),
                    actual = commonTaskAfterChange.tags,
                )
                assertEquals(
                    expected = commonTask.version + 1,
                    actual = commonTaskAfterChange.version
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

            assertEquals(
                expected = epic.id,
                actual = epicAfterChange.id
            )
            assertEquals(
                expected = colors[index % colors.size],
                actual = epicAfterChange.color
            )
            assertEquals(
                expected = epic.version + 1,
                actual = epicAfterChange.version
            )
        }
    }
}