package io.eugenethedev.taigamobile.ui.screens.stories

import androidx.lifecycle.ViewModel
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import javax.inject.Inject

class StoriesViewModel : ViewModel() {

    @Inject lateinit var session: Session

    val projectName: String get() = session.currentProjectName

    init {
        TaigaApp.appComponent.inject(this)
    }
}