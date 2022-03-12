package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.paging.CommonPagingSource
import io.eugenethedev.taigamobile.domain.repositories.IProjectsRepository
import io.eugenethedev.taigamobile.state.Session
import javax.inject.Inject

class ProjectsRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IProjectsRepository {

    override suspend fun searchProjects(query: String, page: Int) = withIO {
        handle404 {
            taigaApi.getProjects(
                query = query,
                page = page,
                pageSize = CommonPagingSource.PAGE_SIZE
            )
        }
    }

    override suspend fun getMyProjects() = withIO {
        taigaApi.getProjects(memberId = session.currentUserId.value)
    }

    override suspend fun getProjectSlug(id: Long) = withIO {
        taigaApi.getProject(id).slug
    }
}