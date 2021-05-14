package io.eugenethedev.taigamobile

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow

class Settings(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    val themeSetting =  MutableStateFlow(ThemeSetting.values()[sharedPreferences.getInt(THEME, 0)])
    fun changeThemeSetting(value: ThemeSetting) {
        sharedPreferences.edit {
            putInt(THEME, value.ordinal)
        }
        themeSetting.value = value
    }

    companion object {
        private const val PREFERENCES_NAME = "settings"
        private const val THEME = "theme"
    }
}

enum class ThemeSetting {
    System,
    Light,
    Dark
}
