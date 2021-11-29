package io.eugenethedev.taigamobile.viewmodels.utils

import io.eugenethedev.taigamobile.domain.entities.TeamMember
import io.eugenethedev.taigamobile.testdata.User
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

val accessDeniedException = HttpException(Response.error<String>(403, "".toResponseBody(null)))

fun User.toTeamMember(id: Long, name: String, role: String, totalPower: Int) = TeamMember(
    id = id,
    avatarUrl = null,
    name = name,
    role = role,
    username = this.username,
    totalPower = totalPower
)
