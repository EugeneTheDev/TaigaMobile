package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.ui.screens.epics.EpicsViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.eugenethedev.taigamobile.viewmodels.utils.notFoundException
import io.eugenethedev.taigamobile.viewmodels.utils.testLazyPagingItems
import io.mockk.coEvery
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class EpicsViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: EpicsViewModel

    @BeforeTest
    fun setup() {
        viewModel = EpicsViewModel(mockAppComponent)
    }

    @Test
    fun `test on open`() = runBlocking {
        val filtersData = FiltersData()
        coEvery { mockTaskRepository.getFiltersData(CommonTaskType.Epic) } returns filtersData
        viewModel.onOpen()
        assertResultEquals(SuccessResult(filtersData), viewModel.filters.value)
    }

    @Test
    fun `test on open error`(): Unit = runBlocking {
        coEvery { mockTaskRepository.getFiltersData(any()) } throws notFoundException
        viewModel.onOpen()
        assertIs<ErrorResult<FiltersData>>(viewModel.filters.value)
    }

    @Test
    fun `test epics list with filters`(): Unit = runBlocking {
        val query = "query"
        testLazyPagingItems(viewModel.epics) { mockTaskRepository.getEpics(any(), eq(FiltersData())) }
        viewModel.selectFilters(FiltersData(query = query))
        testLazyPagingItems(viewModel.epics) { mockTaskRepository.getEpics(any(), eq(FiltersData(query = query))) }
    }
}
