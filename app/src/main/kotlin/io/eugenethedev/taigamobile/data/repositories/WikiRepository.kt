package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.data.api.EditWikiPageRequest
import io.eugenethedev.taigamobile.data.api.NewWikiLinkRequest
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

    override suspend fun getProjectWikiPageBySlug(slug: String): WikiPage = withIO {
        taigaApi.getProjectWikiPageBySlug(
            projectId = currentProjectId,
            slug = slug
        )
    }

    override suspend fun editWikiPage(pageId: Long, content: String, version: Int) = withIO {
        taigaApi.editWikiPage(
            pageId = pageId,
            editWikiPageRequest = EditWikiPageRequest(content, version)
        )
    }

    override suspend fun deleteWikiPage(pageId: Long) = withIO {
        taigaApi.deleteWikiPage(
            pageId = pageId
        )
        return@withIO
    }

    override suspend fun getWikiLink(): List<WikiLink> = withIO {
        taigaApi.getWikiLink(
            projectId = currentProjectId
        )
    }

    override suspend fun createWikiLink(href: String, title: String) = withIO {
        taigaApi.createWikiLink(
            newWikiLinkRequest = NewWikiLinkRequest(
                href = href,
                project = currentProjectId,
                title = title
            )
        )
    }

    override suspend fun deleteWikiLink(linkId: Long) = withIO {
        taigaApi.deleteWikiLink(
            linkId = linkId
        )
        return@withIO
    }
}