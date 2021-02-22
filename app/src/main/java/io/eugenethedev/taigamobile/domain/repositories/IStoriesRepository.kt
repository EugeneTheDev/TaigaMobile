package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.Story

interface IStoriesRepository {
    suspend fun getStatuses(sprintId: Long? = null): List<Status>
    suspend fun getStories(statusId: Long, page: Int, sprintId: Long? = null): List<Story>
    suspend fun getSprints(): List<Sprint>
}