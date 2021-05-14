package io.eugenethedev.taigamobile.ui.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.*
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var settings: Settings
    @Inject lateinit var userRepository: IUsersRepository

    val user = MutableLiveResult<User>()
    val serverUrl get() = session.server

    val themeSetting: LiveData<ThemeSetting>

    init {
        TaigaApp.appComponent.inject(this)

        themeSetting = settings.themeSetting.asLiveData(viewModelScope.coroutineContext)
    }

    fun start() = viewModelScope.launch {
        user.value = Result(ResultStatus.Loading)

        user.value = try {
            Result(ResultStatus.Success, userRepository.getMe())
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.common_error_message)
        }
    }

    fun logout() {
        session.reset()
    }

    fun switchTheme(theme: ThemeSetting) {
        settings.changeThemeSetting(theme)
    }

}