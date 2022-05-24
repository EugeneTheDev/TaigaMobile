package io.eugenethedev.taigamobile.domain.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class WikiPage(
    val id: Long,
    val version: Int,
    val content: String,
    val editions: Long,
    @Json(name = "created_date") val cratedDate: LocalDateTime,
    @Json(name = "is_watcher") val isWatcher: Boolean,
    @Json(name = "last_modifier") val lastModifier: Long,
    @Json(name = "modified_date") val modifiedDate: LocalDateTime,
    @Json(name = "total_watchers") val totalWatchers: Long,
    @Json(name = "slug")val slug: String
)

@JsonClass(generateAdapter = true)
data class WikiLink(
    @Json(name = "href") val ref: String,
    val id: Long,
    val order: Long,
    val title: String
)