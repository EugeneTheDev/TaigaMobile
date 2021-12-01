package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.ui.screens.scrum.ScrumViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.eugenethedev.taigamobile.viewmodels.utils.createDeniedException
import io.eugenethedev.taigamobile.viewmodels.utils.notFoundException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class ScrumViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: ScrumViewModel

    @BeforeTest
    fun setup() {
        viewModel = ScrumViewModel(mockAppComponent)
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        val mockFiltersData = mockk<FiltersData>()

        coEvery { mockTaskRepository.getFiltersData(any()) } returns mockFiltersData

        viewModel.onOpen()
        assertResultEquals(SuccessResult(mockFiltersData), viewModel.filters.value)
    }

    @Test
    fun `test on open error`(): Unit = runBlocking {
        coEvery { mockTaskRepository.getFiltersData(any()) } throws notFoundException

        viewModel.onOpen()
        assertIs<ErrorResult<FiltersData>>(viewModel.filters.value)
    }

    @Test
    fun `test select filters`(): Unit = runBlocking {
        val mockFiltersData = mockk<FiltersData>()

        viewModel.selectFilters(mockFiltersData)
        assertIs<FiltersData>(viewModel.activeFilters.value)
    }

    @Test
    fun `test create sprint`(): Unit = runBlocking {
        val testName = "test name"
        val startLocalDate = LocalDate.of(2000, 1, 1)
        val endLocalDate = LocalDate.of(3000, 1, 1)

        coEvery {
            mockSprintsRepository.createSprint(
                neq(testName),
                any(),
                any()
            )
        } throws createDeniedException

        viewModel.createSprint(testName, startLocalDate, endLocalDate)
        assertResultEquals(SuccessResult(Unit), viewModel.createSprintResult.value)

        viewModel.createSprint(testName + "wrong", startLocalDate, endLocalDate)
        assertIs<ErrorResult<Unit>>(viewModel.createSprintResult.value)

    }
}