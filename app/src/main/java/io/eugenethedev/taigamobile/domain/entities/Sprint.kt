package io.eugenethedev.taigamobile.domain.entities

import java.time.LocalDate

data class Sprint(
    val id: Long,
    val name: String,
    val order: Int,
    val start: LocalDate,
    val end: LocalDate,
    val storiesCount: Int,
    val isClosed: Boolean
)
