package io.eugenethedev.taigamobile.ui.screens.epics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import io.eugenethedev.taigamobile.ui.components.TaskFilters
import io.eugenethedev.taigamobile.ui.components.buttons.PlusButton
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.commonVerticalPadding
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.*

@Composable
fun EpicsScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: EpicsViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val projectName by viewModel.projectName.collectAsState()

    val epics = viewModel.epics
    epics.subscribeOnError(onError)

    val filters by viewModel.filters.collectAsState()
    filters.subscribeOnError(onError)

    val activeFilters by viewModel.activeFilters.collectAsState()

    EpicsScreenContent(
        projectName = projectName,
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.Epic) },
        epics = epics,
        filters = filters.data ?: FiltersData(),
        activeFilters = activeFilters,
        selectFilters = viewModel::selectFilters,
        navigateToTask = navController::navigateToTaskScreen,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EpicsScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    navigateToCreateTask: () -> Unit = {},
    epics: LazyPagingItems<CommonTask>? = null,
    filters: FiltersData = FiltersData(),
    activeFilters: FiltersData = FiltersData(),
    selectFilters: (FiltersData) -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ProjectAppBar(
        projectName = projectName,
        actions = { PlusButton(onClick = navigateToCreateTask) },
        onTitleClick = onTitleClick
    )

    val listState = rememberLazyListState()
    val isVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    AnimatedVisibility(visible = isVisible) {
        TaskFilters(
            selected = activeFilters,
            onSelect = selectFilters,
            data = filters
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        SimpleTasksListWithTitle(
            commonTasksLazy = epics,
            keysHash = activeFilters.hashCode(),
            navigateToTask = navigateToTask,
            horizontalPadding = mainHorizontalScreenPadding,
            bottomPadding = commonVerticalPadding
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun EpicsScreenPreview() = TaigaMobileTheme {
    EpicsScreenContent(
        projectName = "Cool project"
    )
}
