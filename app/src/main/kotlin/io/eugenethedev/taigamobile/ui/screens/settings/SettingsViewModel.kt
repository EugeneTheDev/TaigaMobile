package io.eugenethedev.taigamobile.ui.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.*
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var settings: Settings
    @Inject lateinit var userRepository: IUsersRepository
    @Inject lateinit var screensState: ScreensState

    val user = MutableLiveResult<User>()
    val serverUrl get() = session.server

    val themeSetting: LiveData<ThemeSetting>

    init {
        TaigaApp.appComponent.inject(this)

        themeSetting = settings.themeSetting.asLiveData(viewModelScope.coroutineContext)
    }

    fun start() = viewModelScope.launch {
        user.loadOrError(preserveValue = false) { userRepository.getMe() }
    }

    fun logout() {
        screensState.modify()
        session.reset()
    }

    fun switchTheme(theme: ThemeSetting) {
        settings.changeThemeSetting(theme)
    }

}