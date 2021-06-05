package io.eugenethedev.taigamobile

import android.app.Application
import android.util.Log
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.dagger.DaggerAppComponent
import io.eugenethedev.taigamobile.utils.FileLoggingTree
import timber.log.Timber

class TaigaApp : Application() {

    // logging
    private var fileLoggingTree: FileLoggingTree? = null
    val currentLogFile get() = fileLoggingTree?.currentFile


    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()

        // logging configs
        val minLoggingPriority = if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Log.DEBUG
        } else {
            Log.WARN
        }

        try {
            fileLoggingTree = FileLoggingTree(applicationContext.getExternalFilesDir("logs")!!.absolutePath, minLoggingPriority)
            Timber.plant(fileLoggingTree!!)
        } catch (e: NullPointerException) {
            Timber.w("Cannot setup FileLoggingTree, skipping")
        }

    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}