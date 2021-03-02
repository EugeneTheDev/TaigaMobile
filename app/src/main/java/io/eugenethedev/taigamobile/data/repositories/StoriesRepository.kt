package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.data.api.CommonTaskResponse
import io.eugenethedev.taigamobile.domain.entities.*
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
            taigaApi.getUserStories(session.currentProjectId, sprintId ?: "null", statusId, page).mapToCommonTask(CommonTaskType.USERSTORY)
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
            taigaApi.getTasks(session.currentProjectId, sprintId, "null", page).mapToCommonTask(CommonTaskType.TASK)
        } catch (e: HttpException) {
            // suppress error if page not found (maximum page was reached)
            e.takeIf { it.code() == 404 }?.let { emptyList() } ?: throw e
        }
    }

    override suspend fun getUserStoryTasks(storyId: Long) = withIO {
        try {
            taigaApi.getTasks(session.currentProjectId, null, storyId, null).mapToCommonTask(CommonTaskType.TASK)
        } catch (e: HttpException) {
            // suppress error if page not found (maximum page was reached)
            e.takeIf { it.code() == 404 }?.let { emptyList() } ?: throw e
        }
    }

    override suspend fun getCommonTask(commonTaskId: Long, type: CommonTaskType) = withIO {
        when (type) {
            CommonTaskType.USERSTORY -> taigaApi.getUserStory(commonTaskId)
            CommonTaskType.TASK -> taigaApi.getTask(commonTaskId)
        }.let {
            CommonTaskExtended(
                id = it.id,
                status = Status(
                    id = it.status,
                    name = it.status_extra_info.name,
                    color = it.status_extra_info.color
                ),
                createdDateTime = it.created_date,
                sprintId = it.milestone,
                sprintName = it.milestone_name,
                assignedIds = it.assigned_users ?: listOf(it.assigned_to),
                watcherIds = it.watchers,
                creatorId = it.owner,
                ref = it.ref,
                title = it.subject,
                description = it.description,
                epics = it.epics.orEmpty(),
                projectSlug = it.project_extra_info.slug,
                userStoryShortInfo = it.user_story_extra_info?.let {
                    UserStoryShortInfo(
                        id = it.id,
                        ref = it.ref,
                        title = it.subject,
                        epicColor = it.epics?.first()?.color
                    )
                }
            )
        }
    }

    override suspend fun getComments(commonTaskId: Long, type: CommonTaskType) = withIO {
        when (type) {
            CommonTaskType.USERSTORY -> taigaApi.getUserStoryComments(commonTaskId)
            CommonTaskType.TASK -> taigaApi.getTaskComments(commonTaskId)
        }.sortedBy { it.postDateTime }
    }

    private fun List<CommonTaskResponse>.mapToCommonTask(commonTaskType: CommonTaskType) = map {
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
            },
            projectSlug = it.project_extra_info.slug,
            taskType = commonTaskType
        )
    }

}