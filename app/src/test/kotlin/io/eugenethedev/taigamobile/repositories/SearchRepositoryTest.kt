package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.SearchRepository
import io.eugenethedev.taigamobile.domain.repositories.ISearchRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchRepositoryTest : BaseRepositoryTest() {
    lateinit var searchRepository: ISearchRepository

    @BeforeTest
    fun setupSearchRepositoryTest() {
        searchRepository = SearchRepository(mockTaigaApi)
    }

    @Test
    fun `test simple search projects size`() = runBlocking {
        val projects = searchRepository.searchProjects("", 1)
        assertEquals(9, projects.size)
        assertEquals(
            expected = listOf("test-test", "test-chaka-test", "Test-Scrum", "TransforMap", "Thunderbit", "Penpot", "PyConES 2016", "Pymiento", "Taiga"),
            actual = projects.map { it.name }
        )
    }

    @Test
    fun `test empty response on wrong page`() = runBlocking {
        val projects = searchRepository.searchProjects("", 100)
        assertEquals(0, projects.size)
    }
}
