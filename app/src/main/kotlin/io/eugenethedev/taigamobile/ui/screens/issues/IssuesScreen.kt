package io.eugenethedev.taigamobile.ui.screens.issues

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.FiltersData
import io.eugenethedev.taigamobile.ui.components.TasksFiltersWithLazyList
import io.eugenethedev.taigamobile.ui.components.buttons.PlusButton
import io.eugenethedev.taigamobile.ui.components.appbars.ClickableAppBar
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.commonVerticalPadding
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.*

@Composable
fun IssuesScreen(
    navController: NavController,
    showMessage: (message: Int) -> Unit = {}
) {
    val viewModel: IssuesViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val projectName by viewModel.projectName.collectAsState()

    val issues = viewModel.issues
    issues.subscribeOnError(showMessage)

    val filters by viewModel.filters.collectAsState()
    filters.subscribeOnError(showMessage)

    val activeFilters by viewModel.activeFilters.collectAsState()

    IssuesScreenContent(
        projectName = projectName,
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.Issue) },
        issues = issues,
        filters = filters.data ?: FiltersData(),
        activeFilters = activeFilters,
        selectFilters = viewModel::selectFilters,
        navigateToTask = navController::navigateToTaskScreen
    )
}

@Composable
fun IssuesScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    navigateToCreateTask: () -> Unit = {},
    issues: LazyPagingItems<CommonTask>? = null,
    filters: FiltersData = FiltersData(),
    activeFilters: FiltersData = FiltersData(),
    selectFilters: (FiltersData) -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ClickableAppBar(
        projectName = projectName,
        actions = { PlusButton(onClick = navigateToCreateTask) },
        onTitleClick = onTitleClick
    )

    TasksFiltersWithLazyList(
        filters = filters,
        activeFilters = activeFilters,
        selectFilters = selectFilters
    ) {
        SimpleTasksListWithTitle(
            commonTasksLazy = issues,
            keysHash = activeFilters.hashCode(),
            navigateToTask = navigateToTask,
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
