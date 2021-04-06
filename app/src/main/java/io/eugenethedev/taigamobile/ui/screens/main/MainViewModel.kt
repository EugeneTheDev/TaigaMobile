package io.eugenethedev.taigamobile.ui.screens.main

import androidx.lifecycle.ViewModel
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject lateinit var session: Session
    val isLogged get() = session.isLogged
    val isProjectSelected get() = session.isProjectSelected

    init {
        TaigaApp.appComponent.inject(this)
    }
}