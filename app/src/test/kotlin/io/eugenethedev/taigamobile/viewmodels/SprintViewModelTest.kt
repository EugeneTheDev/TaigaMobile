package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.screens.sprint.SprintViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.eugenethedev.taigamobile.viewmodels.utils.notFoundException
import io.mockk.mockk
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class SprintViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: SprintViewModel

    @BeforeTest
    fun setup() {
        viewModel = SprintViewModel(mockAppComponent)
    }

    @BeforeTest
    fun settingOfLoadData() {

        coEvery { mockSprintsRepository.getSprint(any()) } returns mockSprint
        coEvery { mockTaskRepository.getStatuses(any()) } returns mockStatuses
        coEvery { mockSprintsRepository.getSprintUserStories(any()) } returns mockSprintUserStories
        coEvery { mockTaskRepository.getUserStoryTasks(any()) } returns mockUserStoryTasks
        coEvery { mockSprintsRepository.getSprintIssues(any()) } returns mockSprintIssues
        coEvery { mockSprintsRepository.getSprintTasks(any()) } returns mockSprintTasks
    }

    companion object {
        val mockSprint = mockk<Sprint>(relaxed = true)
        val mockStatuses = mockk<List<Status>>(relaxed = true)
        val mockSprintUserStories = mockk<List<CommonTask>>(relaxed = true)
        val mockUserStoryTasks = mockk<List<CommonTask>>(relaxed = true)
        val mockSprintIssues = mockk<List<CommonTask>>(relaxed = true)
        val mockSprintTasks = mockk<List<CommonTask>>(relaxed = true)
    }

    fun asserts() {
        val mapOfSprintUserStoriesTasks =
            mockSprintUserStories.map { it to mockUserStoryTasks }.toMap()
        assertResultEquals(SuccessResult(mockSprint), viewModel.sprint.value)
        assertResultEquals(SuccessResult(mockStatuses), viewModel.statuses.value)
        assertResultEquals(
            SuccessResult(mapOfSprintUserStoriesTasks),
            viewModel.storiesWithTasks.value
        )
        assertResultEquals(SuccessResult(mockSprintIssues), viewModel.issues.value)
        assertResultEquals(SuccessResult(mockSprintTasks), viewModel.storylessTasks.value)
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        val sprintId = 1L
        viewModel.onOpen(sprintId)
        asserts()
    }

    @Test
    fun `test on open error`(): Unit = runBlocking {
        val sprintId = 1L
        coEvery { mockSprintsRepository.getSprint(any()) } throws notFoundException
        viewModel.onOpen(sprintId)
        assertIs<ErrorResult<Sprint>>(viewModel.sprint.value)
    }

    @Test
    fun `edit sprint`(): Unit = runBlocking {
        val sprintId = 1L
        val name = "lol"
        val start = LocalDate.of(2000, 1, 1)
        val end = LocalDate.of(3000, 1, 1)
        viewModel.onOpen(sprintId)
        coEvery {
            mockSprintsRepository.editSprint(
                any(),
                neq(name),
                any(),
                any()
            )
        } throws notFoundException
        viewModel.editSprint(name, start, end)
        asserts()
        assertResultEquals(SuccessResult(Unit), viewModel.editResult.value)
        viewModel.editSprint(name + "error", start, end)
        assertIs<ErrorResult<Unit>>(viewModel.editResult.value)
    }

    @Test
    fun `delete sprint`(): Unit = runBlocking {
        val sprintId = 1L
        viewModel.onOpen(sprintId)
        viewModel.deleteSprint()
        asserts()
        assertResultEquals(SuccessResult(Unit), viewModel.deleteResult.value)

    }

    @Test
    fun `delete sprint error`(): Unit = runBlocking {
        val sprintId = 1L
        viewModel.onOpen(sprintId)
        coEvery { mockSprintsRepository.deleteSprint(any()) } throws notFoundException
        asserts()
        viewModel.deleteSprint()
        assertIs<ErrorResult<Unit>>(viewModel.deleteResult.value)
    }
}
