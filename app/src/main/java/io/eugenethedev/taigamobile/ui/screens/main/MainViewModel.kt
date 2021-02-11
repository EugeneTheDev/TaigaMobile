package io.eugenethedev.taigamobile.ui.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject lateinit var session: Session
    val isLogged: LiveData<Boolean> by lazy { MutableLiveData(session.isLogged) }

    init {
        TaigaApp.appComponent.inject(this)
    }
}