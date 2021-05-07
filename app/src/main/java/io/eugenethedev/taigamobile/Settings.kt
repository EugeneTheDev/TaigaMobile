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

    // receive update about theme change to recompose immediately
    var themeSettingCallback: ((ThemeSetting) -> Unit)? = null
        set(value) {
            if (field != null) {
                throw IllegalStateException("Already set!")
            }
            field = value
        }
    var themeSetting: ThemeSetting
        get() = ThemeSetting.values()[sharedPreferences.getInt(THEME, 0)]
        set(value) {
            sharedPreferences.edit {
                putInt(THEME, value.ordinal)
            }
            themeSettingCallback?.invoke(value)
        }

    companion object {
        private const val PREFERENCES_NAME = "settings"
        private const val SCRUM_EXPAND_STATUSES = "scrum_expand_statuses"
        private const val SPRINT_EXPAND_STATUSES = "sprint_expand_statuses"
        private const val THEME = "theme"
    }
}

enum class ThemeSetting {
    System,
    Light,
    Dark
}