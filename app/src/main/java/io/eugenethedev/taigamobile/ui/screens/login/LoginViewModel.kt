package io.eugenethedev.taigamobile.ui.screens.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.data.repositories.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel : ViewModel() {

    @Inject lateinit var authRepository: AuthRepository

    var isError: MutableLiveData<Boolean> = MutableLiveData(false)
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun onContinueClick(taigaServer: String, username: String, password: String) = viewModelScope.launch {
        isLoading.value = true
        try {
            authRepository.auth(taigaServer, password, username)
        } catch (e: Exception) {
            isError.value = true
        } finally {
            isLoading.value = false
        }
    }
}