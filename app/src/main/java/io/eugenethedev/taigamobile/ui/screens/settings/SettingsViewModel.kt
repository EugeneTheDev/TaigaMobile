package io.eugenethedev.taigamobile.ui.screens.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.Settings
import io.eugenethedev.taigamobile.TaigaApp
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

    val isScrumScreenExpandStatuses: MutableLiveData<Boolean>
    val isSprintScreenExpandStatuses: MutableLiveData<Boolean>

    init {
        TaigaApp.appComponent.inject(this)

        isScrumScreenExpandStatuses = MutableLiveData(settings.isScrumScreenExpandStatuses)
        isSprintScreenExpandStatuses = MutableLiveData(settings.isSprintScreenExpandStatuses)
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

    fun switchScrumScreenExpandStatuses(checked: Boolean) = viewModelScope.launch {
        settings.let {
            it.isScrumScreenExpandStatuses = checked
            isScrumScreenExpandStatuses.value = it.isScrumScreenExpandStatuses
        }
    }

    fun switchSprintScreenExpandStatuses(checked: Boolean) = viewModelScope.launch {
        settings.let {
            it.isSprintScreenExpandStatuses = checked
            isSprintScreenExpandStatuses.value = it.isSprintScreenExpandStatuses
        }
    }

}