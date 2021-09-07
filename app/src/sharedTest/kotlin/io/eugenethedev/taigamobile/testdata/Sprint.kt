package io.eugenethedev.taigamobile.testdata

import java.time.LocalDate

data class Sprint(
    val name: String,
    val start: LocalDate,
    val end: LocalDate,
    val tasks: List<Task> = emptyList()
)