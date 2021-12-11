package io.eugenethedev.taigamobile.viewmodels

import androidx.lifecycle.viewmodel.compose.viewModel
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.screens.commontask.CommonTaskViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.accessDeniedException
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.eugenethedev.taigamobile.viewmodels.utils.notFoundException
import io.eugenethedev.taigamobile.viewmodels.utils.testLazyPagingItems
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

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
        val testCommonTaskId = 1L
        val mockCommonTaskType = mockk<CommonTaskType>(relaxed = true)
        val mockStatus = mockk<Status>(relaxed = true)
        val errorStatus = Status(
            id = mockStatus.id + 1,
            name = mockStatus.name,
            color = mockStatus.color,
            type = mockStatus.type
        )

        viewModel.onOpen(testCommonTaskId, mockCommonTaskType)
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
        checkEqualityForLoadData(mockCommonTaskType)
        assertResultEquals(SuccessResult(mockStatus.type), viewModel.statusSelectResult.value)

        viewModel.selectStatus(errorStatus)
        assertIs<ErrorResult<StatusType>>(viewModel.statusSelectResult.value)
    }

    @Test
    fun `test select sprint`(): Unit = runBlocking {
        val testCommonTaskId = 1L
        val mockCommonTaskType = mockk<CommonTaskType>(relaxed = true)
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

        viewModel.onOpen(testCommonTaskId, mockCommonTaskType)
        coEvery {
            mockTaskRepository.changeSprint(
                any(),
                any(),
                neq(mockSprint.id),
                any()
            )
        } throws accessDeniedException

        viewModel.selectSprint(mockSprint)
        checkEqualityForLoadData(mockCommonTaskType)
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
}