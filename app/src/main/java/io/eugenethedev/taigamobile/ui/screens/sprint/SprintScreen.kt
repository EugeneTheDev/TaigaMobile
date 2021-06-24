package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@ExperimentalPagerApi
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

    val storiesWithTasks by viewModel.storiesWithTasks.observeAsState()
    storiesWithTasks?.subscribeOnError(onError)

    val storylessTasks by viewModel.storylessTasks.observeAsState()
    storylessTasks?.subscribeOnError(onError)

    val issues by viewModel.issues.observeAsState()
    issues?.subscribeOnError(onError)

    SprintScreenContent(
        sprintName = sprint.name,
        start = sprint.start,
        finish = sprint.finish,
        isLoading = listOf(statuses, storiesWithTasks, storylessTasks, issues).any { it?.resultStatus == ResultStatus.Loading },
        statuses = statuses?.data.orEmpty(),
        storiesWithTasks = storiesWithTasks?.data.orEmpty(),
        storylessTasks = storylessTasks?.data.orEmpty(),
        issues = issues?.data.orEmpty(),
        navigateBack = navController::popBackStack,
        navigateToTask = navController::navigateToTaskScreen,
        navigateToCreateTask = { type, parentId -> navController.navigateToCreateTaskScreen(type, parentId, sprint.id) }
    )
}

@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun SprintScreenContent(
    sprintName: String,
    start: LocalDate,
    finish: LocalDate,
    isLoading: Boolean = false,
    statuses: List<Status> = emptyList(),
    storiesWithTasks: Map<CommonTask, List<CommonTask>> = emptyMap(),
    storylessTasks: List<CommonTask> = emptyList(),
    issues: List<CommonTask> = emptyList(),
    navigateBack: () -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> },
    navigateToCreateTask: (type: CommonTaskType, parentId: Long?) -> Unit = { _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

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
                        start.format(dateFormatter),
                        finish.format(dateFormatter)
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
        SprintKanban(
            statuses = statuses,
            storiesWithTasks = storiesWithTasks,
            storylessTasks = storylessTasks,
            issues = issues,
            navigateToTask = navigateToTask,
            navigateToCreateTask = navigateToCreateTask
        )
    }
}



@ExperimentalPagerApi
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun SprintScreenPreview() = TaigaMobileTheme {
    SprintScreenContent(
        sprintName = "0 sprint",
        start = LocalDate.now(),
        finish = LocalDate.now()
    )
}