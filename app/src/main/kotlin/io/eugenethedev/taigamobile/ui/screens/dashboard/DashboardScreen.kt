package io.eugenethedev.taigamobile.ui.screens.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.ui.utils.LoadingResult
import io.eugenethedev.taigamobile.ui.components.containers.HorizontalTabbedPager
import io.eugenethedev.taigamobile.ui.components.containers.Tab
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.lists.ProjectCard
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.theme.*
import io.eugenethedev.taigamobile.ui.utils.navigateToTaskScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@Composable
fun DashboardScreen(
    navController: NavController,
    showMessage: (message: Int) -> Unit = {},
) {
    val viewModel: DashboardViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val workingOn by viewModel.workingOn.collectAsState()
    workingOn.subscribeOnError(showMessage)

    val watching by viewModel.watching.collectAsState()
    watching.subscribeOnError(showMessage)

    val myProjects by viewModel.myProjects.collectAsState()
    myProjects.subscribeOnError(showMessage)

    val currentProjectId by viewModel.currentProjectId.collectAsState()

    DashboardScreenContent(
        isLoading = listOf(workingOn, watching, myProjects).any { it is LoadingResult<*> },
        workingOn = workingOn.data.orEmpty(),
        watching = watching.data.orEmpty(),
        myProjects = myProjects.data.orEmpty(),
        currentProjectId = currentProjectId,
        navigateToTask = {
            viewModel.changeCurrentProject(it.projectInfo)
            navController.navigateToTaskScreen(it.id, it.taskType, it.ref)
        },
        changeCurrentProject = viewModel::changeCurrentProject
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DashboardScreenContent(
    isLoading: Boolean = false,
    workingOn: List<CommonTask> = emptyList(),
    watching: List<CommonTask> = emptyList(),
    myProjects: List<Project> = emptyList(),
    currentProjectId: Long = 0,
    navigateToTask: (CommonTask) -> Unit = { _ -> },
    changeCurrentProject: (Project) -> Unit = { _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    AppBarWithBackButton(title = { Text(stringResource(R.string.dashboard)) })

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularLoader()
        }
    } else {
        HorizontalTabbedPager(
            tabs = Tabs.values(),
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (Tabs.values()[page]) {
                Tabs.WorkingOn -> TabContent(
                    commonTasks = workingOn,
                    navigateToTask = navigateToTask
                )
                Tabs.Watching -> TabContent(
                    commonTasks = watching,
                    navigateToTask = navigateToTask
                )
                Tabs.MyProjects -> MyProjects(
                    myProjects = myProjects,
                    currentProjectId = currentProjectId,
                    changeCurrentProject = changeCurrentProject
                )
            }
        }
    }

}

private enum class Tabs(@StringRes override val titleId: Int) : Tab {
    WorkingOn(R.string.working_on),
    Watching(R.string.watching),
    MyProjects(R.string.my_projects)
}

@Composable
private fun TabContent(
    commonTasks: List<CommonTask>,
    navigateToTask: (CommonTask) -> Unit,
) = LazyColumn(Modifier.fillMaxSize()) {
    SimpleTasksListWithTitle(
        bottomPadding = commonVerticalPadding,
        horizontalPadding = mainHorizontalScreenPadding,
        showExtendedTaskInfo = true,
        commonTasks = commonTasks,
        navigateToTask = { id, _, _ -> navigateToTask(commonTasks.find { it.id == id }!!) },
    )
}

@Composable
private fun MyProjects(
    myProjects: List<Project>,
    currentProjectId: Long,
    changeCurrentProject: (Project) -> Unit
) = LazyColumn {
    items(myProjects) {
        ProjectCard(
            project = it,
            isCurrent = it.id == currentProjectId,
            onClick = { changeCurrentProject(it) }
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ProjectCardPreview() = TaigaMobileTheme {
    ProjectCard(
        project = Project(
            id = 0,
            name = "Name",
            slug = "slug",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            isPrivate = true
        ),
        isCurrent = true,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() = TaigaMobileTheme {
    DashboardScreenContent(
        myProjects = List(3) {
            Project(
                id = it.toLong(),
                name = "Name",
                slug = "slug",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                isPrivate = true
            )
        }
    )
}
