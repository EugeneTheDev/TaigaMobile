package io.eugenethedev.taigamobile.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.AuthType
import io.eugenethedev.taigamobile.domain.repositories.IAuthRepository
import io.eugenethedev.taigamobile.ui.commons.MutableResultFlow
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel : ViewModel() {
    @Inject lateinit var authRepository: IAuthRepository

    val loginResult = MutableResultFlow<Unit>()

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun login(taigaServer: String, authType: AuthType, username: String, password: String) = viewModelScope.launch {
        loginResult.loadOrError(R.string.login_error_message) {
            authRepository.auth(taigaServer, authType, password, username)
        }
    }
}
