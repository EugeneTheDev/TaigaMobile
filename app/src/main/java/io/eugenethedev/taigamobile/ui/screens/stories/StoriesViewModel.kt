package io.eugenethedev.taigamobile.ui.screens.stories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import javax.inject.Inject

class StoriesViewModel : ViewModel() {

    @Inject lateinit var session: Session

    var projectName = MutableLiveData("")

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun loadData() {
        projectName.value = session.currentProjectName
    }
}