package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.screens.commontask.CommonTaskViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.time.LocalDate
import kotlin.test.*

class CommonTaskViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: CommonTaskViewModel

    @BeforeTest
    fun setup() = runBlocking {
        viewModel = CommonTaskViewModel(mockAppComponent)
    }

    companion object {
        val mockCommonTask = mockk<CommonTaskExtended>(relaxed = true)
        val mockUser = mockk<User>(relaxed = true)
        val mockCustomFields = mockk<CustomFields>(relaxed = true)
        val mockListOfAttachments = mockk<List<Attachment>>(relaxed = true)
        val mockEpicUserStories = mockk<List<CommonTask>>(relaxed = true)
        val mockUserStoryTasks = mockk<List<CommonTask>>(relaxed = true)
        val mockListOfComments = mockk<List<Comment>>(relaxed = true)
        val mockListOfTags = mockk<List<Tag>>(relaxed = true)
        val mockListOfTeamMember = mockk<List<TeamMember>>(relaxed = true)
        val mockListOfSwimlanes = mockk<List<Swimlane>>(relaxed = true)
        val mockListOfStatus = mockk<List<Status>>(relaxed = true)
    }

    @BeforeTest
    fun setupMockForLoadData() {
        coEvery { mockTaskRepository.getCommonTask(any(), any()) } returns mockCommonTask
        coEvery { mockUsersRepository.getUser(any()) } returns mockUser
        coEvery { mockTaskRepository.getCustomFields(any(), any()) } returns mockCustomFields
        coEvery { mockTaskRepository.getAttachments(any(), any()) } returns mockListOfAttachments
        coEvery { mockTaskRepository.getEpicUserStories(any()) } returns mockEpicUserStories
        coEvery { mockTaskRepository.getUserStoryTasks(any()) } returns mockUserStoryTasks
        coEvery { mockTaskRepository.getComments(any(), any()) } returns mockListOfComments
        coEvery { mockTaskRepository.getAllTags(any()) } returns mockListOfTags
        coEvery { mockUsersRepository.getTeam() } returns mockListOfTeamMember
        coEvery { mockTaskRepository.getSwimlanes() } returns mockListOfSwimlanes
        coEvery { mockTaskRepository.getStatusByType(any(), any()) } returns mockListOfStatus
    }

    private fun initOnOpen() {
        val mockCommonTaskType = mockk<CommonTaskType>(relaxed = true)
        val testCommonTaskId = 1L

        viewModel.onOpen(testCommonTaskId, mockCommonTaskType)
        checkEqualityForLoadData(mockCommonTaskType)
    }

    private fun checkEqualityForLoadData(mockCommonTaskType: CommonTaskType) {
        val mapOfStatuses = StatusType.values().filter {
            if (mockCommonTaskType != CommonTaskType.Issue) it == StatusType.Status else true
        }.associateWith { mockListOfStatus }

        assertResultEquals(SuccessResult(mockCommonTask), viewModel.commonTask.value)
        assertResultEquals(SuccessResult(mockUser), viewModel.creator.value)
        assertResultEquals(SuccessResult(mockCustomFields), viewModel.customFields.value)
        assertResultEquals(SuccessResult(mockListOfAttachments), viewModel.attachments.value)
        assertResultEquals(SuccessResult(mockEpicUserStories), viewModel.userStories.value)
        assertResultEquals(SuccessResult(mockUserStoryTasks), viewModel.tasks.value)
        assertResultEquals(SuccessResult(mockListOfComments), viewModel.comments.value)
        assertResultEquals(SuccessResult(mockListOfTags), viewModel.tags.value)
        assertResultEquals(
            SuccessResult(mockListOfTeamMember.map { it.toUser() }),
            viewModel.team.value
        )
        assertResultEquals(
            SuccessResult(listOf(CommonTaskViewModel.SWIMLANE_HEADER) + mockListOfSwimlanes),
            viewModel.swimlanes.value
        )
        assertResultEquals(SuccessResult(mapOfStatuses), viewModel.statuses.value)
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        val testCommonTaskId = 1L
        val mockCommonTaskType = mockk<CommonTaskType>(relaxed = true)

        viewModel.onOpen(testCommonTaskId, mockCommonTaskType)
        checkEqualityForLoadData(mockCommonTaskType)

        coEvery { mockTaskRepository.getCommonTask(any(), any()) } throws notFoundException
        viewModel.onOpen(testCommonTaskId, mockCommonTaskType)
        assertIs<ErrorResult<CommonTaskExtended>>(viewModel.commonTask.value)
    }

    @Test
    fun `test select status`(): Unit = runBlocking {
        val mockStatus = mockk<Status>(relaxed = true)
        val errorStatus = Status(
            id = mockStatus.id + 1,
            name = mockStatus.name,
            color = mockStatus.color,
            type = mockStatus.type
        )

        initOnOpen()
        coEvery {
            mockTaskRepository.changeStatus(
                any(),
                any(),
                neq(mockStatus.id),
                any(),
                any()
            )
        } throws accessDeniedException

        viewModel.selectStatus(mockStatus)
        assertResultEquals(SuccessResult(mockStatus.type), viewModel.statusSelectResult.value)

        viewModel.selectStatus(errorStatus)
        assertIs<ErrorResult<StatusType>>(viewModel.statusSelectResult.value)
    }

    @Test
    fun `test list of sprints`(): Unit = runBlocking {
        testLazyPagingItems(viewModel.sprints, offset = 1) { // because of SPRINT_HEADER
            mockSprintsRepository.getSprints(any())
        }
    }

    @Test
    fun `test select sprint`(): Unit = runBlocking {
        val mockSprint = mockk<Sprint>(relaxed = true)
        val errorSprint = Sprint(
            id = mockSprint.id + 1,
            name = mockSprint.name,
            order = mockSprint.order,
            start = mockSprint.start,
            end = mockSprint.end,
            storiesCount = mockSprint.storiesCount,
            isClosed = mockSprint.isClosed
        )

        initOnOpen()
        coEvery {
            mockTaskRepository.changeSprint(
                any(),
                any(),
                neq(mockSprint.id),
                any()
            )
        } throws accessDeniedException

        viewModel.selectSprint(mockSprint)
        assertResultEquals(SuccessResult(Unit), viewModel.selectSprintResult.value)

        viewModel.selectSprint(errorSprint)
        assertIs<ErrorResult<Unit>>(viewModel.selectSprintResult.value)
    }

    @Test
    fun `test epics list with filters`(): Unit = runBlocking {
        val query = "query"
        testLazyPagingItems(viewModel.epics) {
            mockTaskRepository.getEpics(
                any(),
                eq(FiltersData())
            )
        }
        viewModel.searchEpics(query)
        testLazyPagingItems(viewModel.epics) {
            mockTaskRepository.getEpics(
                any(),
                eq(FiltersData(query = query))
            )
        }
    }

    @Test
    fun `test link to epic`(): Unit = runBlocking {
        val mockEpic = mockk<CommonTask>(relaxed = true)
        val errorEpic = CommonTask(
            id = mockEpic.id + 1,
            createdDate = mockEpic.createdDate,
            title = mockEpic.title,
            ref = mockEpic.ref,
            status = mockEpic.status,
            projectInfo = mockEpic.projectInfo,
            taskType = mockEpic.taskType,
            isClosed = mockEpic.isClosed
        )

        initOnOpen()
        coEvery {
            mockTaskRepository.linkToEpic(
                neq(mockEpic.id),
                any()
            )
        } throws accessDeniedException

        viewModel.linkToEpic(mockEpic)
        assertResultEquals(SuccessResult(Unit), viewModel.linkToEpicResult.value)

        viewModel.linkToEpic(errorEpic)
        assertIs<ErrorResult<Unit>>(viewModel.linkToEpicResult.value)
    }

    @Test
    fun `test unlink to epic`(): Unit = runBlocking {
        val mockEpic = mockk<EpicShortInfo>(relaxed = true)
        val errorEpic = EpicShortInfo(
            id = mockEpic.id + 1,
            title = mockEpic.title,
            ref = mockEpic.ref,
            color = mockEpic.color
        )

        initOnOpen()
        coEvery {
            mockTaskRepository.unlinkFromEpic(
                neq(mockEpic.id),
                any()
            )
        } throws accessDeniedException

        viewModel.unlinkFromEpic(mockEpic)
        assertResultEquals(SuccessResult(Unit), viewModel.linkToEpicResult.value)

        viewModel.unlinkFromEpic(errorEpic)
        assertIs<ErrorResult<Unit>>(viewModel.linkToEpicResult.value)
    }

    @Test
    fun `test search team`(): Unit = runBlocking {
        val teamName = "teamName"

        initOnOpen()
        viewModel.searchEpics(teamName)

        assertIs<List<User>>(viewModel.teamSearched.value)
        assertEquals(
            expected = mockListOfTeamMember.filter { it.name == teamName }.map { it.toUser() },
            actual = viewModel.teamSearched.value
        )
    }

    @Test
    fun `test add assignee`(): Unit = runBlocking {
        val mockUser = mockk<User>(relaxed = true)

        initOnOpen()
        viewModel.addAssignee(mockUser)
        assertResultEquals(
            SuccessResult(mockListOfTeamMember.map { it.toUser() }),
            viewModel.assignees.value
        )

        coEvery {
            mockTaskRepository.changeAssignees(
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        viewModel.addAssignee(mockUser)
        assertIs<ErrorResult<List<User>>>(viewModel.assignees.value)
    }

    @Test
    fun `test remove assignee`(): Unit = runBlocking {
        val mockUser = mockk<User>(relaxed = true)

        initOnOpen()
        viewModel.removeAssignee(mockUser)
        assertResultEquals(
            SuccessResult(mockListOfTeamMember.map { it.toUser() }),
            viewModel.assignees.value
        )

        coEvery {
            mockTaskRepository.changeAssignees(
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        viewModel.removeAssignee(mockUser)
        assertIs<ErrorResult<List<User>>>(viewModel.assignees.value)
    }

    @Test
    fun `test assignee or watch to me`(): Unit = runBlocking {
        val mockUser = mockk<User>(relaxed = true)

        initOnOpen()
        coEvery { mockUsersRepository.getUser(any()) } returns mockUser

        viewModel.assigneeOrWatchToMe()
        assertResultEquals(
            SuccessResult(mockListOfTeamMember.map { it.toUser() }),
            viewModel.assignees.value
        )

        viewModel.assigneeOrWatchToMe(isWatcher = true)
        assertResultEquals(
            SuccessResult(mockListOfTeamMember.map { it.toUser() }),
            viewModel.watchers.value
        )

        coEvery {
            mockTaskRepository.changeAssignees(
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        coEvery {
            mockTaskRepository.changeWatchers(
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException


        viewModel.assigneeOrWatchToMe()
        assertIs<ErrorResult<List<User>>>(viewModel.assignees.value)

        viewModel.assigneeOrWatchToMe(isWatcher = true)
        assertIs<ErrorResult<List<User>>>(viewModel.watchers.value)
    }

    @Test
    fun `test add watcher`(): Unit = runBlocking {
        val mockUser = mockk<User>(relaxed = true)

        initOnOpen()
        viewModel.addWatcher(mockUser)
        assertResultEquals(
            SuccessResult(mockListOfTeamMember.map { it.toUser() }),
            viewModel.watchers.value
        )

        coEvery {
            mockTaskRepository.changeWatchers(
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        viewModel.addWatcher(mockUser)
        assertIs<ErrorResult<List<User>>>(viewModel.watchers.value)
    }

    @Test
    fun `test remove watcher`(): Unit = runBlocking {
        val mockUser = mockk<User>(relaxed = true)

        initOnOpen()
        viewModel.removeWatcher(mockUser)
        assertResultEquals(
            SuccessResult(mockListOfTeamMember.map { it.toUser() }),
            viewModel.watchers.value
        )

        coEvery {
            mockTaskRepository.changeWatchers(
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        viewModel.removeWatcher(mockUser)
        assertIs<ErrorResult<List<User>>>(viewModel.watchers.value)
    }

    @Test
    fun `test create comment`(): Unit = runBlocking {
        val comment = "comment"

        initOnOpen()
        coEvery {
            mockTaskRepository.createComment(
                any(),
                any(),
                neq(comment),
                any()
            )
        } throws notFoundException

        viewModel.createComment(comment)
        assertResultEquals(SuccessResult(mockListOfComments), viewModel.comments.value)

        viewModel.createComment(comment + "error")
        assertIs<ErrorResult<List<Comment>>>(viewModel.comments.value)
    }

    @Test
    fun `test delete comment`(): Unit = runBlocking {
        val mockComment = mockk<Comment>(relaxed = true)
        val errorComment = Comment(
            id = mockComment.id + "error",
            author = mockComment.author,
            text = mockComment.text,
            postDateTime = mockComment.postDateTime,
            deleteDate = mockComment.deleteDate
        )

        initOnOpen()
        coEvery {
            mockTaskRepository.deleteComment(
                any(),
                any(),
                neq(mockComment.id)
            )
        } throws notFoundException

        viewModel.deleteComment(mockComment)
        assertResultEquals(SuccessResult(mockListOfComments), viewModel.comments.value)

        viewModel.deleteComment(errorComment)
        assertIs<ErrorResult<List<Comment>>>(viewModel.comments.value)
    }

    @Test
    fun `test delete attachment`(): Unit = runBlocking {
        val mockAttachment = mockk<Attachment>(relaxed = true)
        val errorAttachment = Attachment(
            id = mockAttachment.id + 1L,
            name = mockAttachment.name,
            sizeInBytes = mockAttachment.sizeInBytes,
            url = mockAttachment.url
        )

        initOnOpen()
        coEvery {
            mockTaskRepository.deleteAttachment(
                any(),
                neq(mockAttachment.id)
            )
        } throws accessDeniedException

        viewModel.deleteAttachment(mockAttachment)
        assertResultEquals(SuccessResult(mockListOfAttachments), viewModel.attachments.value)

        viewModel.deleteAttachment(errorAttachment)
        assertIs<ErrorResult<List<Attachment>>>(viewModel.attachments.value)
    }

    @Test
    fun `test add attachment`(): Unit = runBlocking {
        val fileName = "fileName"
        val mockInputStream = mockk<InputStream>(relaxed = true)

        initOnOpen()
        coEvery {
            mockTaskRepository.addAttachment(
                any(),
                any(),
                neq(fileName),
                any()
            )
        } throws accessDeniedException

        viewModel.addAttachment(fileName, mockInputStream)
        assertResultEquals(SuccessResult(mockListOfAttachments), viewModel.attachments.value)

        viewModel.addAttachment(fileName + "error", mockInputStream)
        assertIs<ErrorResult<List<Attachment>>>(viewModel.attachments.value)
    }

    @Test
    fun `test edit task`(): Unit = runBlocking {
        val title = "title"
        val description = "description"

        initOnOpen()
        coEvery {
            mockTaskRepository.editCommonTask(
                any(),
                any(),
                neq(title),
                any(),
                any()
            )
        } throws accessDeniedException

        viewModel.editTask(title, description)
        assertResultEquals(SuccessResult(Unit), viewModel.editResult.value)

        viewModel.editTask(title + "error", description)
        assertIs<ErrorResult<Unit>>(viewModel.editResult.value)
    }

    @Test
    fun `test delete task`(): Unit = runBlocking {
        initOnOpen()

        viewModel.deleteTask()
        assertResultEquals(SuccessResult(Unit), viewModel.deleteResult.value)

        coEvery { mockTaskRepository.deleteCommonTask(any(), any()) } throws notFoundException
        viewModel.deleteTask()
        assertIs<ErrorResult<Unit>>(viewModel.deleteResult.value)
    }

    @Test
    fun `test promote to userstory`(): Unit = runBlocking {
        val mockCommonTask = mockk<CommonTask>(relaxed = true)

        initOnOpen()

        coEvery {
            mockTaskRepository.promoteCommonTaskToUserStory(
                any(),
                any()
            )
        } returns mockCommonTask
        viewModel.promoteToUserStory()
        assertResultEquals(SuccessResult(mockCommonTask), viewModel.promoteResult.value)

        coEvery {
            mockTaskRepository.promoteCommonTaskToUserStory(
                any(),
                any()
            )
        } throws notFoundException
        viewModel.promoteToUserStory()
        assertIs<ErrorResult<CommonTask>>(viewModel.promoteResult.value)
    }

    @Test
    fun `test edit custom field`(): Unit = runBlocking {
        val mockCustomField = mockk<CustomField>(relaxed = true)
        val mockCustomFieldValue = mockk<CustomFieldValue>(relaxed = true)

        initOnOpen()
        viewModel.editCustomField(mockCustomField, mockCustomFieldValue)
        assertResultEquals(SuccessResult(mockCustomFields), viewModel.customFields.value)

        coEvery {
            mockTaskRepository.editCustomFields(
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        viewModel.editCustomField(mockCustomField, mockCustomFieldValue)
        assertIs<ErrorResult<CustomFields>>(viewModel.customFields.value)
    }

    @Test
    fun `test search tags`(): Unit = runBlocking {
        val query = "query"

        initOnOpen()
        viewModel.searchTags(query)

        assertIs<List<Tag>>(viewModel.tagsSearched.value)
        assertEquals(
            expected = mockListOfTags.filter { query.isNotEmpty() && query.lowercase() in it.name },
            actual = viewModel.tagsSearched.value
        )
    }

    @Test
    fun `test add tags`(): Unit = runBlocking {
        val mockTag = mockk<Tag>(relaxed = true)

        initOnOpen()
        viewModel.addTag(mockTag)
        assertResultEquals(
            SuccessResult(mockListOfTags),
            viewModel.tags.value
        )

        coEvery {
            mockTaskRepository.editTags(
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        viewModel.addTag(mockTag)
        assertIs<ErrorResult<List<Tag>>>(viewModel.tags.value)
    }

    @Test
    fun `test delete tags`(): Unit = runBlocking {
        val mockTag = mockk<Tag>(relaxed = true)

        initOnOpen()
        viewModel.deleteTag(mockTag)
        assertResultEquals(
            SuccessResult(mockListOfTags),
            viewModel.tags.value
        )

        coEvery {
            mockTaskRepository.editTags(
                any(),
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        viewModel.deleteTag(mockTag)
        assertIs<ErrorResult<List<Tag>>>(viewModel.tags.value)
    }

    @Test
    fun `test select swimlane`(): Unit = runBlocking {
        val mockSwimlane = mockk<Swimlane>(relaxed = true)

        initOnOpen()
        viewModel.swimlanes.value = SuccessResult(mockListOfSwimlanes)
        viewModel.selectSwimlane(mockSwimlane)
        assertResultEquals(SuccessResult(mockListOfSwimlanes), viewModel.swimlanes.value)

        coEvery {
            mockTaskRepository.changeUserStorySwimlane(
                any(),
                any(),
                any()
            )
        } throws accessDeniedException
        viewModel.selectSwimlane(mockSwimlane)
        assertIs<ErrorResult<List<Swimlane>>>(viewModel.swimlanes.value)
    }

    @Test
    fun `test select due date`(): Unit = runBlocking {
        val mockLocaleDate = LocalDate.of(2000, 1, 1)
        val errorLocaleDate = LocalDate.of(3000, 1, 1)

        initOnOpen()
        coEvery {
            mockTaskRepository.changeDueDate(
                any(),
                any(),
                neq(mockLocaleDate),
                any()
            )
        } throws accessDeniedException

        viewModel.selectDueDate(mockLocaleDate)
        assertResultEquals(SuccessResult(Unit), viewModel.dueDateResult.value)

        viewModel.selectDueDate(errorLocaleDate)
        assertIs<ErrorResult<Unit>>(viewModel.dueDateResult.value)
    }

    @Test
    fun `test select epic color`(): Unit = runBlocking {
        val color = "color"

        initOnOpen()
        coEvery {
            mockTaskRepository.changeEpicColor(
                any(),
                neq(color),
                any()
            )
        } throws accessDeniedException

        viewModel.selectEpicColor(color)
        assertResultEquals(SuccessResult(Unit), viewModel.colorResult.value)

        viewModel.selectEpicColor(color + "error")
        assertIs<ErrorResult<Unit>>(viewModel.colorResult.value)
    }
}