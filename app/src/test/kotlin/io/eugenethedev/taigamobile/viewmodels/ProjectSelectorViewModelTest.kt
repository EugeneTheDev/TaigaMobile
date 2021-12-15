package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.manager.ProjectData
import io.eugenethedev.taigamobile.ui.screens.commontask.CommonTaskViewModel
import io.eugenethedev.taigamobile.ui.screens.projectselector.ProjectSelectorViewModel
import io.eugenethedev.taigamobile.ui.utils.ErrorResult
import io.eugenethedev.taigamobile.ui.utils.SuccessResult
import io.eugenethedev.taigamobile.viewmodels.utils.assertResultEquals
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import io.eugenethedev.taigamobile.viewmodels.utils.notFoundException
import io.eugenethedev.taigamobile.viewmodels.utils.testLazyPagingItems
import io.mockk.coEvery
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
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
        /*viewModel.searchProjects(query+"error")
        testLazyPagingItems(viewModel.projects) {
            mockSearchRepository.searchProjects(
                neq(query),
                any()
            )
        }*/
    }

    @Test
    fun `test select project`(): Unit = runBlocking {
        val mockProject = mockk<Project>()

        viewModel.selectProject(mockProject)
        assertIs<Project>(viewModel.currentProjectId.value)
    }
}
