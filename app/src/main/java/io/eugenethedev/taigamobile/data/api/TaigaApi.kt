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
        @Query("page") page: Int,
        @Query("order_by") order: String = "user_order"
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
    ): List<CommonTaskResponse>

    @GET("tasks")
    suspend fun getTasks(
        @Query("project") project: Long,
        @Query("milestone") sprint: Long?,
        @Query("user_story") userStory: Any,
        @Query("page") page: Int,
        @Query("order_by") order: String = "us_order"
    ): List<CommonTaskResponse>

    @GET("milestones")
    suspend fun getSprints(@Query("project") project: Long): List<SprintResponse>

}