package io.eugenethedev.taigamobile.manager

import io.eugenethedev.taigamobile.testdata.*

class UserData(
    val id: Long,
    val accessToken: String,
    val refreshToken: String
)

class UserInfo(
    val user: User,
    val data: UserData,
    val projects: Map<Long, Project>
)

class ProjectData(
    val id: Long
) {
    lateinit var epicToId: Map<Epic, Long>
    lateinit var sprintToId: Map<Sprint, Long>
    lateinit var userstoryToId: Map<UserStory, Long>
    lateinit var issueToId: Map<Issue, Long>
}
