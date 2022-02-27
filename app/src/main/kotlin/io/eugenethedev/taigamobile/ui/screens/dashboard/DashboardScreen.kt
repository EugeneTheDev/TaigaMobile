package io.eugenethedev.taigamobile.ui.screens.dashboard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.ui.utils.LoadingResult
import io.eugenethedev.taigamobile.ui.components.containers.HorizontalTabbedPager
import io.eugenethedev.taigamobile.ui.components.containers.Tab
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.theme.*
import io.eugenethedev.taigamobile.ui.utils.navigateToTaskScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@Composable
fun DashboardScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: DashboardViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val workingOn by viewModel.workingOn.collectAsState()
    workingOn.subscribeOnError(onError)

    val watching by viewModel.watching.collectAsState()
    watching.subscribeOnError(onError)

    val myProjects by viewModel.myProjects.collectAsState()
    myProjects.subscribeOnError(onError)

    val currentProjectId by viewModel.currentProjectId.collectAsState()

    DashboardScreenContent(
        isLoading = listOf(workingOn, watching).any { it is LoadingResult<*> },
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
        navigateToTask = { id, _, _ -> navigateToTask(commonTasks.find { it.id == id }!!)  },
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

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun ProjectCard(
    project: Project,
    isCurrent: Boolean,
    onClick: () -> Unit
) = Surface(
    shape = shapes.medium,
    border = if (isCurrent) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    },
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = mainHorizontalScreenPadding, vertical = 4.dp)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(
                    data = project.avatarUrl ?: R.drawable.default_avatar,
                    builder = {
                        error(R.drawable.default_avatar)
                        crossfade(true)
                    },
                ),
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )

            Spacer(Modifier.width(8.dp))

            Column {
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    text = stringResource(
                        when {
                            project.isOwner -> R.string.project_owner
                            project.isAdmin -> R.string.project_admin
                            project.isMember -> R.string.project_member
                            else -> 0
                        }
                    )
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = project.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        project.description?.let {
            Spacer(Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(8.dp))

        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.outline
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconSize = 18.dp
                val indicatorsSpacing = 8.dp

                @Composable
                fun Indicator(@DrawableRes icon: Int, value: Int) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Indicator(R.drawable.ic_favorite, project.fansCount)
                Spacer(Modifier.width(indicatorsSpacing))
                Indicator(R.drawable.ic_watch, project.watchersCount)
                Spacer(Modifier.width(indicatorsSpacing))
                Indicator(R.drawable.ic_team, project.members.size)

                if (project.isPrivate) {
                    Spacer(Modifier.width(indicatorsSpacing))
                    Icon(
                        painter = painterResource(R.drawable.ic_key),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
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
