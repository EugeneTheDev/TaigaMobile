package io.eugenethedev.taigamobile.dagger

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.eugenethedev.taigamobile.ui.screens.login.LoginViewModel
import io.eugenethedev.taigamobile.ui.screens.main.MainViewModel
import io.eugenethedev.taigamobile.ui.screens.projectselector.ProjectSelectorViewModel
import io.eugenethedev.taigamobile.ui.screens.scrum.ScrumViewModel
import io.eugenethedev.taigamobile.ui.screens.sprint.SprintViewModel
import io.eugenethedev.taigamobile.ui.screens.commontask.CommonTaskViewModel
import io.eugenethedev.taigamobile.ui.screens.createtask.CreateTaskViewModel
import io.eugenethedev.taigamobile.ui.screens.dashboard.DashboardViewModel
import io.eugenethedev.taigamobile.ui.screens.epics.EpicsViewModel
import io.eugenethedev.taigamobile.ui.screens.issues.IssuesViewModel
import io.eugenethedev.taigamobile.ui.screens.kanban.KanbanViewModel
import io.eugenethedev.taigamobile.ui.screens.profile.ProfileViewModel
import io.eugenethedev.taigamobile.ui.screens.settings.SettingsViewModel
import io.eugenethedev.taigamobile.ui.screens.team.TeamViewModel
import io.eugenethedev.taigamobile.ui.screens.wiki.WikiViewModel
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
    fun inject(dashboardViewModel: DashboardViewModel)
    fun inject(scrumViewModel: ScrumViewModel)
    fun inject(epicsViewModel: EpicsViewModel)
    fun inject(projectSelectorViewModel: ProjectSelectorViewModel)
    fun inject(sprintViewModel: SprintViewModel)
    fun inject(commonTaskViewModel: CommonTaskViewModel)
    fun inject(teamViewModel: TeamViewModel)
    fun inject(settingsViewModel: SettingsViewModel)
    fun inject(createTaskViewModel: CreateTaskViewModel)
    fun inject(issuesViewModel: IssuesViewModel)
    fun inject(kanbanViewModel: KanbanViewModel)
    fun inject(profileViewModel: ProfileViewModel)
    fun inject(wikiViewModel: WikiViewModel)
}