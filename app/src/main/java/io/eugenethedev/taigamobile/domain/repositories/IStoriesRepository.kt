package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.*

interface IStoriesRepository {
    suspend fun getStatuses(sprintId: Long? = null): List<Status>
    suspend fun getStories(statusId: Long, page: Int, sprintId: Long? = null): List<CommonTask>
    suspend fun getSprints(): List<Sprint>
    suspend fun getUserStoryTasks(storyId: Long): List<CommonTask>
    suspend fun getSprintTasks(sprintId: Long, page: Int): List<CommonTask>
    suspend fun getUserStory(storyId: Long): CommonTaskExtended
    suspend fun getComments(storyId: Long): List<Comment>
}