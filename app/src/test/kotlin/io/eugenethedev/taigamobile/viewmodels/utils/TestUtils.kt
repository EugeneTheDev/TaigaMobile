package io.eugenethedev.taigamobile.viewmodels.utils

import io.eugenethedev.taigamobile.domain.entities.TeamMember
import io.eugenethedev.taigamobile.testdata.User
import io.eugenethedev.taigamobile.ui.utils.Result
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals


val accessDeniedException = HttpException(Response.error<String>(403, "".toResponseBody(null)))
val badInternetException = HttpException(Response.error<String>(404, "".toResponseBody(null)))

fun User.toTeamMember(id: Long, name: String, role: String, totalPower: Int) = TeamMember(
    id = id,
    avatarUrl = null,
    name = name,
    role = role,
    username = this.username,
    totalPower = totalPower
)

fun <T> assertResultEquals(expected: Result<T>, actual: Result<T>) {
    assertEquals(expected::class, actual::class)
    assertEquals(expected.data, actual.data)
    assertEquals(expected.message, actual.message)
}
