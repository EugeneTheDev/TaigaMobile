package io.eugenethedev.taigamobile.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.utils.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.Result
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel : ViewModel() {

    @Inject lateinit var session: Session
    @Inject lateinit var userRepository: IUsersRepository

    val user = MutableLiveResult<User>()
    val serverUrl get() = session.server

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() = viewModelScope.launch {
        user.value = Result(ResultStatus.LOADING)

        user.value = try {
            Result(ResultStatus.SUCCESS, userRepository.getMe())
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }
    }

    fun logout() {
        session.reset()
    }

}