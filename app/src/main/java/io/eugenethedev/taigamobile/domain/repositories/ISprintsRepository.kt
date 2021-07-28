package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.Sprint
import java.time.LocalDate

interface ISprintsRepository {
    suspend fun getSprints(page: Int): List<Sprint>

    suspend fun getSprintIssues(sprintId: Long): List<CommonTask>
    suspend fun getSprintUserStories(sprintId: Long): List<CommonTask>
    suspend fun getSprintTasks(sprintId: Long): List<CommonTask>

    suspend fun createSprint(name: String, start: LocalDate, end: LocalDate)
}
