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
    suspend fun getProjects(
        @Query("q") query: String,
        @Query("page") page: Int
    ): List<ProjectResponse>

    @GET("userstories/filters_data")
    suspend fun getFiltersData(
        @Query("project") project: Long,
        @Query("milestone") sprint: Any // workaround, since Retrofit drops null fields
    ): FiltersDataResponse

    @GET("userstories")
    suspend fun getUserStories(
        @Query("project") project: Long,
        @Query("milestone") sprint: Any,
        @Query("status") status: Long,
        @Query("page") page: Int
    ): List<UserStoryResponse>

    @GET("milestones")
    suspend fun getSprints(@Query("project") project: Long): List<SprintResponse>

}