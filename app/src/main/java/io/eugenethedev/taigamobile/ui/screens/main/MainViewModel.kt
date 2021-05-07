package io.eugenethedev.taigamobile.ui.screens.main

import androidx.lifecycle.*
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.Settings
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.ThemeSetting
import javax.inject.Inject

class MainViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var settings: Settings

    val isLogged get() = session.isLogged
    val isProjectSelected get() = session.isProjectSelected

    val theme = MutableLiveData<ThemeSetting>()

    init {
        TaigaApp.appComponent.inject(this)

        theme.value = settings.themeSetting
        settings.themeSettingCallback = { theme.value = it }
    }
}