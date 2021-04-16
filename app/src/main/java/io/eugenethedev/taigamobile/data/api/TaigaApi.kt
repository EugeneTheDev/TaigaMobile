package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.Comment
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.domain.entities.User
import retrofit2.Response
import retrofit2.http.*

interface TaigaApi {
    companion object {
        const val API_PREFIX = "api/v1"
    }

    @POST("auth")
    suspend fun auth(@Body authRequest: AuthRequest): AuthResponse

    @GET("projects?order_by=user_order&slight=true")
    suspend fun getProjects(
        @Query("q") query: String,
        @Query("page") page: Int
    ): List<Project>

    @GET("projects/{id}")
    suspend fun getProject(@Path("id") projectId: Long): ProjectResponse

    @GET("userstories/filters_data")
    suspend fun getUserStoriesFiltersData(@Query("project") project: Long): FiltersDataResponse

    @GET("tasks/filters_data")
    suspend fun getTasksFiltersData(@Query("project") project: Long): FiltersDataResponse

    @GET("epics/filters_data")
    suspend fun getEpicsFiltersData(@Query("project") project: Long): FiltersDataResponse

    @GET("issues/filters_data")
    suspend fun getIssuesFiltersData(@Query("project") project: Long): FiltersDataResponse

    @GET("userstories")
    suspend fun getUserStories(
        @Query("project") project: Long?,
        @Query("milestone") sprint: Any?,
        @Query("status") status: Long?,
        @Query("epic") epic: Long?,
        @Query("page") page: Int?
    ): List<CommonTaskResponse>

    @GET("tasks?order_by=us_order")
    suspend fun getTasks(
        @Query("project") project: Long,
        @Query("milestone") sprint: Long?,
        @Query("user_story") userStory: Any,
        @Query("page") page: Int?
    ): List<CommonTaskResponse>

    @GET("epics")
    suspend fun getEpics(
        @Query("project") project: Long,
        @Query("page") page: Int,
        @Query("q") query: String?
    ): List<CommonTaskResponse>

    @GET("issues")
    suspend fun getIssues(
        @Query("project") project: Long,
        @Query("page") page: Int,
        @Query("q") query: String?,
        @Query("milestone") sprint: Long?
    ): List<CommonTaskResponse>

    @GET("milestones")
    suspend fun getSprints(
        @Query("project") project: Long,
        @Query("page") page: Int
    ): List<SprintResponse>

    @GET("userstories/{id}")
    suspend fun getUserStory(@Path("id") storyId: Long): CommonTaskResponse

    @GET("userstories/by_ref")
    suspend fun getUserStoryByRef(
        @Query("project") projectId: Long,
        @Query("ref") ref: Int
    ): CommonTaskResponse

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") taskId: Long): CommonTaskResponse

    @GET("epics/{id}")
    suspend fun getEpic(@Path("id") epicId: Long): CommonTaskResponse

    @GET("issues/{id}")
    suspend fun getIssue(@Path("id") issueId: Long): CommonTaskResponse

    @GET("history/userstory/{id}?type=comment")
    suspend fun getUserStoryComments(@Path("id") userStoryId: Long): List<Comment>

    @GET("history/task/{id}?type=comment")
    suspend fun getTaskComments(@Path("id") taskId: Long): List<Comment>

    @GET("history/epic/{id}?type=comment")
    suspend fun getEpicComments(@Path("id") epicId: Long): List<Comment>

    @GET("history/issue/{id}?type=comment")
    suspend fun getIssueComments(@Path("id") issueId: Long): List<Comment>

    @POST("history/userstory/{id}/delete_comment")
    suspend fun deleteUserStoryComment(
        @Path("id") userStoryId: Long,
        @Query("id") commentId: String
    )

    @POST("history/task/{id}/delete_comment")
    suspend fun deleteTaskComment(
        @Path("id") taskId: Long,
        @Query("id") commentId: String
    )

    @POST("history/epic/{id}/delete_comment")
    suspend fun deleteEpicComment(
        @Path("id") epicId: Long,
        @Query("id") commentId: String
    )

