package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.WikiLink
import io.eugenethedev.taigamobile.domain.entities.WikiPage

interface IWikiRepository {
    suspend fun getProjectWikiPages(): List<WikiPage>
    suspend fun getWikiLink(): List<WikiLink>
    suspend fun deleteWikiPage(pageId: Long)
    suspend fun deleteWikiLink(linkId: Long)
    suspend fun editWikiPage(pageId: Long, content: String, version: Int)
}