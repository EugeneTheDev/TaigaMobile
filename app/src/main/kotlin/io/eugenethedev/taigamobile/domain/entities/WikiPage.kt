package io.eugenethedev.taigamobile.domain.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class WikiPage(
    val content: String,
    @Json(name = "created_date") val cratedDate: LocalDateTime,
    val editions: Long,
    val id: Long,
    @Json(name = "is_watcher") val isWatcher: Boolean,
    @Json(name = "last_modifier") val lastModifier: Long,
    @Json(name = "modified_date") val modifiedDate: LocalDateTime,
    @Json(name = "total_watchers") val totalWatchers: Long,
    val version: Long
)

@JsonClass(generateAdapter = true)
data class WikiLink(
    @Json(name = "href") val ref: String,
    val order: Long,
    val title: String
)