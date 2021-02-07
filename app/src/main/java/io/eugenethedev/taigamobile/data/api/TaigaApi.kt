package io.eugenethedev.taigamobile.data.api

import retrofit2.http.Body
import retrofit2.http.POST

interface TaigaApi {
    companion object {
        const val API_PREFIX = "/api/v1/"
    }

    @POST("auth")
    suspend fun auth(@Body authRequest: AuthRequest): AuthResponse

}