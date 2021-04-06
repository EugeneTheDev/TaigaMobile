package io.eugenethedev.taigamobile

import android.content.Context
import androidx.core.content.edit

class Session(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var token: String
        get() = sharedPreferences.getString(TOKEN_KEY, "").orEmpty()
        set(value) {
            sharedPreferences.edit {
                putString(TOKEN_KEY, value)
            }
        }

    var server: String
        get() = sharedPreferences.getString(SERVER_KEY, "").orEmpty()
        set(value) {
            sharedPreferences.edit {
                putString(SERVER_KEY, value)
            }
        }

    var currentProjectId: Long
        get() = sharedPreferences.getLong(PROJECT_ID_KEY, -1)
        set(value) {
            sharedPreferences.edit {
                putLong(PROJECT_ID_KEY, value)
            }
        }

    var currentProjectName: String
        get() = sharedPreferences.getString(PROJECT_NAME_KEY, "").orEmpty()
        set(value) {
            sharedPreferences.edit {
                putString(PROJECT_NAME_KEY, value)
            }
        }

    var currentUserId: Long
        get() = sharedPreferences.getLong(USER_ID_KEY, -1)
        set(value) {
            sharedPreferences.edit {
                putLong(USER_ID_KEY, value)
            }
        }

    val isLogged: Boolean get() = token.isNotEmpty() && server.isNotEmpty()

    val isProjectSelected get() = currentProjectId >= 0

    fun reset() {
        token = ""
        server = ""
        currentProjectId = -1
        currentProjectName = ""
    }

    companion object {
        private const val PREFERENCES_NAME = "session"
        private const val TOKEN_KEY = "token"
        private const val SERVER_KEY = "server"
        private const val PROJECT_NAME_KEY = "project_name"
        private const val PROJECT_ID_KEY = "project_id"
        private const val USER_ID_KEY = "user_id"
    }
}