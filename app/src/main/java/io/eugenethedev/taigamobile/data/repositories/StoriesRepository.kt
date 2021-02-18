package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.Story
import io.eugenethedev.taigamobile.domain.repositories.IStoriesRepository
import javax.inject.Inject

class StoriesRepository @Inject constructor(
    private val taigaApi: TaigaApi
) : IStoriesRepository {

    override suspend fun getStatuses(projectId: Long, sprintId: Long?) = withIO {
        taigaApi.getFiltersData(projectId, sprintId ?: "null").statuses
    }

    override suspend fun getStories(projectId: Long, statusId: Long, page: Int, sprintId: Long?) = withIO {
        taigaApi.getUserStories(projectId, sprintId ?: "null", statusId, page).map {
            Story(
                id = it.id,
                createdDate = it.created_date,
                title = it.subject,
                status = Status(
                    id = it.status,
                    name = it.status_extra_info.name,
                    color = it.status_extra_info.color
                ),
                assignee = it.assigned_to_extra_info?.let {
                    Story.Assignee(
                        id = it.id,
                        fullName = it.full_name_display
                    )
                }
            )
        }
    }
}