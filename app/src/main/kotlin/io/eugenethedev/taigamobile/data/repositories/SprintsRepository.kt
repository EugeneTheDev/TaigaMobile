package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.data.api.CreateSprintRequest
import io.eugenethedev.taigamobile.data.api.EditSprintRequest
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import java.time.LocalDate
import javax.inject.Inject

class SprintsRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : ISprintsRepository {
    private val currentProjectId get() = session.currentProjectId.value
    
    override suspend fun getSprintUserStories(sprintId: Long) = withIO {
        taigaApi.getUserStories(project = currentProjectId, sprint = sprintId)
            .map { it.toCommonTask(CommonTaskType.UserStory) }
    }

    override suspend fun getSprints(page: Int) = withIO {
        handle404 {
            taigaApi.getSprints(currentProjectId, page).map { it.toSprint() }
        }
    }

    override suspend fun getSprint(sprintId: Long) = withIO {
        taigaApi.getSprint(sprintId).toSprint()
    }

    override suspend fun getSprintTasks(sprintId: Long) = withIO {
        taigaApi.getTasks(userStory = "null", project = currentProjectId, sprint = sprintId)
            .map { it.toCommonTask(CommonTaskType.Task) }
    }

    override suspend fun getSprintIssues(sprintId: Long) = withIO {
        taigaApi.getIssues(project = currentProjectId, sprint = sprintId)
            .map { it.toCommonTask(CommonTaskType.Issue) }

    }

    override suspend fun createSprint(name: String, start: LocalDate, end: LocalDate) = withIO {
        taigaApi.createSprint(CreateSprintRequest(name, start, end, currentProjectId))
    }

    override suspend fun editSprint(
        sprintId: Long,
        name: String,
        start: LocalDate,
        end: LocalDate
    ) = withIO {
        taigaApi.editSprint(
            id = sprintId,
            request = EditSprintRequest(name, start, end)
        )
    }

    override suspend fun deleteSprint(sprintId: Long) = withIO {
        taigaApi.deleteSprint(sprintId)
        return@withIO
    }
}
