package io.eugenethedev.taigamobile.data.repositories

import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.data.api.TaigaApi
import io.eugenethedev.taigamobile.domain.entities.TeamMember
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import kotlinx.coroutines.async
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val taigaApi: TaigaApi,
    private val session: Session
) : IUsersRepository {
    private val currentProjectId get() = session.currentProjectId.value

    override suspend fun getMe() = withIO { taigaApi.getMyProfile() }

    override suspend fun getUser(userId: Long) = withIO { taigaApi.getUser(userId) }

    override suspend fun getTeam() = withIO {
        val team = async { taigaApi.getProject(currentProjectId).members }
        val stats = async {
            taigaApi.getMemberStats(currentProjectId).run {
                // calculating total number of points for each id
                (closed_bugs.toList() + closed_tasks.toList() + created_bugs.toList() +
                    iocaine_tasks.toList() + wiki_changes.toList())
                    .mapNotNull { p -> p.first.toLongOrNull()?.let { it to p.second } }
                    .groupBy { it.first }
                    .map { (k, v) -> k to v.sumOf { it.second } }
                    .toMap()
            }
        }

        stats.await().let { stat ->
            team.await().map {
                TeamMember(
                    id = it.id,
                    avatarUrl = it.photo,
                    name = it.full_name_display,
                    role = it.role_name,
                    username = it.username,
                    totalPower = stat[it.id] ?: 0
                )
            }
        }
    }
}