package io.eugenethedev.taigamobile.manager

import io.eugenethedev.taigamobile.data.api.CommonTaskPathPlural
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.testdata.Comment
import io.eugenethedev.taigamobile.testdata.Task
import io.eugenethedev.taigamobile.testdata.TestData
import io.eugenethedev.taigamobile.testdata.User
import okhttp3.Request
import java.sql.DriverManager
import java.util.*

interface Creations {
    fun TaigaTestInstanceManager.initData() = tx {
        if (runCatching { createStatement().execute("select * from users_user_$_snapshotPostfix where false") }.isSuccess) {
            println("Hot init")
            hotInit()
        } else {
            println("Cold init")
            coldInit()
        }

        authActiveUser()
    }

    private fun TaigaTestInstanceManager.coldInit() {
        val sleepTime = 300L

        createUsers()
        Thread.sleep(sleepTime)
        createProjects()
        Thread.sleep(sleepTime)
        createEpics()
        Thread.sleep(sleepTime)
        createSprints()
        Thread.sleep(sleepTime)
        createUserStories()
        Thread.sleep(sleepTime)
        createIssues()
        Thread.sleep(sleepTime)

        tx {
            getAllProdTables().forEach {
                println("Creating snapshot for '$it'")
                createStatement().execute("select * into ${it}_$_snapshotPostfix from $it")
            }
        }
    }

    private fun TaigaTestInstanceManager.hotInit() = tx {
        getAllProdTables().forEach {
            println("Getting snapshot for '$it'")
            createStatement().execute("alter table $it disable trigger all")
            createStatement().execute("delete from $it where true")
            createStatement().execute("insert into $it select * from ${it}_$_snapshotPostfix")
            createStatement().execute("alter table $it enable trigger all")
        }
    }

    private fun TaigaTestInstanceManager.authActiveUser() {
        activeUser = TestData.activeUser.let {
            val data = Request.Builder()
                .apiEndpoint("auth")
                .post(
                    """
                        {
                            "password": "${it.password}",
                            "username": "${it.username}",
                            "type": "normal"
                        }
                    """.trimIndent().toJsonBody()
                )
                .build()
                .execute()
                .toJsonObject()
                .let {
                    UserData(
                        id = it.get("id").asLong,
                        accessToken = it.get("auth_token").asString,
                        refreshToken = it.get("refresh").asString
                    )
                }

            val projects = Request.Builder()
                .apiEndpoint("projects?member=${data.id}")
                .get()
                .header("Authorization", "Bearer ${data.accessToken}")
                .build()
                .execute()
                .toJsonArray()
                .map { it.asJsonObject }
                .map { it.get("id").asLong to it.get("name").asString }
                .map { (id, name) -> id to TestData.projects.find { it.name == name }!! }
                .toMap()

            UserInfo(it, data, projects)
        }
    }

    private fun TaigaTestInstanceManager.watchTask(taskType: CommonTaskType, taskId: Long, user: User) {
        Request.Builder()
            .apiEndpoint("${CommonTaskPathPlural(taskType).path}/$taskId/watch")
            .post("".toJsonBody())
            .withAuth(user)
            .build()
            .execute()
            .successOrThrow()
    }

    private fun TaigaTestInstanceManager.postComment(
        taskType: CommonTaskType,
        taskId: Long,
        comment: Comment,
        version: Int = 1
    ) {
        Request.Builder()
            .apiEndpoint("${CommonTaskPathPlural(taskType).path}/$taskId")
            .patch(
                """
                   {
                       "comment": "${comment.text}",
                       "version": $version
                   }
                """.trimIndent().toJsonBody()
            )
            .withAuth(comment.author)
            .build()
            .execute()
            .successOrThrow()
    }

