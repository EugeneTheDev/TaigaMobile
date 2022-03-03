package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.paging.CommonPagingSource
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * All API endpoints
 */
interface TaigaApi {
    companion object {
        const val API_PREFIX = "api/v1"
        const val AUTH_ENDPOINTS = "auth"
        const val REFRESH_ENDPOINT = "auth/refresh"
    }

    @POST("auth")
    suspend fun auth(@Body authRequest: AuthRequest): AuthResponse


    /**
     * Projects
     */

    @GET("projects?order_by=user_order&slight=true")
    suspend fun getProjects(
        @Query("q") query: String? = null,
        @Query("page") page: Int? = null,
        @Query("member") memberId: Long? = null,
        @Query("page_size") pageSize: Int? = null
    ): List<Project>

    @GET("projects/{id}")
    suspend fun getProject(@Path("id") projectId: Long): ProjectResponse


    /**
     * Users
     */

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Long): User

    @GET("users/me")
    suspend fun getMyProfile(): User

    @GET("users/{id}/stats")
    suspend fun getUserStats(@Path("id") userId: Long): Stats

    @GET("projects/{id}/member_stats")
    suspend fun getMemberStats(@Path("id") projectId: Long): MemberStatsResponse


    /**
     * Sprints
     */
    @GET("milestones")
    suspend fun getSprints(
        @Query("project") project: Long,
        @Query("page") page: Int,
        @Query("closed") isClosed: Boolean
    ): List<SprintResponse>

    @GET("milestones/{id}")
    suspend fun getSprint(@Path("id") sprintId: Long): SprintResponse

    @PATCH("{taskPath}/{id}")
    suspend fun changeCommonTaskSprint(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long,
        @Body changeSprintRequest: ChangeSprintRequest
    )

    @POST("milestones")
    suspend fun createSprint(@Body request: CreateSprintRequest)

    @PATCH("milestones/{id}")
    suspend fun editSprint(
        @Path("id") id: Long,
        @Body request: EditSprintRequest
    )

    @DELETE("milestones/{id}")
    suspend fun deleteSprint(@Path("id") id: Long): Response<Void>

    /**
     * Everything related to common tasks (epics, user stories, etc.)
     */

    @GET("{taskPath}/filters_data")
    suspend fun getCommonTaskFiltersData(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Query("project") project: Long
    ): FiltersDataResponse

    @GET("userstories")
    suspend fun getUserStories(
        @Query("project") project: Long? = null,
        @Query("milestone") sprint: Any? = null,
        @Query("status") status: Long? = null,
        @Query("epic") epic: Long? = null,
        @Query("page") page: Int? = null,
        @Query("assigned_users") assignedId: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,
        @Query("dashboard") isDashboard: Boolean? = null,
        @Query("q") query: String? = null,
        @Query("page_size") pageSize: Int = CommonPagingSource.PAGE_SIZE,

        // List<Long?>?
        @Query("assigned_to", encoded = true) assignedIds: String? = null,
        @Query("epic", encoded = true) epics: String? = null,

        // List<Long>?
        @Query("owner", encoded = true) ownerIds: String? = null,
        @Query("role", encoded = true) roles: String? = null,
        @Query("status", encoded = true) statuses: String? = null,

        // List<String>?
        @Query("tags", encoded = true) tags: String? = null,

        // here and below instead of setting header to "false" remove it,
        // because api always returns unpaginated result if header persists, regardless of its value
        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("tasks?order_by=us_order")
    suspend fun getTasks(
        @Query("user_story") userStory: Any? = null,
        @Query("project") project: Long? = null,
        @Query("milestone") sprint: Long? = null,
        @Query("page") page: Int? = null,
        @Query("assigned_to") assignedId: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,

        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("epics")
    suspend fun getEpics(
        @Query("page") page: Int? = null,
        @Query("project") project: Long? = null,
        @Query("q") query: String? = null,
        @Query("assigned_to") assignedId: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,
        @Query("page_size") pageSize: Int = CommonPagingSource.PAGE_SIZE,

        // List<Long?>?
        @Query("assigned_to", encoded = true) assignedIds: String? = null,

        // List<Long>?
        @Query("owner", encoded = true) ownerIds: String? = null,
        @Query("status", encoded = true) statuses: String? = null,

        // List<String>?
        @Query("tags", encoded = true) tags: String? = null,

        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("issues")
    suspend fun getIssues(
        @Query("page") page: Int? = null,
        @Query("project") project: Long? = null,
        @Query("q") query: String? = null,
        @Query("milestone") sprint: Long? = null,
        @Query("status__is_closed") isClosed: Boolean? = null,
        @Query("watchers") watcherId: Long? = null,
        @Query("page_size") pageSize: Int = CommonPagingSource.PAGE_SIZE,

        // List<Long?>?
        @Query("assigned_to", encoded = true) assignedIds: String? = null,

        // List<Long>?
        @Query("owner", encoded = true) ownerIds: String? = null,
        @Query("priority", encoded = true) priorities: String? = null,
        @Query("severity", encoded = true) severities: String? = null,
        @Query("type", encoded = true) types: String? = null,
        @Query("role", encoded = true) roles: String? = null,
        @Query("status", encoded = true) statuses: String? = null,

        // List<String>?
        @Query("tags", encoded = true) tags: String? = null,

        @Header("x-disable-pagination") disablePagination: Boolean? = (page == null).takeIf { it }
    ): List<CommonTaskResponse>

    @GET("userstories/by_ref")
    suspend fun getUserStoryByRef(
        @Query("project") projectId: Long,
        @Query("ref") ref: Int
    ): CommonTaskResponse

    @GET("{taskPath}/{id}")
    suspend fun getCommonTask(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long
    ): CommonTaskResponse

    @PATCH("{taskPath}/{id}")
    suspend fun changeCommonTaskStatus(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
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
    suspend fun changeUserStoryAssignees(
        @Path("id") id: Long,
        @Body changeAssigneesRequest: ChangeUserStoryAssigneesRequest
    )

    @PATCH("{taskPath}/{id}")
    suspend fun changeCommonTaskAssignees(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long,
        @Body changeAssigneesRequest: ChangeCommonTaskAssigneesRequest
    )

    @PATCH("{taskPath}/{id}")
    suspend fun changeCommonTaskWatchers(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long,
        @Body changeWatchersRequest: ChangeWatchersRequest
    )

    @PATCH("{taskPath}/{id}")
    suspend fun changeCommonTaskDueDate(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long,
        @Body request: ChangeCommonTaskDueDateRequest
    )

    @PATCH("epics/{id}")
    suspend fun changeEpicColor(
        @Path("id") id: Long,
        @Body request: ChangeEpicColor
    )

    @PATCH("{taskPath}/{id}")
    suspend fun editCommonTask(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long,
        @Body editRequest: EditCommonTaskRequest
    )

    @POST("{taskPath}")
    suspend fun createCommonTask(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Body createRequest: CreateCommonTaskRequest
    ): CommonTaskResponse

    @POST("tasks")
    suspend fun createTask(@Body createTaskRequest: CreateTaskRequest): CommonTaskResponse

    @POST("issues")
    suspend fun createIssue(@Body createIssueRequest: CreateIssueRequest): CommonTaskResponse

    @POST("userstories")
    suspend fun createUserstory(@Body createUserStoryRequest: CreateUserStoryRequest): CommonTaskResponse

    @DELETE("{taskPath}/{id}")
    suspend fun deleteCommonTask(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long
    ): Response<Void>

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

    @POST("{taskPath}/{id}/promote_to_user_story")
    suspend fun promoteCommonTaskToUserStory(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") taskId: Long,
        @Body promoteToUserStoryRequest: PromoteToUserStoryRequest
    ): List<Int>

    // Tasks comments

    @PATCH("{taskPath}/{id}")
    suspend fun createCommonTaskComment(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") id: Long,
        @Body createCommentRequest: CreateCommentRequest
    )

    @GET("history/{taskPath}/{id}?type=comment")
    suspend fun getCommonTaskComments(
        @Path("taskPath") taskPath: CommonTaskPathSingular,
        @Path("id") id: Long
    ): List<Comment>

    @POST("history/{taskPath}/{id}/delete_comment")
    suspend fun deleteCommonTaskComment(
        @Path("taskPath") taskPath: CommonTaskPathSingular,
        @Path("id") id: Long,
        @Query("id") commentId: String
    )

    // Tasks attachments

    @GET("{taskPath}/attachments")
    suspend fun getCommonTaskAttachments(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Query("object_id") storyId: Long,
        @Query("project") projectId: Long
    ): List<Attachment>

    @DELETE("{taskPath}/attachments/{id}")
    suspend fun deleteCommonTaskAttachment(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") attachmentId: Long
    ): Response<Void>

    @POST("{taskPath}/attachments")
    @Multipart
    suspend fun uploadCommonTaskAttachment(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Part file: MultipartBody.Part,
        @Part project: MultipartBody.Part,
        @Part objectId: MultipartBody.Part
    )

    // Custom attributes

    @GET("{taskPath}-custom-attributes")
    suspend fun getCustomAttributes(
        @Path("taskPath") taskPath: CommonTaskPathSingular,
        @Query("project") projectId: Long
    ): List<CustomAttributeResponse>

    @GET("{taskPath}/custom-attributes-values/{id}")
    suspend fun getCustomAttributesValues(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") taskId: Long
    ): CustomAttributesValuesResponse

    @PATCH("{taskPath}/custom-attributes-values/{id}")
    suspend fun editCustomAttributesValues(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") taskId: Long,
        @Body editRequest: EditCustomAttributesValuesRequest
    )

    // Tags

    @PATCH("{taskPath}/{id}")
    suspend fun editTags(
        @Path("taskPath") taskPath: CommonTaskPathPlural,
        @Path("id") taskId: Long,
        @Body editRequest: EditTagsRequest
    )

    // Swimlanes

    @GET("swimlanes")
    suspend fun getSwimlanes(@Query("project") project: Long): List<Swimlane>

    @PATCH("userstories/{id}")
    suspend fun changeUserStorySwimlane(
        @Path("id") id: Long,
        @Body request: ChangeUserStorySwimlaneRequest
    )
}
