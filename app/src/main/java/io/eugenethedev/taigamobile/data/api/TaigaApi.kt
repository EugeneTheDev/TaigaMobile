package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.Comment
import io.eugenethedev.taigamobile.domain.entities.ProjectInSearch
import io.eugenethedev.taigamobile.domain.entities.User
import retrofit2.http.*

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
    ): List<ProjectInSearch>

    @GET("projects/{id}")
    suspend fun getProject(@Path("id") projectId: Long): ProjectResponse

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
        @Query("page") page: Int?,
        @Query("order_by") order: String = "us_order"
    ): List<CommonTaskResponse>

    @GET("milestones")
    suspend fun getSprints(@Query("project") project: Long): List<SprintResponse>

    @GET("userstories/{id}")
    suspend fun getUserStory(@Path("id") storyId: Long): CommonTaskResponse

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") taskId: Long): CommonTaskResponse

    @GET("history/userstory/{id}?type=comment")
    suspend fun getUserStoryComments(@Path("id") userStoryId: Long): List<Comment>

    @GET("history/task/{id}?type=comment")
    suspend fun getTaskComments(@Path("id") taskId: Long): List<Comment>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Long): User

    @GET("projects/{id}/member_stats")
    suspend fun getMemberStats(@Path("id") projectId: Long): MemberStatsResponse
}