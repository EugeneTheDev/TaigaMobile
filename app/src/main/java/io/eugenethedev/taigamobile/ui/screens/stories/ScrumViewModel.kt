package io.eugenethedev.taigamobile.ui.screens.stories

import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.ui.commons.StoriesViewModel
import kotlinx.coroutines.launch

class ScrumViewModel : StoriesViewModel() {

    val projectName: String get() = session.currentProjectName

    init {
        TaigaApp.appComponent.inject(this)
    }

    override fun start() {
        if (statuses.value == null && stories.value == null) {
            viewModelScope.launch {
                loadStatuses()
            }
        }
    }

}