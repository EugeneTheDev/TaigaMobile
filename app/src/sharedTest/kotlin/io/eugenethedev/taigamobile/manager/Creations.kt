package io.eugenethedev.taigamobile.manager

import io.eugenethedev.taigamobile.data.api.CommonTaskPathPlural
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.testdata.Task
import io.eugenethedev.taigamobile.testdata.TestData
import okhttp3.Request

interface Creations {
    private fun TaigaTestInstanceManager.watchTask(taskType: CommonTaskType, taskId: Long) {
        Request.Builder()
            .apiEndpoint("${CommonTaskPathPlural(taskType).path}/$taskId/watch")
            .post("".toJsonBody())
            .withAuth()
            .build()
            .execute()
            .successOrThrow()
    }

    private fun TaigaTestInstanceManager.postComment(
        taskType: CommonTaskType,
        taskId: Long,
        comment: String,
        version: Int = 1
    ) {
        Request.Builder()
            .apiEndpoint("${CommonTaskPathPlural(taskType).path}/$taskId")
            .patch(
                """
                   {
                       "comment": "$comment",
                       "version": $version
                   }
                """.trimIndent().toJsonBody()
            )
            .withAuth()
            .build()
            .execute()
            .successOrThrow()
    }

    private fun TaigaTestInstanceManager.createTasks(tasks: List<Task>, sprintId: Long? = null, userStoryId: Long? = null) {
        tasks.forEach {
            val taskId = Request.Builder()
                .apiEndpoint("tasks")
                .post(
                    """
                        {
                            "project": $projectId,
                            "subject": "${it.name}",
                            "description": "${it.description}",
                            "milestone": $sprintId,
                            "user_story": $userStoryId,
                            "assigned_to": ${if (it.isAssigned) userId else null}
                            ${
                        if (it.isClosed)
                            """, "status": ${getClosedStatusId(CommonTaskType.Task)}"""
                        else
                            ""
                    }
                        }
                    """.trimIndent().toJsonBody()
                )
                .withAuth()
                .build()
                .execute()
                .toJsonObject()
                .get("id")
                .asLong

            var version = 1
            it.comments.forEach {
                postComment(
                    taskId = taskId,
                    taskType = CommonTaskType.Task,
                    comment = it,
                    version = version++
                )
            }

            if (it.isWatching) {
                watchTask(CommonTaskType.Task, taskId)
            }

        }
    }

    fun TaigaTestInstanceManager.createTestUser() = with(TestData.User) {
        Request.Builder()
            .apiEndpoint("auth/register")
            .post(
                """
                {
                    "accepted_terms": true,
                    "email": "$email",
                    "full_name": "$fullName",
                    "password": "$password",
                    "type": "public",
                    "username": "$username"
                }
            """.trimIndent().toJsonBody()
            )
            .build()
            .execute()
            .toJsonObject()
            .let {
                accessToken = it.get("auth_token").asString
                refreshToken = it.get("refresh").asString
                userId = it.get("id").asLong
            }
    }

    fun TaigaTestInstanceManager.createTestProject() = with(TestData.Project) {
        Request.Builder()
            .apiEndpoint("projects")
            .post(
                """
                    {
                        "name": "$name",
                        "description": "$description",
                        "is_private": false,
                        "is_issues_activated": true,
                        "is_backlog_activated": true,
                        "is_wiki_activated": true
                    }
                """.trimIndent().toJsonBody()
            )
            .withAuth()
            .build()
            .execute()
            .toJsonObject()
            .let {
                projectId = it.get("id").asLong
            }

        // for some reason these parameters are ignored in create project request
        Request.Builder()
            .apiEndpoint("projects/$projectId")
            .patch(
                """
                    {
                        "is_epics_activated": true,
                        "is_kanban_activated": true
                    }
                """.trimIndent().toJsonBody()
            )
            .withAuth()
            .build()
            .execute()
            .successOrThrow()
    }

