package io.eugenethedev.taigamobile

import android.app.Application
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.dagger.DaggerAppComponent

class TaigaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}