package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.ProjectsRepository
import io.eugenethedev.taigamobile.domain.repositories.IProjectsRepository
import io.eugenethedev.taigamobile.testdata.TestData
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProjectsRepositoryTest : BaseRepositoryTest() {
    lateinit var projectsRepository: IProjectsRepository

    @BeforeTest
    fun setupSearchRepositoryTest() {
        projectsRepository = ProjectsRepository(mockTaigaApi, mockSession)
    }

    @Test
    fun `test simple search projects`() = runBlocking {
        val projects = projectsRepository.searchProjects("", 1)
        assertEquals(
            expected = TestData.projects.map { it.name },
            actual = projects.map { it.name }
        )
    }

    @Test
    fun `test empty response on wrong query or page`() = runBlocking {
        assertEquals(0, projectsRepository.searchProjects("", 100).size)
        assertEquals(0, projectsRepository.searchProjects("dumb string", 1).size)
    }

    @Test
    fun `get my projects`() = runBlocking {
        val projects = projectsRepository.getMyProjects()
        assertEquals(
            expected = TestData.projects.map { it.name },
            actual = projects.map { it.name }
        )
    }
}
