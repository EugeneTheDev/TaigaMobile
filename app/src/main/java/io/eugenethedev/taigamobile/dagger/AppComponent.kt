package io.eugenethedev.taigamobile.dagger

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.eugenethedev.taigamobile.ui.screens.main.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, RepositoriesModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance fun context(context: Context): Builder
        fun build(): AppComponent
    }

    fun inject(mainViewModel: MainViewModel)
}