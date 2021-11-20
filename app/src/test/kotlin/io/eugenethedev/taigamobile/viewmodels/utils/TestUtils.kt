package io.eugenethedev.taigamobile.viewmodels.utils

import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

val accessDeniedException = HttpException(Response.error<String>(403, "".toResponseBody(null)))