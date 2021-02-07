package io.eugenethedev.taigamobile

import android.content.Context
import androidx.core.content.edit

class Session(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var token: String
        get() = sharedPreferences.getString(TOKEN_KEY, "") ?: ""
        set(value) {
            sharedPreferences.edit {
                putString(TOKEN_KEY, value)
            }
        }

    var server: String
        get() = sharedPreferences.getString(SERVER_KEY, "") ?: ""
        set(value) {
            sharedPreferences.edit {
                putString(SERVER_KEY, value)
            }
        }

    companion object {
        private const val PREFERENCES_NAME = "session"
        private const val TOKEN_KEY = "token"
        private const val SERVER_KEY = "server"
    }
}