package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.entities.WikiLink
import io.eugenethedev.taigamobile.domain.entities.WikiPage
import io.eugenethedev.taigamobile.domain.repositories.IWikiRepository
import io.eugenethedev.taigamobile.state.Session
import javax.inject.Inject

class WikiRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IWikiRepository {

    private val currentProjectId get() = session.currentProjectId.value

    override suspend fun getProjectWikiPages(): List<WikiPage> = withIO {
        taigaApi.getProjectWikiPages(
            projectId = currentProjectId
        )
    }

    override suspend fun getWikiLink(): List<WikiLink> = withIO {
        taigaApi.getWikiLink(
            currentProjectId
        )
    }

    override suspend fun deleteWikiPage(pageId: Long) {
        // TODO("Not yet implemented")
    }
}