    private fun TaigaTestInstanceManager.createTasks(
        tasks: List<Task>,
        projectId: Long,
        sprintId: Long? = null,
        userStoryId: Long? = null,
    ) {
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
                            "assigned_to": ${_userToUserData[it.assignedTo]?.id}
                            ${
                                if (it.isClosed)
                                    """, "status": ${getClosedStatusId(CommonTaskType.Task, projectId, it.creator)}"""
                                else
                                    ""
                            }
                        }
                    """.trimIndent().toJsonBody()
                )
                .withAuth(it.creator)
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

            it.watchers.forEach {
                watchTask(CommonTaskType.Task, taskId, it)
            }
        }
    }

    private fun TaigaTestInstanceManager.createUsers() {
        _userToUserData = TestData.users.associateWith {
            Request.Builder()
                .apiEndpoint("auth/register")
                .post(
                    """
                        {
                            "accepted_terms": true,
                            "email": "${it.email}",
                            "full_name": "${it.fullName}",
                            "password": "${it.password}",
                            "type": "public",
                            "username": "${it.username}"
                        }
                    """.trimIndent().toJsonBody()
                )
                .build()
                .execute()
                .toJsonObject()
                .let {
                    UserData(
                        id = it.get("id").asLong,
                        accessToken = it.get("auth_token").asString,
                        refreshToken = it.get("refresh").asString
                    )
                }
        }

        // some dirty tricks, because email has to be verified
        println("Manually verifying emails")

        tx {
            createStatement().execute("update users_user set verified_email = true where true")
        }
    }

    private fun TaigaTestInstanceManager.createProjects() {
        _projectToProjectData = TestData.projects.associateWith {
            val projectId = Request.Builder()
                .apiEndpoint("projects")
                .post(
                    """
                        {
                            "name": "${it.name}",
                            "description": "${it.description}",
                            "is_private": false,
                            "is_issues_activated": true,
                            "is_backlog_activated": true,
                            "is_wiki_activated": true
                        }
                    """.trimIndent().toJsonBody()
                )
                .withAuth(it.creator)
                .build()
                .execute()
                .toJsonObject()
                .get("id")
                .asLong

            val roleId = Request.Builder()
                .apiEndpoint("roles")
                .post(
                    """
                        {
                            "project": $projectId,
                            "name": "${it.defaultRoleName}",
                            "permissions": [
                                "add_issue",
                                "delete_us",
                                "modify_wiki_link",
                                "delete_epic",
                                "view_issues",
                                "add_wiki_page",
                                "comment_issue",
                                "modify_epic",
                                "delete_issue",
                                "delete_wiki_link",
                                "delete_task",
                                "view_wiki_pages",
                                "modify_wiki_page",
                                "delete_wiki_page",
                                "delete_milestone",
                                "comment_task",
                                "comment_wiki_page",
                                "view_project",
                                "add_task",
                                "view_wiki_links",
                                "view_tasks",
                                "add_us",
                                "add_milestone",
                                "modify_us",
                                "modify_milestone",
                                "comment_epic",
                                "modify_issue",
                                "view_milestones",
                                "view_epics",
                                "view_us",
                                "comment_us",
                                "modify_task",
                                "add_epic",
                                "add_wiki_link"
                            ]
                        }
                    """.trimIndent().toJsonBody()
                )
                .withAuth(it.creator)
                .build()
                .execute()
                .toJsonObject()
                .get("id")
                .asLong

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
                .withAuth(it.creator)
                .build()
                .execute()
                .successOrThrow()

            it.team.filter { user -> user != it.creator }.forEach { user ->
                Request.Builder()
                    .apiEndpoint("memberships")
                    .post(
                        """
                            {
                                "project": $projectId,
                                "role": $roleId,
                                "username": "${user.email}"
                            }
                        """.trimIndent().toJsonBody()
                        // ^^^ api logic 100% ¯\_(ツ)_/¯
                    )
                    .withAuth(it.creator)
                    .build()
                    .execute()
                    .successOrThrow()
            }

            ProjectData(projectId)
        }

    }

    private fun TaigaTestInstanceManager.createSprints() {
        _projectToProjectData.forEach { (project, data) ->
            data.sprintToId = project.sprints.associateWith {
                val sprintId = Request.Builder()
                    .apiEndpoint("milestones")
                    .post(
                        """
                           {
                               "project": ${data.id},
                               "name": "${it.name}",
                               "estimated_start": "${it.start}",
                               "estimated_finish": "${it.end}"
                           }
                        """.trimIndent().toJsonBody()
                    )
                    .withAuth(it.creator)
                    .build()
                    .execute()
                    .toJsonObject()
                    .get("id")
                    .asLong

                createTasks(it.tasks, data.id, sprintId = sprintId)

                sprintId
            }
        }
    }

    private fun TaigaTestInstanceManager.createEpics() {
        _projectToProjectData.forEach { (project, data) ->
            data.epicToId = project.epics.associateWith {
                val epicId = Request.Builder()
                    .apiEndpoint("epics")
                    .post(
                        """
                            {
                                "project": ${data.id},
                                "subject": "${it.name}",
                                "description": "${it.description}",
                                "assigned_to": ${_userToUserData[it.assignedTo]?.id}
                                ${
                                    if (it.isClosed)
                                        """, "status": ${getClosedStatusId(CommonTaskType.Epic, data.id, it.creator)}"""
                                    else
                                        ""
                                }
                            }
                        """.trimIndent().toJsonBody()
                    )
                    .withAuth(it.creator)
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
                it.watchers.forEach {
                    watchTask(CommonTaskType.Epic, epicId, it)
                }

                epicId
            }
        }
    }

    private fun TaigaTestInstanceManager.createUserStories() {
        _projectToProjectData.forEach { (project, data) ->
            data.userstoryToId = project.userstories.associateWith {
                val userStoryId = Request.Builder()
                    .apiEndpoint("userstories")
                    .post(
                        """
                            {
                                "project": ${data.id},
                                "subject": "${it.name}",
                                "description": "${it.description}",
                                "assigned_to": ${_userToUserData[it.assignedTo]?.id},
                                "milestone": ${data.sprintToId[it.sprint]}
                                ${
                                    if (it.isClosed)
                                        """, "status": ${getClosedStatusId(CommonTaskType.UserStory, data.id, it.creator)}"""
                                    else
                                        ""
                                }
                            }
                        """.trimIndent().toJsonBody()
                    )
                    .withAuth(it.creator)
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
                        .apiEndpoint("epics/${data.epicToId[it]}/related_userstories")
                        .post(
                            """
                                {
                                    "epic": ${data.epicToId[it]},
                                    "user_story": $userStoryId
                                }
                            """.trimIndent().toJsonBody()
                        )
                        .withAuth(project.creator)
                        .build()
                        .execute()
                        .successOrThrow()
                }

                it.watchers.forEach {
                    watchTask(CommonTaskType.UserStory, userStoryId, it)
                }

                createTasks(it.tasks, data.id, userStoryId = userStoryId)

                userStoryId
            }
        }
    }

    private fun TaigaTestInstanceManager.createIssues() {
        _projectToProjectData.forEach { (project, data) ->
            data.issueToId = project.issues.associateWith {
                val issueId = Request.Builder()
                    .apiEndpoint("issues")
                    .post(
                        """
                            {
                                "project": ${data.id},
                                "subject": "${it.name}",
                                "description": "${it.description}",
                                "assigned_to": ${_userToUserData[it.assignedTo]?.id},
                                "milestone": ${data.sprintToId[it.sprint]}
                                ${
                                    if (it.isClosed)
                                        """, "status": ${getClosedStatusId(CommonTaskType.UserStory, data.id, it.creator)}"""
                                    else
                                        ""
                                }
                            }
                        """.trimIndent().toJsonBody()
                    )
                    .withAuth(it.creator)
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

                it.watchers.forEach {
                    watchTask(CommonTaskType.Issue, issueId, it)
                }

                issueId
            }
        }
    }
}
