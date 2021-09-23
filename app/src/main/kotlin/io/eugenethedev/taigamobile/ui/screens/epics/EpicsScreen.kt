package io.eugenethedev.taigamobile.ui.screens.epics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.components.buttons.PlusButton
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.lists.CommonTaskItem
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
    LaunchedEffect(Unit) {
        viewModel.start()
    }

    val epics = viewModel.epics
    epics.subscribeOnError(onError)

    EpicsScreenContent(
        projectName = viewModel.projectName,
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.Epic) },
        epics = epics,
        navigateToTask = navController::navigateToTaskScreen,
    )
}

@Composable
fun EpicsScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    navigateToCreateTask: () -> Unit = {},
    epics: LazyPagingItems<CommonTask>? = null,
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

    if (epics == null) return@Column

    val isLoading = epics.run { loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading }

    if (epics.itemCount == 0 && !(isLoading || epics.loadState.prepend is LoadState.Loading)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NothingToSeeHereText()
        }
    }
    else {
        LazyColumn(Modifier.fillMaxWidth()) {
            item {
                if (epics.loadState.prepend is LoadState.Loading) {
                    DotsLoader()
                }
            }

            itemsIndexed(epics) { index, item ->
                if (item == null) return@itemsIndexed

                CommonTaskItem(
                    commonTask = item,
                    navigateToTask = navigateToTask
                )

                if (index < epics.itemCount - 1) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.LightGray
                    )
                }
            }

            item {
                if (isLoading) {
                    DotsLoader()
                }

                Spacer(Modifier.height(16.dp))
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
