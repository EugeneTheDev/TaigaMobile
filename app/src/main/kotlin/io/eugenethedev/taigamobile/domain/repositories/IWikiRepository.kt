package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.WikiLink
import io.eugenethedev.taigamobile.domain.entities.WikiPage

interface IWikiRepository {
    suspend fun getProjectWikiPages(): List<WikiPage>
    suspend fun getProjectWikiPageBySlug(slug: String): WikiPage
    suspend fun editWikiPage(pageId: Long, content: String, version: Int)
    suspend fun deleteWikiPage(pageId: Long)

    suspend fun getWikiLink(): List<WikiLink>
    suspend fun createWikiLink(href: String, title: String)
    suspend fun deleteWikiLink(linkId: Long)
}