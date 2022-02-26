package io.eugenethedev.taigamobile.ui.screens.dashboard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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

    DashboardScreenContent(
        isLoading = listOf(workingOn, watching).any { it is LoadingResult<*> },
        workingOn = workingOn.data.orEmpty(),
        watching = watching.data.orEmpty(),
        navigateToTask = {
            viewModel.changeCurrentProject(it)
            navController.navigateToTaskScreen(it.id, it.taskType, it.ref)
        }
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
    navigateToTask: (CommonTask) -> Unit = {_ -> }
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
                    currentProjectId = currentProjectId
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
    currentProjectId: Long
) = LazyColumn {
    items(myProjects) {
        ProjectCard(
            project = it,
            isCurrent = it.id == currentProjectId
        )

        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun ProjectCard(
    project: Project,
    isCurrent: Boolean
) = Surface(
    shape = shapes.medium,
    shadowElevation = cardShadowElevation,
    tonalElevation = if (isCurrent) 8.dp else 0.dp,
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = mainHorizontalScreenPadding)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = {}
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

            Text(
                text = project.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(project.description.orEmpty())

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
        isCurrent = true
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
