package io.eugenethedev.taigamobile.domain.repositories

import io.eugenethedev.taigamobile.domain.entities.Stats
import io.eugenethedev.taigamobile.domain.entities.TeamMember
import io.eugenethedev.taigamobile.domain.entities.User

interface IUsersRepository {
    suspend fun getMe(): User
    suspend fun getUser(userId: Long): User
    suspend fun getUserStats(userId: Long): Stats
    suspend fun getTeam(): List<TeamMember>
}