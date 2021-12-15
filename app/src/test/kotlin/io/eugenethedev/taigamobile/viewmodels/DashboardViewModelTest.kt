package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.ui.screens.dashboard.DashboardViewModel
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.TeamMember
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.eugenethedev.taigamobile.viewmodels.utils.createDeniedException
import io.eugenethedev.taigamobile.viewmodels.utils.notFoundException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class DashboardViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: DashboardViewModel

    @BeforeTest
    fun setup() {
        viewModel = DashboardViewModel(mockAppComponent)
    }
    @BeforeTest
    fun settingsOfLaunch(){
        coEvery { mockTaskRepository.getWorkingOn() } returns mockWorkingOn
        coEvery { mockTaskRepository.getWatching() } returns mockWatching
    }
    companion object {
        val mockWorkingOn = mockk<List<CommonTask>>(relaxed = true)
        val mockWatching = mockk<List<CommonTask>>(relaxed = true)
    }
    fun asserts(){
        assertResultEquals(SuccessResult(mockWorkingOn), viewModel.workingOn.value)
        assertResultEquals(SuccessResult(mockWatching), viewModel.watching.value)
    }
    @Test
    fun `test on open`(): Unit = runBlocking {
        viewModel.onOpen()
        asserts()

    }
    @Test
    fun `test on open error`(): Unit = runBlocking {
        coEvery { mockTaskRepository.getWorkingOn() } throws notFoundException
        viewModel.onOpen()

        assertIs<ErrorResult<List<CommonTask>>>(viewModel.workingOn.value)

    }

    @Test
    fun `change current project`(): Unit = runBlocking {
        val mockCommonTask = mockk<CommonTask>(relaxed = true)
        viewModel.changeCurrentProject(mockCommonTask)
    }
}
