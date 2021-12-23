package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.screens.projectselector.ProjectSelectorViewModel
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import io.eugenethedev.taigamobile.viewmodels.utils.testLazyPagingItems
import io.mockk.coVerify
import kotlin.test.Test

class ProjectSelectorViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: ProjectSelectorViewModel

    @BeforeTest
    fun setup() {
        viewModel = ProjectSelectorViewModel(mockAppComponent)
    }

    //Fixme
    @Test
    fun `test list of projects`(): Unit = runBlocking {
        val query = "query"
        viewModel.onOpen()
        viewModel.searchProjects(query)
        testLazyPagingItems(viewModel.projects) {
            mockSearchRepository.searchProjects(any(), any())
        }
    }

    @Test
    fun `test select project`(): Unit = runBlocking {
        val mockProject = mockk<Project>(relaxed = true)
        viewModel.selectProject(mockProject)
        coVerify { mockSession.changeCurrentProject(any(), any()) }
    }
}