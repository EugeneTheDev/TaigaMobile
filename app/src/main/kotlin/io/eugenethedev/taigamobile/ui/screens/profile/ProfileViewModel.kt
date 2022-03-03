package io.eugenethedev.taigamobile.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.domain.entities.Stats
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.repositories.IProjectsRepository
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.ui.utils.MutableResultFlow
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {
    @Inject
    lateinit var usersRepository: IUsersRepository
    @Inject
    lateinit var projectsRepository: IProjectsRepository
    @Inject
    lateinit var session: Session

    val currentUser = MutableResultFlow<User>()
    val currentUserStats = MutableResultFlow<Stats>()
    val currentUserProjects = MutableResultFlow<List<Project>>()

    init {
        appComponent.inject(this)
    }

    fun getUser(userId: Long) = viewModelScope.launch {
        currentUser.loadOrError {
            usersRepository.getUser(userId)
        }
    }

    fun getCurrentUserStats(userId: Long) = viewModelScope.launch {
        currentUserStats.loadOrError {
            usersRepository.getUserStats(userId)
        }
    }

    fun getCurrentUserProjects(userId: Long) = viewModelScope.launch {
        currentUserProjects.loadOrError {
            projectsRepository.getUserProjects(userId)
        }
    }
}