package io.eugenethedev.taigamobile.ui.screens.issues

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.buttons.PlusButton
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.components.editors.searchFieldHorizontalPadding
import io.eugenethedev.taigamobile.ui.components.editors.searchFieldVerticalPadding
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.commonVerticalPadding
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.*

@Composable
fun IssuesScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {}
) {
    val viewModel: IssuesViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.start()
    }

    val issues by viewModel.issues.observeAsState()
    issues?.subscribeOnError(onError)

    IssuesScreenContent(
        projectName = viewModel.projectName,
        onTitleClick = {
            navController.navigate(Routes.projectsSelector)
            viewModel.reset()
        },
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.Issue) },
        isLoading = issues?.resultStatus == ResultStatus.Loading,
        issues = issues?.data.orEmpty(),
        navigateToTask = navController::navigateToTaskScreen,
        loadIssues = viewModel::loadIssues
    )
}

@Composable
fun IssuesScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    navigateToCreateTask: () -> Unit = {},
    isLoading: Boolean = false,
    issues: List<CommonTask> = emptyList(),
    navigateToTask: NavigateToTask = { _, _, _ -> },
    loadIssues: (query: String) -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ProjectAppBar(
        projectName = projectName,
        actions = { PlusButton(onClick = navigateToCreateTask) },
        onTitleClick = onTitleClick
    )

    var query by remember { mutableStateOf(TextFieldValue()) }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            TextFieldWithHint(
                hintId = R.string.tasks_search_hint,
                value = query,
                onValueChange = { query = it },
                onSearchClick = { loadIssues(query.text) },
                horizontalPadding = searchFieldHorizontalPadding,
                verticalPadding = searchFieldVerticalPadding,
                hasBorder = true
            )
        }

        SimpleTasksListWithTitle(
            commonTasks = issues,
            navigateToTask = navigateToTask,
            isTasksLoading = isLoading,
            loadData = { loadIssues(query.text) },
            horizontalPadding = mainHorizontalScreenPadding,
            bottomPadding = commonVerticalPadding
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun IssuesScreenPreview() = TaigaMobileTheme {
    IssuesScreenContent(
        projectName = "Cool project"
    )
}
