package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.data.api.CommonTaskResponse
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.repositories.IStoriesRepository
import retrofit2.HttpException
import javax.inject.Inject

class StoriesRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IStoriesRepository {

    override suspend fun getStatuses(sprintId: Long?) = withIO {
        taigaApi.getFiltersData(session.currentProjectId, sprintId ?: "null").statuses
    }

    override suspend fun getStories(statusId: Long, page: Int, sprintId: Long?) = withIO {
        try {
            taigaApi.getUserStories(session.currentProjectId, sprintId ?: "null", statusId, page).mapToStory()
        } catch (e: HttpException) {
            // suppress error if page not found (maximum page was reached)
            e.takeIf { it.code() == 404 }?.let { emptyList() } ?: throw e
        }
    }

    override suspend fun getSprints() = withIO {
        taigaApi.getSprints(session.currentProjectId).map {
            Sprint(
                id = it.id,
                name = it.name,
                order = it.order,
                start = it.estimated_start,
                finish = it.estimated_finish,
                storiesCount = it.user_stories.size,
                isClosed = it.closed
            )
        }
    }

    override suspend fun getSprintTasks(sprintId: Long, page: Int) = withIO {
        try {
            taigaApi.getTasks(session.currentProjectId, sprintId, "null", page).mapToStory()
        } catch (e: HttpException) {
            // suppress error if page not found (maximum page was reached)
            e.takeIf { it.code() == 404 }?.let { emptyList() } ?: throw e
        }
    }

    override suspend fun getUserStoryTasks(storyId: Long, page: Int) = withIO {
        try {
            taigaApi.getTasks(session.currentProjectId, null, storyId, page).mapToStory()
        } catch (e: HttpException) {
            // suppress error if page not found (maximum page was reached)
            e.takeIf { it.code() == 404 }?.let { emptyList() } ?: throw e
        }
    }

    private fun List<CommonTaskResponse>.mapToStory() = map {
        CommonTask(
            id = it.id,
            createdDate = it.created_date,
            title = it.subject,
            ref = it.ref,
            status = Status(
                id = it.status,
                name = it.status_extra_info.name,
                color = it.status_extra_info.color
            ),
            assignee = it.assigned_to_extra_info?.let {
                CommonTask.Assignee(
                    id = it.id,
                    fullName = it.full_name_display
                )
            }
        )
    }

}