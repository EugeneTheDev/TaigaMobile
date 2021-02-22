package io.eugenethedev.taigamobile.dagger

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.eugenethedev.taigamobile.ui.screens.login.LoginViewModel
import io.eugenethedev.taigamobile.ui.screens.main.MainViewModel
import io.eugenethedev.taigamobile.ui.screens.projectselector.ProjectSelectorViewModel
import io.eugenethedev.taigamobile.ui.screens.scrum.ScrumViewModel
import io.eugenethedev.taigamobile.ui.screens.sprint.SprintViewModel
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
    fun inject(loginViewModel: LoginViewModel)
    fun inject(scrumViewModel: ScrumViewModel)
    fun inject(projectSelectorViewModel: ProjectSelectorViewModel)
    fun inject(sprintViewModel: SprintViewModel)
}