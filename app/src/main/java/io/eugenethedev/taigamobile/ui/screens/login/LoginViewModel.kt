package io.eugenethedev.taigamobile.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.data.repositories.AuthRepository
import io.eugenethedev.taigamobile.ui.commons.MutableLiveResult
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel : ViewModel() {

    @Inject lateinit var authRepository: AuthRepository

    val loginResult = MutableLiveResult<Unit>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun login(taigaServer: String, username: String, password: String) = viewModelScope.launch {
        loginResult.value = Result(ResultStatus.Loading)
        loginResult.value = try {
            authRepository.auth(taigaServer, password, username)
             Result(ResultStatus.Success)
        } catch (e: Exception) {
            Timber.w(e)
            Result(ResultStatus.Error, message = R.string.login_error_message)
        }
    }
}