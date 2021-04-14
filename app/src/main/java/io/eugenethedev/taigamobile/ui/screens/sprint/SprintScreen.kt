package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.lists.CommonTasksList
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.*
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalAnimationApi
@Composable
fun SprintScreen(
    navController: NavController,
    sprint: Sprint,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: SprintViewModel = viewModel()
    remember {
        viewModel.start(sprint.id)
        null
    }

    val statuses by viewModel.statuses.observeAsState()
    statuses?.subscribeOnError(onError)

    val stories by viewModel.stories.observeAsState()
    stories?.subscribeOnError(onError)

    val loadingStatusIds by viewModel.loadingStatusIds.observeAsState()
    val visibleStatusIds by viewModel.visibleStatusIds.observeAsState()

    val tasks by viewModel.tasks.observeAsState()
    tasks?.subscribeOnError(onError)

    val issues by viewModel.issues.observeAsState()
    issues?.subscribeOnError(onError)

    SprintScreenContent(
        sprintName = sprint.name,
        start = sprint.start,
        finish = sprint.finish,
        isLoading = statuses?.resultStatus == ResultStatus.LOADING ||
            (tasks?.resultStatus == ResultStatus.LOADING && tasks?.data.isNullOrEmpty()) ||
            (issues?.resultStatus == ResultStatus.LOADING && issues?.data.isNullOrEmpty()),
        startStatusesExpanded = viewModel.startStatusesExpanded,
        statuses = statuses?.data.orEmpty(),
        commonTasks = stories?.data.orEmpty(),
        loadingStatusIds = loadingStatusIds.orEmpty(),
        loadStories = viewModel::loadStories,
        visibleStatusIds = visibleStatusIds.orEmpty(),
        onStatusClick = viewModel::statusClick,
        navigateBack = navController::popBackStack,
        tasks = tasks?.data.orEmpty(),
        isTasksLoading = tasks?.resultStatus == ResultStatus.LOADING,
        loadTasks = viewModel::loadTasks,
        issues = issues?.data.orEmpty(),
        isIssuesLoading = issues?.resultStatus == ResultStatus.LOADING,
        loadIssues = viewModel::loadIssues,
        navigateToTask = navController::navigateToTaskScreen,
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(it, sprintId = sprint.id) }
    )
}

@ExperimentalAnimationApi
@Composable
fun SprintScreenContent(
    sprintName: String,
    start: Date,
    finish: Date,
    isLoading: Boolean = false,
    startStatusesExpanded: Boolean = false,
    statuses: List<Status> = emptyList(),
    commonTasks: List<CommonTask> = emptyList(),
    loadingStatusIds: List<Long> = emptyList(),
    loadStories: (Status) -> Unit = {},
    visibleStatusIds: List<Long> = emptyList(),
    onStatusClick: (Long) -> Unit = {},
    navigateBack: () -> Unit = {},
    tasks: List<CommonTask> = emptyList(),
    isTasksLoading: Boolean = false,
    loadTasks: () -> Unit = {},
    issues: List<CommonTask> = emptyList(),
    isIssuesLoading: Boolean = false,
    loadIssues: () -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> },
    navigateToCreateTask: (CommonTaskType) -> Unit = { _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    val dateFormatter = remember { SimpleDateFormat.getDateInstance() }

    AppBarWithBackButton(
        title = {
            Column {
                Text(
                    text = sprintName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(R.string.sprint_dates_template).format(
                        dateFormatter.format(start),
                        dateFormatter.format(finish)
                    ),
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigateBack = navigateBack
    )

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularLoader()
        }
    } else {
        val sectionsMargin = 18.dp

        LazyColumn(Modifier.fillMaxWidth()) {
            item {
                Row(Modifier.padding(horizontal = mainHorizontalScreenPadding)) {
                    Text(
                        text = stringResource(R.string.stories),
                        style = MaterialTheme.typography.h6,
                    )
                }
            }

            // stories
            CommonTasksList(
                statuses = statuses,
                commonTasks = commonTasks,
                loadingStatusIds = loadingStatusIds,
                visibleStatusIds = visibleStatusIds,
                onStatusClick = onStatusClick,
                loadData = loadStories,
                navigateToTask = navigateToTask,
                isInverseVisibility = startStatusesExpanded
            )

            // tasks
            SimpleTasksListWithTitle(
                titleText = R.string.tasks_without_story,
                topMargin = sectionsMargin,
                horizontalPadding = mainHorizontalScreenPadding,
                commonTasks = tasks,
                isTasksLoading = isTasksLoading,
                navigateToTask = navigateToTask,
                navigateToCreateCommonTask = { navigateToCreateTask(CommonTaskType.TASK) },
                loadData = loadTasks
            )

            // issues
            SimpleTasksListWithTitle(
                titleText = R.string.sprint_issues,
                topMargin = sectionsMargin,
                bottomMargin = 16.dp,
                horizontalPadding = mainHorizontalScreenPadding,
                commonTasks = issues,
                isTasksLoading = isIssuesLoading,
                navigateToTask = navigateToTask,
                navigateToCreateCommonTask = { navigateToCreateTask(CommonTaskType.ISSUE) },
                loadData = loadIssues
            )
        }
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun SprintScreenPreview() = TaigaMobileTheme {
    SprintScreenContent(
        sprintName = "0 sprint",
        start = Date(),
        finish = Date()
    )
}