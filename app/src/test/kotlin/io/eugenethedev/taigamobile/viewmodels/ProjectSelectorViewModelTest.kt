package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.screens.projectselector.ProjectSelectorViewModel
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import io.eugenethedev.taigamobile.viewmodels.utils.testLazyPagingItems
import kotlin.test.Test
import kotlin.test.assertIs

class ProjectSelectorViewModelTest: BaseViewModelTest() {
    private lateinit var viewModel: ProjectSelectorViewModel

    @BeforeTest
    fun setup() = runBlocking {
        viewModel = ProjectSelectorViewModel(mockAppComponent)
    }

    @Test
    fun `test search projects`(): Unit = runBlocking {
        val query = "query"
        viewModel.onOpen()
        viewModel.searchProjects(query)
        testLazyPagingItems(viewModel.projects) {
            mockSearchRepository.searchProjects(
                any(),
                any()
            )
        }
    }

    @Test
    fun `test select project`(): Unit = runBlocking {
        val mockProject = mockk<Project>()

        viewModel.selectProject(mockProject)
        assertIs<Project>(viewModel.currentProjectId.value)
    }
}
