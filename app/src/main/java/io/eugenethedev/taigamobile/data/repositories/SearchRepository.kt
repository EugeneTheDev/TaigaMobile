package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.domain.repositories.ISearchRepository
import retrofit2.HttpException
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val taigaApi: TaigaApi
) : ISearchRepository {

    override suspend fun searchProjects(query: String, page: Int) = withIO {
        try {
            taigaApi.getProjects(query, page)
        } catch (e: HttpException) {
            // suppress error if page not found (maximum page was reached)
            e.takeIf { it.code() == 404 }?.let { emptyList() } ?: throw e
        }
    }
}