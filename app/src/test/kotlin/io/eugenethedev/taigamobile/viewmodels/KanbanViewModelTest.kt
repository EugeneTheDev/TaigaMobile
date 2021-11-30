package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.screens.kanban.KanbanViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.eugenethedev.taigamobile.viewmodels.utils.badInternetException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class KanbanViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: KanbanViewModel

    @BeforeTest
    fun setup() {
        viewModel = KanbanViewModel(mockAppComponent)
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        val listStatuses = mockk<List<Status>>(relaxed = true)
        val listTeamMembers = mockk<List<TeamMember>>(relaxed = true)
        val listCommonTaskExtended = mockk<List<CommonTaskExtended>>(relaxed = true)
        val listSwimlanes = mockk<List<Swimlane>>(relaxed = true)

        coEvery { mockTaskRepository.getStatuses(any()) } returns listStatuses
        coEvery { mockUsersRepository.getTeam() } returns listTeamMembers
        coEvery { mockTaskRepository.getAllUserStories() } returns listCommonTaskExtended
        coEvery { mockTaskRepository.getSwimlanes() } returns listSwimlanes
        viewModel.onOpen()

        assertResultEquals(SuccessResult(listStatuses), viewModel.statuses.value)
        assertResultEquals(SuccessResult(listTeamMembers.map { it.toUser() }), viewModel.team.value)
        assertResultEquals(SuccessResult(listCommonTaskExtended), viewModel.stories.value)
        assertResultEquals(SuccessResult(listOf(null) + listSwimlanes), viewModel.swimlanes.value)
    }

    @Test
    fun `test on open error result`(): Unit = runBlocking {
        coEvery { mockTaskRepository.getStatuses(any()) } throws badInternetException
        coEvery { mockUsersRepository.getTeam() } throws badInternetException
        coEvery { mockTaskRepository.getAllUserStories() } throws badInternetException
        coEvery { mockTaskRepository.getSwimlanes() } throws badInternetException
        viewModel.onOpen()

        assertIs<ErrorResult<List<Status>>>(viewModel.statuses.value)
        assertIs<ErrorResult<List<User>>>(viewModel.team.value)
        assertIs<ErrorResult<List<CommonTaskExtended>>>(viewModel.stories.value)
        assertIs<ErrorResult<List<Swimlane?>>>(viewModel.swimlanes.value)
    }

    @Test
    fun `test select swimlane`(): Unit = runBlocking {
        val mockSwimlane = mockk<Swimlane>(relaxed = true)

        viewModel.selectSwimlane(mockSwimlane)
        assertIs<Swimlane?>(viewModel.selectedSwimlane.value)

        viewModel.selectSwimlane(null)
        assertIs<Swimlane?>(viewModel.selectedSwimlane.value)
    }
}