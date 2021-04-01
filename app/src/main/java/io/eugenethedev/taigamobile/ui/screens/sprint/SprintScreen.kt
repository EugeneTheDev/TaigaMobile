package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import io.eugenethedev.taigamobile.ui.components.*
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.NavigateToTask
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.navigateToTaskScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
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

    SprintScreenContent(
        sprintName = sprint.name,
        start = sprint.start,
        finish = sprint.finish,
        isLoading = statuses?.resultStatus == ResultStatus.LOADING || (tasks?.resultStatus == ResultStatus.LOADING && tasks?.data.isNullOrEmpty()),
        isTasksLoading = tasks?.resultStatus == ResultStatus.LOADING,
        statuses = statuses?.data.orEmpty(),
        commonTasks = stories?.data.orEmpty(),
        loadingStatusIds = loadingStatusIds.orEmpty(),
        loadStories = viewModel::loadStories,
        visibleStatusIds = visibleStatusIds.orEmpty(),
        onStatusClick = viewModel::statusClick,
        navigateBack = navController::popBackStack,
        tasks = tasks?.data.orEmpty(),
        loadTasks = viewModel::loadTasks,
        navigateToTask = navController::navigateToTaskScreen
    )
}

@ExperimentalAnimationApi
@Composable
fun SprintScreenContent(
    sprintName: String,
    start: Date,
    finish: Date,
    isLoading: Boolean = false,
    isTasksLoading: Boolean = false,
    statuses: List<Status> = emptyList(),
    commonTasks: List<CommonTask> = emptyList(),
    loadingStatusIds: List<Long> = emptyList(),
    loadStories: (Status) -> Unit = {},
    visibleStatusIds: List<Long> = emptyList(),
    onStatusClick: (Long) -> Unit = {},
    navigateBack: () -> Unit = {},
    tasks: List<CommonTask> = emptyList(),
    loadTasks: () -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> }
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
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {

            item {
                Column(Modifier.padding(horizontal = mainHorizontalScreenPadding)) {
                    Text(
                        text = stringResource(R.string.stories),
                        style = MaterialTheme.typography.h6,
                    )
                }
            }


            CommonTasksList(
                statuses = statuses,
                commonTasks = commonTasks,
                loadingStatusIds = loadingStatusIds,
                visibleStatusIds = visibleStatusIds,
                onStatusClick = onStatusClick,
                loadData = loadStories,
                navigateToTask = navigateToTask
            )

            item {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.tasks_without_story),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(horizontal = mainHorizontalScreenPadding)
                )
            }

            itemsIndexed(tasks) { index, item ->
                CommonTaskItem(
                    commonTask = item,
                    navigateToTask = navigateToTask
                )

                if (index < tasks.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.LightGray
                    )
                }
            }

            item {
                if (isTasksLoading) {
                    DotsLoader()
                } else if (tasks.isEmpty()) {
                    NothingToSeeHereText()
                }

                SideEffect {
                    loadTasks()
                }
                Spacer(Modifier.height(16.dp))
            }
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