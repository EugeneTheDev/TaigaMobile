package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.data.api.ChangeStatusRequest
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.data.api.CommonTaskResponse
import io.eugenethedev.taigamobile.data.api.SprintResponse
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.repositories.IStoriesRepository
import retrofit2.HttpException
import javax.inject.Inject

class StoriesRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IStoriesRepository {

    override suspend fun getStatuses(commonTaskType: CommonTaskType) = withIO {
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.getUserStoriesFiltersData(session.currentProjectId)
            CommonTaskType.TASK -> taigaApi.getTasksFiltersData(session.currentProjectId)
        }.statuses
    }

    override suspend fun getStories(statusId: Long, page: Int, sprintId: Long?) = withIO {
        handlePage {
            taigaApi.getUserStories(session.currentProjectId, sprintId ?: "null", statusId, page)
                .mapToCommonTask(CommonTaskType.USERSTORY)
        }
    }

    override suspend fun getSprints(page: Int) = withIO {
        handlePage {
            taigaApi.getSprints(session.currentProjectId, page).map { it.toSprint() }
        }
    }

    override suspend fun getSprintTasks(sprintId: Long, page: Int) = withIO {
        handlePage {
            taigaApi.getTasks(session.currentProjectId, sprintId, "null", page)
                .mapToCommonTask(CommonTaskType.TASK)
        }
    }

    override suspend fun getUserStoryTasks(storyId: Long) = withIO {
        handlePage {
            taigaApi.getTasks(session.currentProjectId, null, storyId, null).mapToCommonTask(CommonTaskType.TASK)
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
                sprint = it.milestone?.let { taigaApi.getSprint(it).toSprint() },
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
                },
                version = it.version
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
    
    private fun SprintResponse.toSprint() = Sprint(
        id = id,
        name = name,
        order = order,
        start = estimated_start,
        finish = estimated_finish,
        storiesCount = user_stories.size,
        isClosed = closed
    )

    private suspend fun <T> handlePage(action: suspend () -> List<T>): List<T> = try {
        action()
    } catch (e: HttpException) {
        // suppress error if page not found (maximum page was reached)
        e.takeIf { it.code() == 404 }?.let { emptyList() } ?: throw e
    }

    override suspend fun changeStatus(
        commonTaskId: Long,
        commonTaskType: CommonTaskType,
        statusId: Long,
        version: Int
    ) = withIO {
        val body = ChangeStatusRequest(statusId, version)
        when (commonTaskType) {
            CommonTaskType.USERSTORY -> taigaApi.changeUserStoryStatus(commonTaskId, body)
            CommonTaskType.TASK -> taigaApi.changeTaskStatus(commonTaskId, body)
        }
    }
}