package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.repositories.ISearchRepository
import javax.inject.Inject

class ProjectsRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IProjectsRepository {

    override suspend fun searchProjects(query: String, page: Int) = withIO {
        handle404 {
            taigaApi.getProjects(query, page)
        }
    }
}