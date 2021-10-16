package io.eugenethedev.taigamobile

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

class Session(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _refreshToken = MutableStateFlow(sharedPreferences.getString(REFRESH_TOKEN_KEY, "").orEmpty())
    private val _token = MutableStateFlow(sharedPreferences.getString(TOKEN_KEY, "").orEmpty())

    val refreshToken: StateFlow<String> = _refreshToken
    val token: StateFlow<String> = _token

    fun changeAuthCredentials(token: String, refreshToken: String) {
        sharedPreferences.edit {
            putString(TOKEN_KEY, token)
            putString(REFRESH_TOKEN_KEY, refreshToken)
        }
        _token.value = token
        _refreshToken.value = refreshToken
    }

    private val _server = MutableStateFlow(sharedPreferences.getString(SERVER_KEY, "").orEmpty())
    val server: StateFlow<String> = _server
    fun changeServer(value: String) {
        sharedPreferences.edit { putString(SERVER_KEY, value) }
        _server.value = value
    }

    private val _currentUserId = MutableStateFlow(sharedPreferences.getLong(USER_ID_KEY, -1))
    val currentUserId: StateFlow<Long> = _currentUserId
    fun changeCurrentUserId(value: Long) {
        sharedPreferences.edit { putLong(USER_ID_KEY, value) }
        _currentUserId.value = value
    }

    private val _currentProjectId = MutableStateFlow(sharedPreferences.getLong(PROJECT_ID_KEY, -1))
    private val _currentProjectName = MutableStateFlow(sharedPreferences.getString(PROJECT_NAME_KEY, "").orEmpty())

    val currentProjectId: StateFlow<Long> = _currentProjectId
    val currentProjectName: StateFlow<String> = _currentProjectName

    fun changeCurrentProject(id: Long, name: String) {
        sharedPreferences.edit {
            putLong(PROJECT_ID_KEY, id)
            putString(PROJECT_NAME_KEY, name)
        }
        _currentProjectId.value = id
        _currentProjectName.value = name
    }

    private fun checkLogged(token: String, refresh: String) = listOf(token, refresh).all { it.isNotEmpty() }
    val isLogged = combine(token, refreshToken, ::checkLogged)
        .stateIn(scope, SharingStarted.Eagerly, initialValue = checkLogged(token.value, refreshToken.value))

    private fun checkProjectSelected(id: Long) = id >= 0
    val isProjectSelected = currentProjectId.map(::checkProjectSelected)
        .stateIn(scope, SharingStarted.Eagerly, initialValue = checkProjectSelected(currentProjectId.value))

    fun reset() {
        changeAuthCredentials("", "")
        changeServer("")
        changeCurrentUserId(-1)
        changeCurrentProject(-1, "")
    }

    companion object {
        private const val PREFERENCES_NAME = "session"
        private const val TOKEN_KEY = "token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val SERVER_KEY = "server"
        private const val PROJECT_NAME_KEY = "project_name"
        private const val PROJECT_ID_KEY = "project_id"
        private const val USER_ID_KEY = "user_id"
    }
}
