package io.eugenethedev.taigamobile.repositories.utils

import io.eugenethedev.taigamobile.testdata.Task
import io.eugenethedev.taigamobile.testdata.TestData
import io.eugenethedev.taigamobile.testdata.User

class TestCommonTask(
    val title: String,
    val assignee: User? = null,
    val isClosed: Boolean
)

fun getTestTasks(projectId: Int): List<Task> {
    val fromSprints = TestData.projects[projectId].sprints.flatMap { it.tasks }
    val fromUserStories = TestData.projects[projectId].userstories.flatMap { it.tasks }

    return fromSprints + fromUserStories
}