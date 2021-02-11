package io.eugenethedev.taigamobile.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TaigaApi {
    companion object {
        const val API_PREFIX = "/api/v1/"
    }

    @POST("auth")
    suspend fun auth(@Body authRequest: AuthRequest): AuthResponse

    @GET("projects")
    suspend fun loadProjects(
        @Query("q") query: String,
        @Query("page") page: Int
    ): List<ProjectResponse>

}