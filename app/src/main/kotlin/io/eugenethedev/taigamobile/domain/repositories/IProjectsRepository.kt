package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.Project

interface IProjectsRepository {
    suspend fun searchProjects(query: String, page: Int): List<Project>
}