package io.eugenethedev.taigamobile.state

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Settings(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _themeSetting =  MutableStateFlow(ThemeSetting.values()[sharedPreferences.getInt(THEME, 0)])
    val themeSetting: StateFlow<ThemeSetting> = _themeSetting
    fun changeThemeSetting(value: ThemeSetting) {
        sharedPreferences.edit { putInt(THEME, value.ordinal) }
        _themeSetting.value = value
    }

    private val _scrumFilters = MutableStateFlow(sharedPreferences.getStringSet(SCRUM_FILTERS, null))
    val scrumFilters: StateFlow<Set<String>?> = _scrumFilters
    fun changeScrumFilters(value: Set<String>) {
        sharedPreferences.edit { putStringSet(SCRUM_FILTERS, value) }
        _scrumFilters.value = value
    }


    companion object {
        private const val PREFERENCES_NAME = "settings"
        private const val THEME = "theme"
        private const val SCRUM_FILTERS = "scrum_filters"
    }
}

enum class ThemeSetting {
    System,
    Light,
    Dark
}
