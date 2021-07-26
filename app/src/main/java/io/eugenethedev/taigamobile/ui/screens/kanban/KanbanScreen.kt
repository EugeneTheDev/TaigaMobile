package io.eugenethedev.taigamobile.ui.screens.kanban

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.navigateToCreateTaskScreen
import io.eugenethedev.taigamobile.ui.utils.navigateToTaskScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@Composable
fun KanbanScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {}
) {
    val viewModel: KanbanViewModel = viewModel()
    remember {
        viewModel.start()
        null
    }

    val swimlanes by viewModel.swimlanes.observeAsState()
    swimlanes?.subscribeOnError(onError)

    val statuses by viewModel.statuses.observeAsState()
    statuses?.subscribeOnError(onError)

    val team by viewModel.team.observeAsState()
    team?.subscribeOnError(onError)

    val stories by viewModel.stories.observeAsState()
    stories?.subscribeOnError(onError)

    val selectedSwimlane by viewModel.selectedSwimlane.observeAsState()

    KanbanScreenContent(
        projectName = viewModel.projectName,
        isLoading = listOf(swimlanes, team, stories).any { it?.resultStatus == ResultStatus.Loading },
        statuses = statuses?.data.orEmpty(),
        stories = stories?.data.orEmpty(),
        team = team?.data.orEmpty(),
        swimlanes = swimlanes?.data.orEmpty(),
        selectSwimlane = viewModel::selectSwimlane,
        selectedSwimlane = selectedSwimlane,
        navigateToStory = { id, ref -> navController.navigateToTaskScreen(id, CommonTaskType.UserStory, ref) },
        onTitleClick = {
            navController.navigate(Routes.projectsSelector)
            viewModel.reset()
        },
        navigateBack = navController::popBackStack,
        navigateToCreateTask = { statusId, swimlaneId ->
            navController.navigateToCreateTaskScreen(CommonTaskType.UserStory, statusId = statusId, swimlaneId = swimlaneId)
        }
    )
}

@Composable
fun KanbanScreenContent(
    projectName: String,
    isLoading: Boolean = false,
    statuses: List<Status> = emptyList(),
    stories: List<CommonTaskExtended> = emptyList(),
    team: List<User> = emptyList(),
    swimlanes: List<Swimlane?> = emptyList(),
    selectSwimlane: (Swimlane?) -> Unit = {},
    selectedSwimlane: Swimlane? = null,
    navigateToStory: (id: Long, ref: Int) -> Unit = { _, _ -> },
    onTitleClick: () -> Unit = {},
    navigateBack: () -> Unit = {},
    navigateToCreateTask: (statusId: Long, swinlanaeId: Long?) -> Unit = { _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ProjectAppBar(
        projectName = projectName,
        onTitleClick = onTitleClick,
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
        KanbanBoard(
            statuses = statuses,
            stories = stories,
            team = team,
            swimlanes = swimlanes,
            selectSwimlane = selectSwimlane,
            selectedSwimlane = selectedSwimlane,
            navigateToStory = navigateToStory,
            navigateToCreateTask = navigateToCreateTask
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KanbanScreenPreview() = TaigaMobileTheme {
    KanbanScreenContent(
        projectName = "Cool project"
    )
}
