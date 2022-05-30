package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.data.api.EditWikiPageRequest
import io.eugenethedev.taigamobile.data.api.NewWikiLinkRequest
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.entities.Attachment
import io.eugenethedev.taigamobile.domain.repositories.IWikiRepository
import io.eugenethedev.taigamobile.state.Session
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

class WikiRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IWikiRepository {

    private val currentProjectId get() = session.currentProjectId.value

    override suspend fun getProjectWikiPages() = withIO {
        taigaApi.getProjectWikiPages(
            projectId = currentProjectId
        )
    }

    override suspend fun getProjectWikiPageBySlug(slug: String) = withIO {
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

    override suspend fun getPageAttachments(pageId: Long): List<Attachment> = withIO {
        taigaApi.getPageAttachments(
            pageId = pageId,
            projectId = currentProjectId
        )
    }

    override suspend fun addPageAttachment(pageId: Long, fileName: String, inputStream: InputStream) = withIO {
        val file = MultipartBody.Part.createFormData(
            name = "attached_file",
            filename = fileName,
            body = inputStream.readBytes().toRequestBody("*/*".toMediaType())
        )
        val project = MultipartBody.Part.createFormData("project", currentProjectId.toString())
        val objectId = MultipartBody.Part.createFormData("object_id", pageId.toString())

        inputStream.use {
            taigaApi.uploadPageAttachment(
                file = file,
                project = project,
                objectId = objectId
            )
        }
    }

    override suspend fun deletePageAttachment(attachmentId: Long) = withIO {
        taigaApi.deletePageAttachment(
            attachmentId = attachmentId
        )
        return@withIO
    }

    override suspend fun getWikiLinks() = withIO {
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