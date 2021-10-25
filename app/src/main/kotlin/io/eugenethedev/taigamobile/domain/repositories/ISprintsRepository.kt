package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.Sprint
import java.time.LocalDate

interface ISprintsRepository {
    suspend fun getSprints(page: Int, isClosed: Boolean = false): List<Sprint>
    suspend fun getSprint(sprintId: Long): Sprint

    suspend fun getSprintIssues(sprintId: Long): List<CommonTask>
    suspend fun getSprintUserStories(sprintId: Long): List<CommonTask>
    suspend fun getSprintTasks(sprintId: Long): List<CommonTask>

    suspend fun createSprint(name: String, start: LocalDate, end: LocalDate)
    suspend fun editSprint(sprintId: Long, name: String, start: LocalDate, end: LocalDate)
    suspend fun deleteSprint(sprintId: Long)
}
