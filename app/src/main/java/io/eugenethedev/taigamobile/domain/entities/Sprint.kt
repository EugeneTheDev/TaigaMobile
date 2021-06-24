package io.eugenethedev.taigamobile.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Sprint(
    val id: Long,
    val name: String,
    val order: Int,
    val start: LocalDate,
    val finish: LocalDate,
    val storiesCount: Int,
    val isClosed: Boolean
) : Parcelable
