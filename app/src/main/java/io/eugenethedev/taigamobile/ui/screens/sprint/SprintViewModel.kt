package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.ui.commons.StoriesViewModel
import kotlinx.coroutines.launch

class SprintViewModel : StoriesViewModel() {

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start(sprintId: Long) {
        this.sprintId = sprintId

        if (
            statuses.value == null &&
            stories.value == null
        ) {
            viewModelScope.launch { loadStatuses() }
        }
    }
}