package io.eugenethedev.taigamobile

import android.content.Context
import androidx.core.content.edit

class Settings(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var isScrumScreenExpandStatuses: Boolean
        get() = sharedPreferences.getBoolean(SCRUM_EXPAND_STATUSES, false)
        set(value) {
            sharedPreferences.edit {
                putBoolean(SCRUM_EXPAND_STATUSES, value)
            }
        }

    var isSprintScreenExpandStatuses: Boolean
        get() = sharedPreferences.getBoolean(SPRINT_EXPAND_STATUSES, false)
        set(value) {
            sharedPreferences.edit {
                putBoolean(SPRINT_EXPAND_STATUSES, value)
            }
        }

    companion object {
        private const val PREFERENCES_NAME = "settings"
        private const val SCRUM_EXPAND_STATUSES = "scrum_expand_statuses"
        private const val SPRINT_EXPAND_STATUSES = "sprint_expand_statuses"

    }
}