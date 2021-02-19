package io.eugenethedev.taigamobile.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.data.repositories.AuthRepository
import io.eugenethedev.taigamobile.ui.utils.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.Result
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel : ViewModel() {

    @Inject lateinit var authRepository: AuthRepository

    val loginResult = MutableLiveResult<Unit>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun onContinueClick(taigaServer: String, username: String, password: String) = viewModelScope.launch {
        loginResult.value = Result(ResultStatus.LOADING)
        try {
            authRepository.auth(taigaServer, password, username)
            loginResult.value = Result(ResultStatus.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            loginResult.value = Result(ResultStatus.ERROR, message = R.string.login_error_message)
        }
    }
}