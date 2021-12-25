package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.ui.screens.issues.IssuesViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.eugenethedev.taigamobile.viewmodels.utils.notFoundException
import io.eugenethedev.taigamobile.viewmodels.utils.testLazyPagingItems
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

class IssuesViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: IssuesViewModel

    @BeforeTest
    fun setup() {
        viewModel = IssuesViewModel(mockAppComponent)
    }

    @Test
    fun `test on open`(): Unit = runBlocking {
        val mockFiltersData = mockk<FiltersData>(relaxed = true)

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
    fun `test issues list with filters`(): Unit = runBlocking {
        val query = "query"
        testLazyPagingItems(viewModel.issues) {
            mockTaskRepository.getIssues(
                any(),
                eq(FiltersData())
            )
        }
        viewModel.selectFilters(FiltersData(query = query))
        testLazyPagingItems(viewModel.issues) {
            mockTaskRepository.getIssues(
                any(),
                eq(FiltersData(query = query))
            )
        }
    }
}