    fun TaigaTestInstanceManager.createSprints() = with(TestData.Project) {
        sprintToId = sprints.associateWith {
            val sprintId = Request.Builder()
                .apiEndpoint("milestones")
                .post(
                    """
                       {
                           "project": $projectId,
                           "name": "${it.name}",
                           "estimated_start": "${it.start}",
                           "estimated_finish": "${it.end}"
                       }
                    """.trimIndent().toJsonBody()
                )
                .withAuth()
                .build()
                .execute()
                .toJsonObject()
                .get("id")
                .asLong

            createTasks(it.tasks, sprintId = sprintId)

            sprintId
        }
    }

    fun TaigaTestInstanceManager.createEpics() = with (TestData.Project) {
        epicToId = epics.associateWith {
            val epicId = Request.Builder()
                .apiEndpoint("epics")
                .post(
                    """
                        {
                            "project": $projectId,
                            "subject": "${it.name}",
                            "description": "${it.description}",
                            "assigned_to": ${if (it.isAssigned) userId else null}
                            ${
                                if (it.isClosed)
                                    """, "status": ${getClosedStatusId(CommonTaskType.Epic)}"""
                                else
                                    ""
                            }
                        }
                    """.trimIndent().toJsonBody()
                )
                .withAuth()
                .build()
                .execute()
                .toJsonObject()
                .get("id")
                .asLong

            var version = 1
            it.comments.forEach {
                postComment(
                    taskId = epicId,
                    taskType = CommonTaskType.Epic,
                    comment = it,
                    version = version++
                )
            }

            // again, for some reason you can't specify watchers during epic creation, despite docs say opposite
            if (it.isWatching) {
                watchTask(CommonTaskType.Epic, epicId)
            }

            epicId
        }
    }

    fun TaigaTestInstanceManager.createUserStories() = with(TestData.Project) {
        userstories.forEach {
            val userStoryId = Request.Builder()
                .apiEndpoint("userstories")
                .post(
                    """
                        {
                            "project": $projectId,
                            "subject": "${it.name}",
                            "description": "${it.description}",
                            "assigned_to": ${if (it.isAssigned) userId else null},
                            "milestone": ${sprintToId[it.sprint]}
                            ${
                                if (it.isClosed)
                                    """, "status": ${getClosedStatusId(CommonTaskType.UserStory)}"""
                                else
                                    ""
                            }
                        }
                    """.trimIndent().toJsonBody()
                )
                .withAuth()
                .build()
                .execute()
                .toJsonObject()
                .get("id")
                .asLong

            var version = 1
            it.comments.forEach {
                postComment(
                    taskId = userStoryId,
                    taskType = CommonTaskType.UserStory,
                    comment = it,
                    version = version++
                )
            }

            it.epics.forEach {
                Request.Builder()
                    .apiEndpoint("epics/${epicToId[it]}/related_userstories")
                    .post(
                        """
                            {
                                "epic": ${epicToId[it]},
                                "user_story": $userStoryId
                            }
                        """.trimIndent().toJsonBody()
                    )
                    .withAuth()
                    .build()
                    .execute()
                    .successOrThrow()
            }

            if (it.isWatching) {
                watchTask(CommonTaskType.UserStory, userStoryId)
            }

            createTasks(it.tasks, userStoryId = userStoryId)
        }
    }

    fun TaigaTestInstanceManager.createIssues() = with(TestData.Project) {
        issues.forEach {
            val issueId = Request.Builder()
                .apiEndpoint("issues")
                .post(
                    """
                        {
                            "project": $projectId,
                            "subject": "${it.name}",
                            "description": "${it.description}",
                            "assigned_to": ${if (it.isAssigned) userId else null},
                            "milestone": ${sprintToId[it.sprint]}
                            ${
                                if (it.isClosed)
                                    """, "status": ${getClosedStatusId(CommonTaskType.UserStory)}"""
                                else
                                    ""
                            }
                        }
                    """.trimIndent().toJsonBody()
                )
                .withAuth()
                .build()
                .execute()
                .toJsonObject()
                .get("id")
                .asLong

            var version = 1
            it.comments.forEach {
                postComment(
                    taskId = issueId,
                    taskType = CommonTaskType.Issue,
                    comment = it,
                    version = version++
                )
            }

            if (it.isWatching) {
                watchTask(CommonTaskType.Issue, issueId)
            }
        }
    }
}
