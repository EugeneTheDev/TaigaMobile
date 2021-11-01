package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.testdata.Task
import io.eugenethedev.taigamobile.testdata.TestData
import io.eugenethedev.taigamobile.testdata.User

data class TestCommonTask(
    val title: String,
    val assignee: User? = null,
    val isClosed: Boolean
)

fun getTestTasks() : List<Task> {
    val fromSprints = TestData.projects[0].sprints.flatMap { it.tasks }
    val fromUserStories = TestData.projects[0].userstories.flatMap { it.tasks }

    return fromSprints + fromUserStories
}