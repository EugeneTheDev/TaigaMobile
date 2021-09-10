package io.eugenethedev.taigamobile.ui.commons

import javax.inject.Inject
import javax.inject.Singleton


/**
 * indicates if screen should be reloaded to reload it only if needed.
 * e.g. if task status has been modified, tasks list on screen should be reloaded.
 * but if nothing has changed and user just navigates back from task screen list should not be reloaded.
 * i'm not sure if such approach is ok, but for now let it be like this...
 **/
@Singleton
class ScreensState @Inject constructor() {
    var shouldReloadDashboardScreen: Boolean = false
        private set
        get() {
            val value = field
            field = false
            return value
        }

    var shouldReloadScrumScreen: Boolean = false
        private set
        get() {
            val value = field
            field = false
            return value
        }

    var shouldReloadSprintScreen: Boolean = false
        private set
        get() {
            val value = field
            field = false
            return value
        }

    var shouldReloadEpicsScreen: Boolean = false
        private set
        get() {
            val value = field
            field = false
            return value
        }

    var shouldReloadIssuesScreen: Boolean = false
        private set
        get() {
            val value = field
            field = false
            return value
        }

    var shouldReloadKanbanScreen: Boolean = false
        private set
        get() {
            val value = field
            field = false
            return value
        }


    fun modify() {
        shouldReloadDashboardScreen = true
        shouldReloadScrumScreen = true
        shouldReloadSprintScreen = true
        shouldReloadEpicsScreen = true
        shouldReloadIssuesScreen = true
        shouldReloadKanbanScreen = true
    }
}
