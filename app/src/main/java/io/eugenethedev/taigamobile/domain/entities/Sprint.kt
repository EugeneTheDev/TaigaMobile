package io.eugenethedev.taigamobile.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Sprint(
    val id: Long,
    val name: String,
    val order: Int,
    val start: Date,
    val finish: Date,
    val storiesCount: Int,
    val isClosed: Boolean
) : Parcelable
