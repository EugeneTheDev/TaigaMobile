package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.domain.repositories.ISearchRepository
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val taigaApi: TaigaApi
) : ISearchRepository {

    override suspend fun searchProjects(query: String, page: Int) = withIO {
        taigaApi.loadProjects(query, page).map { Project(it.id, it.name) }
    }
}