    @POST("history/issue/{id}/delete_comment")
    suspend fun deleteIssueComment(
        @Path("id") issueId: Long,
        @Query("id") commentId: String
    )

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Long): User

    @GET("users/me")
    suspend fun getMyProfile(): User

    @GET("projects/{id}/member_stats")
    suspend fun getMemberStats(@Path("id") projectId: Long): MemberStatsResponse

    @GET("milestones/{id}")
    suspend fun getSprint(@Path("id") sprintId: Long): SprintResponse

    @PATCH("userstories/{id}")
    suspend fun changeUserStoryStatus(
        @Path("id") id: Long,
        @Body changeStatusRequest: ChangeStatusRequest
    )

    @PATCH("tasks/{id}")
    suspend fun changeTaskStatus(
        @Path("id") id: Long,
        @Body changeStatusRequest: ChangeStatusRequest
    )

    @PATCH("epics/{id}")
    suspend fun changeEpicStatus(
        @Path("id") id: Long,
        @Body changeStatusRequest: ChangeStatusRequest
    )

    @PATCH("issues/{id}")
    suspend fun changeIssueStatus(
        @Path("id") id: Long,
        @Body changeStatusRequest: ChangeStatusRequest
    )

    @PATCH("issues/{id}")
    suspend fun changeIssueType(
        @Path("id") id: Long,
        @Body changeTypeRequest: ChangeTypeRequest
    )

    @PATCH("issues/{id}")
    suspend fun changeIssueSeverity(
        @Path("id") id: Long,
        @Body changeSeverityRequest: ChangeSeverityRequest
    )

    @PATCH("issues/{id}")
    suspend fun changeIssuePriority(
        @Path("id") id: Long,
        @Body changePriorityRequest: ChangePriorityRequest
    )

    @PATCH("userstories/{id}")
    suspend fun changeUserStorySprint(
        @Path("id") id: Long,
        @Body changeSprintRequest: ChangeSprintRequest
    )

    @PATCH("tasks/{id}")
    suspend fun changeTaskSprint(
        @Path("id") id: Long,
        @Body changeSprintRequest: ChangeSprintRequest
    )

    @PATCH("issues/{id}")
    suspend fun changeIssueSprint(
        @Path("id") id: Long,
        @Body changeSprintRequest: ChangeSprintRequest
    )

    @PATCH("userstories/{id}")
    suspend fun changeUserStoryAssignees(
        @Path("id") id: Long,
        @Body changeAssigneesRequest: ChangeUserStoryAssigneesRequest
    )

    @PATCH("userstories/{id}")
    suspend fun changeUserStoryWatchers(
        @Path("id") id: Long,
        @Body changeWatchersRequest: ChangeWatchersRequest
    )

    @PATCH("tasks/{id}")
    suspend fun changeTaskAssignees(
        @Path("id") id: Long,
        @Body changeAssigneesRequest: ChangeCommonTaskAssigneesRequest
    )

    @PATCH("epics/{id}")
    suspend fun changeEpicAssignees(
        @Path("id") id: Long,
        @Body changeAssigneesRequest: ChangeCommonTaskAssigneesRequest
    )

    @PATCH("issues/{id}")
    suspend fun changeIssueAssignees(
        @Path("id") id: Long,
        @Body changeAssigneesRequest: ChangeCommonTaskAssigneesRequest
    )

    @PATCH("tasks/{id}")
    suspend fun changeTaskWatchers(
        @Path("id") id: Long,
        @Body changeWatchersRequest: ChangeWatchersRequest
    )

    @PATCH("epics/{id}")
    suspend fun changeEpicWatchers(
        @Path("id") id: Long,
        @Body changeWatchersRequest: ChangeWatchersRequest
    )

    @PATCH("issues/{id}")
    suspend fun changeIssuesWatchers(
        @Path("id") id: Long,
        @Body changeWatchersRequest: ChangeWatchersRequest
    )

    @PATCH("userstories/{id}")
    suspend fun createUserStoryComment(
        @Path("id") id: Long,
        @Body createCommentRequest: CreateCommentRequest
    )

    @PATCH("tasks/{id}")
    suspend fun createTaskComment(
        @Path("id") id: Long,
        @Body createCommentRequest: CreateCommentRequest
    )

    @PATCH("epics/{id}")
    suspend fun createEpicComment(
        @Path("id") id: Long,
        @Body createCommentRequest: CreateCommentRequest
    )

    @PATCH("issues/{id}")
    suspend fun createIssueComment(
        @Path("id") id: Long,
        @Body createCommentRequest: CreateCommentRequest
    )

    @PATCH("userstories/{id}")
    suspend fun editUserStory(
        @Path("id") id: Long,
        @Body editRequest: EditCommonTaskRequest
    )

    @PATCH("tasks/{id}")
    suspend fun editTask(
        @Path("id") id: Long,
        @Body editRequest: EditCommonTaskRequest
    )

    @PATCH("epics/{id}")
    suspend fun editEpic(
        @Path("id") id: Long,
        @Body editRequest: EditCommonTaskRequest
    )

    @PATCH("issues/{id}")
    suspend fun editIssue(
        @Path("id") id: Long,
        @Body editRequest: EditCommonTaskRequest
    )

    @POST("userstories")
    suspend fun createUserStory(@Body createUserStoryRequest: CreateCommonTaskRequest): CommonTaskResponse

    @POST("tasks")
    suspend fun createTask(@Body createTaskRequest: CreateTaskRequest): CommonTaskResponse

    @POST("epics")
    suspend fun createEpic(@Body createEpicRequest: CreateCommonTaskRequest): CommonTaskResponse

    @POST("issues")
    suspend fun createIssue(@Body createIssueRequest: CreateIssueRequest): CommonTaskResponse

    @DELETE("userstories/{id}")
    suspend fun deleteUserStory(@Path("id") id: Long): Response<Void>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Long): Response<Void>

    @DELETE("epics/{id}")
    suspend fun deleteEpic(@Path("id") id: Long): Response<Void>

    @DELETE("issues/{id}")
    suspend fun deleteIssue(@Path("id") id: Long): Response<Void>

    @POST("epics/{id}/related_userstories")
    suspend fun linkToEpic(
        @Path("id") epicId: Long,
        @Body linkToEpicRequest: LinkToEpicRequest
    )

    @DELETE("epics/{epicId}/related_userstories/{userStoryId}")
    suspend fun unlinkFromEpic(
        @Path("epicId") epicId: Long,
        @Path("userStoryId") userStoryId: Long
    ): Response<Void>

    @POST("/tasks/{id}/promote_to_user_story")
    suspend fun promoteTaskToUserStory(
        @Path("id") taskId: Long,
        @Body promoteToUserStoryRequest: PromoteToUserStoryRequest
    ): List<Int>

    @POST("/issues/{id}/promote_to_user_story")
    suspend fun promoteIssueToUserStory(
        @Path("id") issueId: Long,
        @Body promoteToUserStoryRequest: PromoteToUserStoryRequest
    ): List<Int>

}
