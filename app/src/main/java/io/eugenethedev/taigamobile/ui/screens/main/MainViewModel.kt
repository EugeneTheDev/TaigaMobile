package io.eugenethedev.taigamobile.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.data.repositories.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject lateinit var authRepository: AuthRepository

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun onContinueClick(taigaServer: String, username: String, password: String) = viewModelScope.launch {
        authRepository.auth(taigaServer, password, username)
    }
}