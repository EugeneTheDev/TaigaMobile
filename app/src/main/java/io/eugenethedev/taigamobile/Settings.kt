package io.eugenethedev.taigamobile

import android.content.Context
import androidx.core.content.edit

class Settings(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

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
        private const val THEME = "theme"
    }
}

enum class ThemeSetting {
    System,
    Light,
    Dark
}