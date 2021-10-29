package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.data.repositories.TasksRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import kotlin.test.BeforeTest
import kotlin.test.assertEquals


class TasksRepositoryTest : BaseRepositoryTest() {
    lateinit var tasksRepository: ITasksRepository

    @BeforeTest
    fun setupSprintsRepositoryTest() {
        tasksRepository = TasksRepository(mockTaigaApi, mockSession)
    }
}