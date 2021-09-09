package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.SearchRepository
import io.eugenethedev.taigamobile.domain.repositories.ISearchRepository
import io.eugenethedev.taigamobile.testdata.TestData
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
    fun `test simple search projects`() = runBlocking {
        val projects = searchRepository.searchProjects("", 1)
        assertEquals(TestData.projects.size, projects.size)
        assertEquals(
            expected = TestData.projects.map { it.name },
            actual = projects.map { it.name }
        )
    }

    @Test
    fun `test empty response on wrong query or page`() = runBlocking {
        assertEquals(0, searchRepository.searchProjects("", 100).size)
        assertEquals(0, searchRepository.searchProjects("dumb string", 1).size)
    }
}
