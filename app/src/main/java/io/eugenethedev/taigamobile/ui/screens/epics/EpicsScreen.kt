package io.eugenethedev.taigamobile.ui.screens.epics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.buttons.PlusButton
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.lists.CommonTaskItem
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.*

@Composable
fun EpicsScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: EpicsViewModel = viewModel()
    remember {
        viewModel.start()
        null
    }

    val epics by viewModel.epics.observeAsState()
    epics?.subscribeOnError(onError)

    EpicsScreenContent(
        projectName = viewModel.projectName,
        onTitleClick = {
            navController.navigate(Routes.projectsSelector)
            viewModel.reset()
        },
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.Epic) },
        isLoading = epics?.resultStatus == ResultStatus.Loading && epics?.data.isNullOrEmpty(),
        epics = epics?.data.orEmpty(),
        isEpicsLoading = epics?.resultStatus == ResultStatus.Loading,
        navigateToTask = navController::navigateToTaskScreen,
        loadEpics = viewModel::loadEpics
    )
}

@Composable
fun EpicsScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    navigateToCreateTask: () -> Unit = {},
    isLoading: Boolean = false,
    epics: List<CommonTask> = emptyList(),
    isEpicsLoading: Boolean = false,
    navigateToTask: NavigateToTask = { _, _, _ -> },
    loadEpics: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ProjectAppBar(
        projectName = projectName,
        actions = { PlusButton(onClick = navigateToCreateTask) },
        onTitleClick = onTitleClick
    )

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }
        epics.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NothingToSeeHereText()
            }
        }
        else -> {
            LazyColumn(Modifier.fillMaxWidth()) {
                itemsIndexed(epics) { index, item ->
                    CommonTaskItem(
                        commonTask = item,
                        navigateToTask = navigateToTask
                    )

                    if (index < epics.lastIndex) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.LightGray
                        )
                    }
                }

                item {
                    if (isEpicsLoading) {
                        DotsLoader()
                    }

                    LaunchedEffect(epics.size) {
                        loadEpics()
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun EpicsScreenPreview() = TaigaMobileTheme {
    EpicsScreenContent(
        projectName = "Cool project"
    )
}
