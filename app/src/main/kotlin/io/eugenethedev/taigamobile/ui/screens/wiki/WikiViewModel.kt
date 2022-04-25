package io.eugenethedev.taigamobile.ui.screens.wiki

import androidx.lifecycle.ViewModel
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.dagger.AppComponent

class WikiViewModel(appComponent: AppComponent = TaigaApp.appComponent) : ViewModel() {

    init {
        appComponent.inject(this)
    }
}