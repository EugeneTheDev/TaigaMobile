package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.Swimlane
import io.eugenethedev.taigamobile.ui.screens.kanban.KanbanViewModel
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KanbanViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel : KanbanViewModel

    @BeforeTest
    fun setup() {
        viewModel = KanbanViewModel(mockAppComponent)
    }

    @Test
    fun `test select swimlane`() = runBlocking {
        val swimlane = Swimlane(1, "test name", 1)

        viewModel.selectSwimlane(swimlane)
        assertEquals(viewModel.selectedSwimlane.value, swimlane)

        viewModel.selectSwimlane(null)
        assertEquals(viewModel.selectedSwimlane.value, null)
    }
}