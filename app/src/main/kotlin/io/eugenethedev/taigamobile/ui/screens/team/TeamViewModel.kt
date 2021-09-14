package io.eugenethedev.taigamobile.ui.screens.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.TeamMember
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.commons.MutableResultFlow
import io.eugenethedev.taigamobile.ui.commons.NothingResult
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeamViewModel : ViewModel() {

    @Inject lateinit var usersRepository: IUsersRepository
    @Inject lateinit var session: Session

    val projectName: String get() = session.currentProjectName
    val team = MutableResultFlow<List<TeamMember>?>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() = viewModelScope.launch {
        if (team.value is NothingResult) {
            team.loadOrError { usersRepository.getTeam() }
        }
    }

    fun reset() {
        team.value = NothingResult()
    }
}
