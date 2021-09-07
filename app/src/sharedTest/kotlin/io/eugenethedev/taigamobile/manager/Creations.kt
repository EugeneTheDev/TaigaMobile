package io.eugenethedev.taigamobile.manager

import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.testdata.TestData
import okhttp3.Request

interface Creations {
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

            // again, for some reason you can't specify watchers during epic creation, despite docs say opposite
            if (it.isWatching) {
                Request.Builder()
                    .apiEndpoint("epics/$epicId/watch")
                    .post("".toJsonBody())
                    .withAuth()
                    .build()
                    .execute()
                    .successOrThrow()
            }

            epicId
        }
    }
}
